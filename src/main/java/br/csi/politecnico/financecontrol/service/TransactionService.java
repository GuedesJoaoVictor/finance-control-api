package br.csi.politecnico.financecontrol.service;

import br.csi.politecnico.financecontrol.dto.*;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.model.*;
import br.csi.politecnico.financecontrol.repository.*;
import br.csi.politecnico.financecontrol.utils.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final RevenuesRepository revenuesRepository;
    private final UserRepository userRepository;
    private final BankRepository bankRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    public TransactionService(RevenuesRepository revenuesRepository, UserRepository userRepository, BankRepository bankRepository, CategoryRepository categoryRepository, ExpenseRepository expenseRepository) {
        this.revenuesRepository = revenuesRepository;
        this.userRepository = userRepository;
        this.bankRepository = bankRepository;
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<RevenueDTO> findAll() {
        List<Revenue> revenues = revenuesRepository.findAll();
        List<RevenueDTO> dtos = new ArrayList<>();
        if (revenues.isEmpty()) {
            throw new BadRequestException("Nenhuma transação encontrada.");
        }
        for (Revenue revenue : revenues) {
            RevenueDTO dto = RevenueDTO.builder()
                    .id(revenue.getId())
                    .user(new UserDTO(revenue.getUser()))
                    .bank(new BankDTO(revenue.getBank()))
                    .category(new CategoryDTO(revenue.getCategory()))
                    .value(revenue.getValue())
                    .description(revenue.getDescription())
                    .receiptDate(revenue.getReceiptDate())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public RevenueDTO createRevenue(RevenueDTO revenue) {
        User user = userRepository.findById(AuthUtil.getId()).orElse(null);
        if (user == null) {
            throw new BadRequestException("Usuário não encontrado.");
        }
        Bank bank = bankRepository.findById(Long.valueOf(revenue.getBank().getId())).orElse(null);
        if (bank == null) {
            throw new BadRequestException("Banco não encontrado.");
        }
        Category category = categoryRepository.findById(revenue.getCategory().getId()).orElse(null);
        if (category == null) {
            throw new BadRequestException("Categoria não encontrada.");
        }
        if (revenue.getValue().intValue() < 0) {
            throw new BadRequestException("Valor da transação não pode ser negativo.");
        }

        Revenue revenueEntity = Revenue.builder()
                .user(user)
                .bank(bank)
                .category(category)
                .value(revenue.getValue())
                .description(revenue.getDescription())
                .receiptDate(revenue.getReceiptDate())
                .build();
        return new RevenueDTO(revenuesRepository.saveAndFlush(revenueEntity));
    }

    public RevenueDTO findRevenueById(Long id) {
        Revenue revenue = revenuesRepository.findById(id).orElse(null);
        if (revenue == null) {
            throw new BadRequestException("Transação não encontrada.");
        }
        return RevenueDTO.builder()
                .id(revenue.getId())
                .user(new UserDTO(revenue.getUser()))
                .bank(new BankDTO(revenue.getBank()))
                .category(new CategoryDTO(revenue.getCategory()))
                .value(revenue.getValue())
                .description(revenue.getDescription())
                .receiptDate(revenue.getReceiptDate())
                .build();
    }

    public RevenueDTO updateRevenueById(Long id, RevenueDTO revenue) {
        Revenue revenueEntity = revenuesRepository.findById(id).orElse(null);
        if (revenueEntity == null) {
            throw new BadRequestException("Transação não encontrada.");
        }
        Bank bank = bankRepository.findById(Long.valueOf(revenue.getBank().getId())).orElse(null);
        if (bank == null) {
            throw new BadRequestException("Banco não encontrado.");
        }
        Category category = categoryRepository.findById(revenue.getCategory().getId()).orElse(null);
        if (category == null) {
            throw new BadRequestException("Categoria não encontrada.");
        }
        if (revenue.getValue().intValue() < 0) {
            throw new BadRequestException("Valor da transação não pode ser negativo.");
        }

        revenueEntity.setBank(bank);
        revenueEntity.setCategory(category);
        revenueEntity.setValue(revenue.getValue());
        revenueEntity.setDescription(revenue.getDescription());
        revenueEntity.setReceiptDate(revenue.getReceiptDate());
        return new RevenueDTO(revenuesRepository.saveAndFlush(revenueEntity));
    }

    public Boolean deleteRevenueById(Long id) {
        Revenue revenue = revenuesRepository.findById(id).orElse(null);
        if (revenue == null) {
            throw new BadRequestException("Transação não encontrada.");
        }
        revenuesRepository.deleteById(revenue.getId());
        return true;
    }

    public List<RevenueDTO> findAllRevenueByUserUuid(String uuid) {
        List<Revenue> revenues = revenuesRepository.findAllByUser_Uuid(UUID.fromString(uuid));
        if (revenues.isEmpty()) {
            return new ArrayList<>();
        }
        List<RevenueDTO> dtos = new ArrayList<>();
        for (Revenue revenue : revenues) {
            RevenueDTO dto = RevenueDTO.builder()
                    .id(revenue.getId())
                    .user(new UserDTO(revenue.getUser()))
                    .bank(new BankDTO(revenue.getBank()))
                    .category(new CategoryDTO(revenue.getCategory()))
                    .value(revenue.getValue())
                    .description(revenue.getDescription())
                    .receiptDate(revenue.getReceiptDate())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public List<ExpenseDTO> findAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        List<ExpenseDTO> dtos = new ArrayList<>();
        if (expenses.isEmpty()) {
            throw new BadRequestException("Nenhuma despesa encontrada.");
        }
        for (Expense expense : expenses) {
            ExpenseDTO dto = ExpenseDTO.builder()
                    .id(expense.getId())
                    .user(new UserDTO(expense.getUser()))
                    .bank(new BankDTO(expense.getBank()))
                    .category(new CategoryDTO(expense.getCategory()))
                    .value(expense.getValue())
                    .description(expense.getDescription())
                    .expenseDate(expense.getExpenseDate())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public ExpenseDTO createExpense(ExpenseDTO expense) {
        User user = userRepository.findById(AuthUtil.getId()).orElse(null);
        if (user == null) {
            throw new BadRequestException("Usuário não encontrado.");
        }
        Bank bank = bankRepository.findById(Long.valueOf(expense.getBank().getId())).orElse(null);
        if (bank == null) {
            throw new BadRequestException("Banco não encontrado.");
        }
        Category category = categoryRepository.findById(expense.getCategory().getId()).orElse(null);
        if (category == null) {
            throw new BadRequestException("Categoria não encontrada.");
        }
        if (expense.getValue().intValue() < 0) {
            throw new BadRequestException("Valor da despesa não pode ser negativo.");
        }
        Expense expenseEntity = Expense.builder()
                .user(user)
                .bank(bank)
                .category(category)
                .value(expense.getValue())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .build();
        return new ExpenseDTO(expenseRepository.saveAndFlush(expenseEntity));
    }

    public ExpenseDTO updateExpenseById(Long id, ExpenseDTO expense) {
        Expense expenseEntity = expenseRepository.findById(id).orElse(null);
        if (expenseEntity == null) {
            throw new BadRequestException("Despesa não encontrada.");
        }
        Bank bank = bankRepository.findById(Long.valueOf(expense.getBank().getId())).orElse(null);
        if (bank == null) {
            throw new BadRequestException("Banco não encontrado.");
        }
        Category category = categoryRepository.findById(expense.getCategory().getId()).orElse(null);
        if (category == null) {
            throw new BadRequestException("Categoria não encontrada.");
        }
        if (expenseEntity.getValue().intValue() < 0) {
            throw new BadRequestException("Valor da despesa não pode ser negativo.");
        }
        expenseEntity.setBank(bank);
        expenseEntity.setCategory(category);
        expenseEntity.setValue(expense.getValue());
        expenseEntity.setDescription(expense.getDescription());
        expenseEntity.setExpenseDate(expense.getExpenseDate());

        return new ExpenseDTO(expenseRepository.saveAndFlush(expenseEntity));
    }

    public Boolean deleteExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id).orElse(null);
        if (expense == null) {
            throw new BadRequestException("Despesa não encontrada.");
        }
        expenseRepository.deleteById(expense.getId());
        return true;
    }

    public List<ExpenseDTO> findAllExpensesByUserUuid(String uuid) {
        List<Expense> expenses = expenseRepository.findAllByUser_Uuid(UUID.fromString(uuid));
        if (expenses.isEmpty()) {
            return new ArrayList<>();
        }
        List<ExpenseDTO> dtos = new ArrayList<>();
        for (Expense expense : expenses) {
            ExpenseDTO dto = ExpenseDTO.builder()
                    .id(expense.getId())
                    .user(new UserDTO(expense.getUser()))
                    .bank(new BankDTO(expense.getBank()))
                    .category(new CategoryDTO(expense.getCategory()))
                    .value(expense.getValue())
                    .description(expense.getDescription())
                    .expenseDate(expense.getExpenseDate())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public List<RevenueDTO> findAllRevenueByBankId(Long bankId) {
        String userUuid = AuthUtil.getUuid();
        List<Revenue> revenues = revenuesRepository.findAllByBank_IdAndUser_Uuid(bankId, UUID.fromString(userUuid));
        return revenues.stream().map(RevenueDTO::new).toList();
    }

    public List<ExpenseDTO> findAllExpenseByBankId(Long bankId) {
        String userUuid = AuthUtil.getUuid();
        List<Expense> expenses = expenseRepository.findAllByBank_IdAndUser_Uuid(bankId, UUID.fromString(userUuid));
        return expenses.stream().map(ExpenseDTO::new).toList();
    }

    public void deleteAllByBankId(Long bankId) {
        String userUuid = AuthUtil.getUuid();
        revenuesRepository.deleteAllByBank_IdAndUser_Uuid(bankId, UUID.fromString(userUuid));
        expenseRepository.deleteAllByBank_IdAndUser_Uuid(bankId, UUID.fromString(userUuid));
    }

}
