package br.csi.politecnico.financecontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserToken {

    private Long id;
    private String uuid;
    private String name;
    private String email;
    private String cpf;
    private String role;

}
