package cace.processos_api.dto;

import lombok.Data;

@Data
public class FirstAccessRequest {
    private String token;
    private String senhaAtual;
    private String novaSenha;
}
