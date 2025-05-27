package cace.processos_api.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirstAccessRequest {
    private String token;
    private String senhaAtual;
    private String novaSenha;

}
