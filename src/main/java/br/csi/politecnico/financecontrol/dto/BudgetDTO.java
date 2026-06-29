package br.csi.politecnico.financecontrol.dto;

import br.csi.politecnico.financecontrol.model.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {
    private Long id;
    private CategoryDTO category;
    private Integer month;
    private Integer year;
    private BigDecimal limitAmount;
    private BigDecimal spent;

    public BudgetDTO(Budget budget) {
        this.id = budget.getId();
        this.category = new CategoryDTO(budget.getCategory());
        this.month = budget.getMonth();
        this.year = budget.getYear();
        this.limitAmount = budget.getLimitAmount();
    }

    public BudgetDTO(Budget budget, BigDecimal spent) {
        this(budget);
        this.spent = spent;
    }
}
