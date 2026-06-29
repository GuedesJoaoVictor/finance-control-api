package br.csi.politecnico.financecontrol.dto;

import br.csi.politecnico.financecontrol.model.RecurringTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringTransactionDTO {
    private Long id;
    private String description;
    private BigDecimal value;
    private String type;
    private Integer day;
    private Boolean active;
    private CategoryDTO category;
    private BankDTO bank;

    public RecurringTransactionDTO(RecurringTransaction rt) {
        this.id = rt.getId();
        this.description = rt.getDescription();
        this.value = rt.getValue();
        this.type = rt.getType();
        this.day = rt.getDay();
        this.active = rt.getActive();
        this.category = new CategoryDTO(rt.getCategory());
        this.bank = new BankDTO(rt.getBank());
    }
}
