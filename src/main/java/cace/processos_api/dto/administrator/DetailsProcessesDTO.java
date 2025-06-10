package cace.processos_api.dto.administrator;


import cace.processos_api.model.administrator.DetailsProcesses;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsProcessesDTO {

    private Long id;
    private Integer processosVerificar;
    private Integer processosRenajud;
    private Integer processosInfojud;
    private Integer processosErroCertidao;
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraAtualizacao;


    public DetailsProcessesDTO(DetailsProcesses entity) {
        this.id = entity.getId();
        this.processosVerificar = entity.getProcessosVerificar();
        this.processosRenajud = entity.getProcessosRenajud();
        this.processosInfojud = entity.getProcessosInfojud();
        this.processosErroCertidao = entity.getProcessosErroCertidao();
        this.dataHoraCriacao = entity.getDataHoraCriacao();
        this.dataHoraAtualizacao = entity.getDataHoraAtualizacao();
    }


}
