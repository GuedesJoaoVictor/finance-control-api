package br.csi.politecnico.financecontrol.service;

import br.csi.politecnico.financecontrol.dto.BudgetDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.exception.NotFoundException;
import br.csi.politecnico.financecontrol.model.Budget;
import br.csi.politecnico.financecontrol.model.Category;
import br.csi.politecnico.financecontrol.model.Expense;
import br.csi.politecnico.financecontrol.model.User;
import br.csi.politecnico.financecontrol.repository.BudgetRepository;
import br.csi.politecnico.financecontrol.repository.CategoryRepository;
import br.csi.politecnico.financecontrol.repository.ExpenseRepository;
import br.csi.politecnico.financecontrol.repository.UserRepository;
import br.csi.politecnico.financecontrol.utils.AuthUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         CategoryRepository categoryRepository,
                         UserRepository userRepository,
                         ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<BudgetDTO> getBudgets(Integer month, Integer year) {
        String uuid = AuthUtil.getUuid();
        List<Budget> budgets;

        if (month != null && year != null) {
            budgets = budgetRepository.findAllByUser_Uuid(UUID.fromString(uuid))
                    .stream()
                    .filter(b -> b.getMonth().equals(month) && b.getYear().equals(year))
                    .collect(Collectors.toList());
        } else {
            budgets = budgetRepository.findAllByUser_Uuid(UUID.fromString(uuid));
        }

        List<Expense> expenses = expenseRepository.findAllByUser_Uuid(UUID.fromString(uuid));

        return budgets.stream().map(b -> {
            BigDecimal spent = expenses.stream()
                    .filter(e -> e.getCategory() != null
                            && e.getCategory().getId().equals(b.getCategory().getId())
                            && e.getExpenseDate() != null
                            && e.getExpenseDate().getMonthValue() == b.getMonth()
                            && e.getExpenseDate().getYear() == b.getYear())
                    .map(e -> e.getValue() != null ? e.getValue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return new BudgetDTO(b, spent);
        }).collect(Collectors.toList());
    }

    public BudgetDTO create(BudgetDTO dto) {
        String uuid = AuthUtil.getUuid();
        User user = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Category category = categoryRepository.findById(dto.getCategory().getId())
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        Optional<Budget> existing = budgetRepository
                .findByCategory_IdAndUser_UuidAndMonthAndYear(
                        dto.getCategory().getId(), UUID.fromString(uuid), dto.getMonth(), dto.getYear());
        if (existing.isPresent()) {
            throw new BadRequestException("Já existe um orçamento para esta categoria no período");
        }

        Budget budget = Budget.builder()
                .category(category)
                .user(user)
                .month(dto.getMonth())
                .year(dto.getYear())
                .limitAmount(dto.getLimitAmount())
                .build();

        return new BudgetDTO(budgetRepository.saveAndFlush(budget));
    }

    public BudgetDTO update(Long id, BudgetDTO dto) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Orçamento não encontrado"));

        if (dto.getLimitAmount() != null) {
            budget.setLimitAmount(dto.getLimitAmount());
        }

        return new BudgetDTO(budgetRepository.saveAndFlush(budget));
    }

    public void delete(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Orçamento não encontrado"));
        budgetRepository.delete(budget);
    }
}
