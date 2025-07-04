package cace.processos_api.dto.administrator;

import java.time.LocalDateTime;

public interface RelatorioProcessoDTO {
    LocalDateTime getDataCriacao();
    String getNumeroCompleto();
    String getServentia();
    String getResponsavel();
    String getStatus();
    String getPoloAtivoNome();
    String getPoloPassivoNome();
}