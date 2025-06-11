package cace.processos_api.dto.administrator;


import cace.processos_api.model.administrator.DetailsProcesses;
import cace.processos_api.util.FormatUtil;
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
    private int processosTotais;
    private double percentualErros;
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraAtualizacao;

    private String dataHoraCriacaoFormatada;
    private String dataHoraAtualizacaoFormatada;

    // ✅ Construtor padrão usado pelo método salvar()
    public DetailsProcessesDTO(DetailsProcesses entity) {
        this.id = entity.getId();
        this.processosVerificar = entity.getProcessosVerificar();
        this.processosRenajud = entity.getProcessosRenajud();
        this.processosInfojud = entity.getProcessosInfojud();
        this.processosErroCertidao = entity.getProcessosErroCertidao();
        this.processosTotais = entity.getProcessosTotais();
        this.percentualErros = entity.getPercentualErros();
        this.dataHoraCriacao = entity.getDataHoraCriacao();
        this.dataHoraAtualizacao = entity.getDataHoraAtualizacao();

        this.dataHoraCriacaoFormatada = FormatUtil.formatarData(dataHoraCriacao);
        this.dataHoraAtualizacaoFormatada = FormatUtil.formatarData(dataHoraAtualizacao);
    }

    // ✅ Construtor completo (opcionalmente usado por toDTO)
    public DetailsProcessesDTO(Long id,
                               Integer processosVerificar,
                               Integer processosRenajud,
                               Integer processosInfojud,
                               Integer processosErroCertidao,
                               int processosTotais,
                               double percentualErros,
                               LocalDateTime dataHoraCriacao,
                               LocalDateTime dataHoraAtualizacao) {
        this.id = id;
        this.processosVerificar = processosVerificar;
        this.processosRenajud = processosRenajud;
        this.processosInfojud = processosInfojud;
        this.processosErroCertidao = processosErroCertidao;
        this.processosTotais = processosTotais;
        this.percentualErros = percentualErros;
        this.dataHoraCriacao = dataHoraCriacao;
        this.dataHoraAtualizacao = dataHoraAtualizacao;

        this.dataHoraCriacaoFormatada = FormatUtil.formatarData(dataHoraCriacao);
        this.dataHoraAtualizacaoFormatada = FormatUtil.formatarData(dataHoraAtualizacao);
    }


    // Getters...

    public String getDataHoraCriacaoFormatada() {
        return dataHoraCriacaoFormatada;
    }

    public String getDataHoraAtualizacaoFormatada() {
        return dataHoraAtualizacaoFormatada;
    }
}