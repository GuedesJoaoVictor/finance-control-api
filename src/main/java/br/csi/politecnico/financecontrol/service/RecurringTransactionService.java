package br.csi.politecnico.financecontrol.service;

import br.csi.politecnico.financecontrol.dto.RecurringTransactionDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.exception.NotFoundException;
import br.csi.politecnico.financecontrol.model.*;
import br.csi.politecnico.financecontrol.repository.*;
import br.csi.politecnico.financecontrol.utils.AuthUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecurringTransactionService {

    private final RecurringTransactionRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BankRepository bankRepository;
    private final RevenuesRepository revenuesRepository;
    private final ExpenseRepository expenseRepository;

    public RecurringTransactionService(RecurringTransactionRepository repository,
                                       UserRepository userRepository,
                                       CategoryRepository categoryRepository,
                                       BankRepository bankRepository,
                                       RevenuesRepository revenuesRepository,
                                       ExpenseRepository expenseRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.bankRepository = bankRepository;
        this.revenuesRepository = revenuesRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<RecurringTransactionDTO> findAll() {
        String uuid = AuthUtil.getUuid();
        return repository.findAllByUser_Uuid(UUID.fromString(uuid))
                .stream().map(RecurringTransactionDTO::new).toList();
    }

    public RecurringTransactionDTO create(RecurringTransactionDTO dto) {
        String uuid = AuthUtil.getUuid();
        User user = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        Category category = categoryRepository.findById(dto.getCategory().getId())
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        Bank bank = bankRepository.findById(dto.getBank().getId().longValue())
                .orElseThrow(() -> new NotFoundException("Banco não encontrado"));

        if (dto.getDay() < 1 || dto.getDay() > 31) {
            throw new BadRequestException("Dia deve estar entre 1 e 31");
        }

        RecurringTransaction rt = RecurringTransaction.builder()
                .description(dto.getDescription())
                .value(dto.getValue())
                .type(dto.getType())
                .day(dto.getDay())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .category(category)
                .bank(bank)
                .user(user)
                .build();

        return new RecurringTransactionDTO(repository.saveAndFlush(rt));
    }

    public RecurringTransactionDTO update(Long id, RecurringTransactionDTO dto) {
        RecurringTransaction rt = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transação recorrente não encontrada"));

        if (dto.getDescription() != null) rt.setDescription(dto.getDescription());
        if (dto.getValue() != null) rt.setValue(dto.getValue());
        if (dto.getType() != null) rt.setType(dto.getType());
        if (dto.getDay() != null) {
            if (dto.getDay() < 1 || dto.getDay() > 31) {
                throw new BadRequestException("Dia deve estar entre 1 e 31");
            }
            rt.setDay(dto.getDay());
        }
        if (dto.getActive() != null) rt.setActive(dto.getActive());
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory().getId())
                    .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
            rt.setCategory(category);
        }
        if (dto.getBank() != null) {
            Bank bank = bankRepository.findById(dto.getBank().getId().longValue())
                    .orElseThrow(() -> new NotFoundException("Banco não encontrado"));
            rt.setBank(bank);
        }

        return new RecurringTransactionDTO(repository.saveAndFlush(rt));
    }

    public void delete(Long id) {
        RecurringTransaction rt = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transação recorrente não encontrada"));
        repository.delete(rt);
    }

    public int apply(int month, int year) {
        String uuid = AuthUtil.getUuid();
        User user = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        List<RecurringTransaction> actives = repository.findAllByUser_UuidAndActiveTrue(UUID.fromString(uuid));
        int created = 0;

        for (RecurringTransaction rt : actives) {
            int day = Math.min(rt.getDay(), YearMonth.of(year, month).lengthOfMonth());
            LocalDate date = LocalDate.of(year, month, day);

            if ("RECEITA".equals(rt.getType())) {
                boolean exists = revenuesRepository.findAllByUser_Uuid(UUID.fromString(uuid))
                        .stream().anyMatch(r -> r.getDescription() != null
                                && r.getDescription().equals(rt.getDescription())
                                && r.getReceiptDate() != null
                                && r.getReceiptDate().equals(date)
                                && r.getValue() != null
                                && r.getValue().compareTo(rt.getValue()) == 0);
                if (exists) continue;

                Revenue revenue = Revenue.builder()
                        .description(rt.getDescription())
                        .value(rt.getValue())
                        .user(user)
                        .category(rt.getCategory())
                        .bank(rt.getBank())
                        .receiptDate(date)
                        .build();
                revenuesRepository.saveAndFlush(revenue);
                created++;
            } else {
                boolean exists = expenseRepository.findAllByUser_Uuid(UUID.fromString(uuid))
                        .stream().anyMatch(e -> e.getDescription() != null
                                && e.getDescription().equals(rt.getDescription())
                                && e.getExpenseDate() != null
                                && e.getExpenseDate().equals(date)
                                && e.getValue() != null
                                && e.getValue().compareTo(rt.getValue()) == 0);
                if (exists) continue;

                Expense expense = Expense.builder()
                        .description(rt.getDescription())
                        .value(rt.getValue())
                        .user(user)
                        .category(rt.getCategory())
                        .bank(rt.getBank())
                        .expenseDate(date)
                        .build();
                expenseRepository.saveAndFlush(expense);
                created++;
            }
        }

        return created;
    }
}
