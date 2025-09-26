package cace.processos_api.dto.deltaSap;

import cace.processos_api.model.deltaSap.Vara;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaraResponseDTO {
    private Integer id;
    private String nomeVara;
    private Long codigoVaraSisbajud;

    public VaraResponseDTO(Vara vara) {
        this.id = vara.getId();
        this.nomeVara = vara.getNomeVara();
        this.codigoVaraSisbajud = vara.getCodigoVaraSisbajud();
    }


}
