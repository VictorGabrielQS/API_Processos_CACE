package cace.processos_api.dto.administrator;


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

}
