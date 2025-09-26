package cace.processos_api.model.deltaSap;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "varas",

        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nome_vara"}),
                @UniqueConstraint(columnNames = {"codigo_vara_sisbajud"})
        }

)
public class Vara {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(name = "nome_vara", nullable = false, unique = true)
    private String nomeVara;

    @NotNull
    @Column(name = "codigo_vara_sisbajud", nullable = false, unique = true)
    private Long codigoVaraSisbajud;
}
