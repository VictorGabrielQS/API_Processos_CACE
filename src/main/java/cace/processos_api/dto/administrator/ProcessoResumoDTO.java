package cace.processos_api.dto.administrator;


import java.time.LocalDateTime;

public interface ProcessoResumoDTO {
    String getNumeroCurto();
    String getStatus();
    LocalDateTime getDataCriacao();
}
