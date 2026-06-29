package br.csi.politecnico.financecontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DashboardDTO {
    private BigDecimal totalBalance;
    private BigDecimal totalRevenues;
    private BigDecimal totalExpenses;
    private List<BankBalanceDTO> bankBalances;
    private List<RecentTransactionDTO> recentTransactions;
    private List<MonthlySummaryDTO> monthlySummary;
    private List<BudgetDTO> budgets;

    @Data
    @Builder
    @AllArgsConstructor
    public static class MonthlySummaryDTO {
        private int year;
        private int month;
        private BigDecimal revenues;
        private BigDecimal expenses;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class BankBalanceDTO {
        private Integer bankId;
        private String bankName;
        private BigDecimal balance;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class RecentTransactionDTO {
        private Long id;
        private String description;
        private BigDecimal value;
        private String type;
        private LocalDate date;
        private String bankName;
        private String categoryName;
    }
}
