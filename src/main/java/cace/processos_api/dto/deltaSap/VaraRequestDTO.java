package cace.processos_api.dto.deltaSap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaraRequestDTO {
    private String nomeVara;
    private Long codigoVaraSisbajud;
}
