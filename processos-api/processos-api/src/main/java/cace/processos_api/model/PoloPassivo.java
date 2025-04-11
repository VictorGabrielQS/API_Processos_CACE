package cace.processos_api.model;




import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PoloPassivo extends Polo {

    
    @Column(name = "dataNascimento_parte")
    private LocalDate dataNascimentoParte;

    @Column(name = "endereco_parte")
    private String enderecoParte;

    @Column(name = "filiacao_parte")
    private String filiacaoParte;


}
