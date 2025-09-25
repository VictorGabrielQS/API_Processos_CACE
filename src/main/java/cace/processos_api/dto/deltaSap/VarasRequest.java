package cace.processos_api.dto.deltaSap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VarasRequest {

    @NotBlank
    private String nomeVara;

    @NotNull
    private Long codigoVaraSisbajud;


}
