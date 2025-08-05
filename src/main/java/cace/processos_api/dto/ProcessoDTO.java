package cace.processos_api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import cace.processos_api.model.process.Processo;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id; // <--- ALTERADO

    private String numeroCompleto;
    private String numeroCurto;
    private String serventia;
    private String status;
    private String responsavel;
    private String descricao;
    private String certidao;
    private String urlProcessoProjudi;

    private String dataCriacao;
    private String dataAtualizacao;

    public static String formatarData(LocalDateTime data) {
        if (data == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return data.format(formatter);
    }

    public static ProcessoDTO fromEntity(Processo processo) {
        return new ProcessoDTO(
                processo.getId(),
                processo.getNumeroCompleto(),
                processo.getNumeroCurto(),
                processo.getServentia(),
                processo.getStatus(),
                processo.getResponsavel(),
                processo.getDescricao(),
                processo.getTipoCertidao(),
                processo.getUrlProcessoProjudi(),
                formatarData(processo.getDataCriacao()),
                formatarData(processo.getDataAtualizacao())
        );
    }
}
