package br.csi.politecnico.financecontrol.service;

import br.csi.politecnico.financecontrol.dto.BankDTO;
import br.csi.politecnico.financecontrol.dto.UserBankDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.exception.NotFoundException;
import br.csi.politecnico.financecontrol.model.Bank;
import br.csi.politecnico.financecontrol.model.User;
import br.csi.politecnico.financecontrol.model.UserBank;
import br.csi.politecnico.financecontrol.repository.BankRepository;
import br.csi.politecnico.financecontrol.repository.UserBankRepository;
import br.csi.politecnico.financecontrol.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BankService {

    private final BankRepository bankRepository;
    private final UserRepository userRepository;
    private final UserBankRepository userBankRepository;
    private final TransactionService transactionService;

    public BankService(BankRepository bankRepository, UserRepository userRepository, UserBankRepository userBankRepository, TransactionService transactionService) {
        this.bankRepository = bankRepository;
        this.userRepository = userRepository;
        this.userBankRepository = userBankRepository;
        this.transactionService = transactionService;
    }

    public BankDTO create(BankDTO dto) {
        Bank bank = bankRepository.findBankByName(dto.getName()).orElse(null);
        if (bank != null) {
            throw new BadRequestException("O banco já existe");
        }
        if (validateInputsBank(dto)) {
            throw new BadRequestException("Os campos devem ser preenchidos");
        }

        bank = Bank.builder()
                .name(dto.getName())
                .type(dto.getType())
                .build();
        bankRepository.saveAndFlush(bank);

        return new BankDTO(bank);
    }

    private boolean validateInputsBank(BankDTO dto) {
        if (dto.getName() == null || dto.getType() == null) {
            return true;
        }
        return dto.getName().isEmpty() || dto.getType().isEmpty();
    }

    public List<BankDTO> findAll() {
        List<Bank> banks = bankRepository.findAll();
        List<BankDTO> dtos = new ArrayList<>();
        if (banks.isEmpty()) {
            throw new NotFoundException("Nenhum banco encontrado.");
        }

        for (Bank bank : banks) {
            BankDTO dto = BankDTO.builder()
                    .id(Math.toIntExact(bank.getId()))
                    .name(bank.getName())
                    .type(bank.getType())
                    .build();
            dtos.add(dto);
        }

        return dtos;
    }

    public BankDTO findById(Long id) {
        Optional<Bank> bank = bankRepository.findById(id);
        if (bank.isPresent()) {
            Bank bankEntity = bank.get();
            return BankDTO.builder()
                    .id(Math.toIntExact(bankEntity.getId()))
                    .name(bankEntity.getName())
                    .type(bankEntity.getType())
                    .build();
        }

        throw new NotFoundException("Nenhum banco encontrado.");
    }

    public String deleteById(Long id) {
        Bank bank = bankRepository.findById(id).orElse(null);
        if (bank == null) {
            throw new NotFoundException("Nenhum banco encontrado.");
        }

        bankRepository.delete(bank);
        return "Banco excluido com sucesso";
    }

    public BankDTO update(Long id, BankDTO dto) {
        Bank bank = bankRepository.findById(id).orElse(null);
        if (bank == null) {
            throw new NotFoundException("Nenhum banco encontrado.");
        }
        bank.setName(dto.getName());
        bank.setType(dto.getType());
        bankRepository.saveAndFlush(bank);

        return BankDTO.builder()
                .id(Math.toIntExact(bank.getId()))
                .name(bank.getName())
                .type(bank.getType())
                .build();
    }

    public UserBankDTO vinculateUserBank(String uuidUsuario, UserBankDTO dto) {
        User user = userRepository.findByUuid(UUID.fromString(uuidUsuario)).orElse(null);
        if (user == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }
        Bank bank = bankRepository.findById(Long.valueOf(dto.getBank().getId())).orElse(null);
        if (bank == null) {
            throw new NotFoundException("Banco não encontrado.");
        }
        UserBank alreadyExists = userBankRepository.findUserBankByBank_IdAndUser_Id(user.getId(), bank.getId());
        if (alreadyExists != null) {
            throw new BadRequestException("O usuário já está vinculado a esse banco.");
        }

        UserBank userBank = UserBank.builder()
                .bank(bank)
                .user(user)
                .totalAmount(dto.getTotalAmount())
                .name(dto.getName())
                .build();
        return new UserBankDTO(userBankRepository.saveAndFlush(userBank));
    }

    public Boolean deleteUserBankById(Long id) {
        UserBank userBank = userBankRepository.findById(id).orElse(null);
        if (userBank == null) {
            throw new NotFoundException("Vínculo não encontrado.");
        }
        transactionService.deleteAllByBankId(userBank.getBank().getId());
        userBankRepository.delete(userBank);
        return true;
    }

    public List<BankDTO> findUserBankByUuid(String uuid) {
        List<BankDTO> banks = userBankRepository.findALlBanksByUserUuid(UUID.fromString(uuid));

        if (banks.isEmpty()) {
            return Collections.emptyList();
        }

        return banks;
    }

    public List<UserBankDTO> findAllLinks() {
        List<UserBank> userBanks = userBankRepository.findAll();
        List<UserBankDTO> dtos = new ArrayList<>();
        for (UserBank userBank : userBanks) {
            dtos.add(new UserBankDTO(userBank));
        }
        return dtos;
    }

    public List<BankDTO> findAllBanksByUserUuid(String uuid) {
        List<Bank> banks = bankRepository.findAllBanksByUserUuid(UUID.fromString(uuid));
        List<BankDTO> dtos = banks.stream().map(BankDTO::from).toList();
        System.out.println(dtos);
        return dtos;
    }

}
