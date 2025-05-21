package cace.processos_api.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponseException extends RuntimeException {
    private String mensagem;
    private Object data;


    public ApiResponseException(String mensagem , Object data){
        this.mensagem = mensagem;
        this.data = data;
    }


}
