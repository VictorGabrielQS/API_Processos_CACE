package cace.processos_api.dto.deltaSap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VarasResponse {
    private Integer id;
    private String nomeVara;
    private Long codigoVaraSisbajud;
}
