package br.csi.politecnico.financecontrol.service;

import br.csi.politecnico.financecontrol.dto.BankDTO;
import br.csi.politecnico.financecontrol.dto.BudgetDTO;
import br.csi.politecnico.financecontrol.dto.DashboardDTO;
import br.csi.politecnico.financecontrol.dto.ExpenseDTO;
import br.csi.politecnico.financecontrol.dto.RevenueDTO;
import br.csi.politecnico.financecontrol.model.Expense;
import br.csi.politecnico.financecontrol.model.Revenue;
import br.csi.politecnico.financecontrol.model.Budget;
import br.csi.politecnico.financecontrol.repository.BudgetRepository;
import br.csi.politecnico.financecontrol.repository.ExpenseRepository;
import br.csi.politecnico.financecontrol.repository.RevenuesRepository;
import br.csi.politecnico.financecontrol.repository.UserBankRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DashboardService {

    private final RevenuesRepository revenuesRepository;
    private final ExpenseRepository expenseRepository;
    private final UserBankRepository userBankRepository;
    private final BankService bankService;
    private final BudgetRepository budgetRepository;

    public DashboardService(RevenuesRepository revenuesRepository,
                            ExpenseRepository expenseRepository,
                            UserBankRepository userBankRepository,
                            BankService bankService,
                            BudgetRepository budgetRepository) {
        this.revenuesRepository = revenuesRepository;
        this.expenseRepository = expenseRepository;
        this.userBankRepository = userBankRepository;
        this.bankService = bankService;
        this.budgetRepository = budgetRepository;
    }

    public DashboardDTO getDashboard(String uuid) {
        UUID userUuid = UUID.fromString(uuid);

        List<Revenue> revenues = revenuesRepository.findAllByUser_Uuid(userUuid);
        List<Expense> expenses = expenseRepository.findAllByUser_Uuid(userUuid);

        BigDecimal totalRevenues = revenues.stream()
                .map(Revenue::getValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBalance = totalRevenues.subtract(totalExpenses);

        List<BankDTO> userBanks = bankService.findUserBankByUuid(uuid);
        Map<Integer, BigDecimal> bankBalances = new HashMap<>();

        for (Revenue rev : revenues) {
            if (rev.getBank() != null) {
                Integer bankId = Math.toIntExact(rev.getBank().getId());
                bankBalances.merge(bankId, rev.getValue() != null ? rev.getValue() : BigDecimal.ZERO, BigDecimal::add);
            }
        }
        for (Expense exp : expenses) {
            if (exp.getBank() != null) {
                Integer bankId = Math.toIntExact(exp.getBank().getId());
                bankBalances.merge(bankId, exp.getValue() != null ? exp.getValue().negate() : BigDecimal.ZERO, BigDecimal::add);
            }
        }

        List<DashboardDTO.BankBalanceDTO> bankBalanceDTOs = userBanks.stream()
                .map(bank -> DashboardDTO.BankBalanceDTO.builder()
                        .bankId(bank.getId())
                        .bankName(bank.getName())
                        .balance(bankBalances.getOrDefault(bank.getId(), BigDecimal.ZERO))
                        .build())
                .collect(Collectors.toList());

        List<DashboardDTO.RecentTransactionDTO> recentRevenues = revenues.stream()
                .map(r -> DashboardDTO.RecentTransactionDTO.builder()
                        .id(r.getId())
                        .description(r.getDescription())
                        .value(r.getValue())
                        .type("RECEITA")
                        .date(r.getReceiptDate())
                        .bankName(r.getBank() != null ? r.getBank().getName() : null)
                        .categoryName(r.getCategory() != null ? r.getCategory().getName() : null)
                        .build())
                .toList();

        List<DashboardDTO.RecentTransactionDTO> recentExpenses = expenses.stream()
                .map(e -> DashboardDTO.RecentTransactionDTO.builder()
                        .id(e.getId())
                        .description(e.getDescription())
                        .value(e.getValue())
                        .type("DESPESA")
                        .date(e.getExpenseDate())
                        .bankName(e.getBank() != null ? e.getBank().getName() : null)
                        .categoryName(e.getCategory() != null ? e.getCategory().getName() : null)
                        .build())
                .toList();

        List<DashboardDTO.RecentTransactionDTO> allTransactions = Stream.concat(
                recentRevenues.stream(), recentExpenses.stream()
        ).sorted(Comparator.comparing(DashboardDTO.RecentTransactionDTO::getDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());

        Map<Integer, DashboardDTO.MonthlySummaryDTO> monthlyMap = new TreeMap<>(Comparator.reverseOrder());
        for (Revenue rev : revenues) {
            if (rev.getReceiptDate() == null) continue;
            int year = rev.getReceiptDate().getYear();
            int month = rev.getReceiptDate().getMonthValue();
            int key = year * 100 + month;
            monthlyMap.merge(key,
                    DashboardDTO.MonthlySummaryDTO.builder()
                            .year(year).month(month)
                            .revenues(rev.getValue() != null ? rev.getValue() : BigDecimal.ZERO)
                            .expenses(BigDecimal.ZERO)
                            .build(),
                    (a, b) -> DashboardDTO.MonthlySummaryDTO.builder()
                            .year(a.getYear()).month(a.getMonth())
                            .revenues(a.getRevenues().add(b.getRevenues()))
                            .expenses(a.getExpenses().add(b.getExpenses()))
                            .build());
        }
        for (Expense exp : expenses) {
            if (exp.getExpenseDate() == null) continue;
            int year = exp.getExpenseDate().getYear();
            int month = exp.getExpenseDate().getMonthValue();
            int key = year * 100 + month;
            monthlyMap.merge(key,
                    DashboardDTO.MonthlySummaryDTO.builder()
                            .year(year).month(month)
                            .revenues(BigDecimal.ZERO)
                            .expenses(exp.getValue() != null ? exp.getValue() : BigDecimal.ZERO)
                            .build(),
                    (a, b) -> DashboardDTO.MonthlySummaryDTO.builder()
                            .year(a.getYear()).month(a.getMonth())
                            .revenues(a.getRevenues().add(b.getRevenues()))
                            .expenses(a.getExpenses().add(b.getExpenses()))
                            .build());
        }

        List<Budget> budgets = budgetRepository.findAllByUser_Uuid(userUuid);
        List<BudgetDTO> budgetDTOs = budgets.stream().map(b -> {
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

        return DashboardDTO.builder()
                .totalBalance(totalBalance)
                .totalRevenues(totalRevenues)
                .totalExpenses(totalExpenses)
                .bankBalances(bankBalanceDTOs)
                .recentTransactions(allTransactions)
                .monthlySummary(new ArrayList<>(monthlyMap.values()))
                .budgets(budgetDTOs)
                .build();
    }
}
