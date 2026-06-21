package br.csi.politecnico.financecontrol.dto;

import br.csi.politecnico.financecontrol.model.Bank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDTO {
    private Integer id;
    private String name;
    private String type;
    private Integer vinculoId;

    public BankDTO(Bank bank) {
        this.id = Math.toIntExact(bank.getId());
        this.name = bank.getName();
        this.type = bank.getType();
    }

    public static BankDTO from(Bank bank) {
        return BankDTO.builder()
                .id(bank.getId().intValue())
                .name(bank.getName())
                .type(bank.getType())
                .build();
    }
}
