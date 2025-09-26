package cace.processos_api.dto.deltaSap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaraRequestDTO {

    @NotBlank(message = "O nome da vara é Obrigatório ...")
    private String nomeVara;

    @NotNull(message = "O codigo da vara é Obrigatório ...")
    private Long codigoVaraSisbajud;


}
