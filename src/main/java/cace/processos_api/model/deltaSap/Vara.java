package cace.processos_api.model.deltaSap;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "varas")
public class Vara {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome_vara", nullable = false, unique = true)
    private String nomeVara;

    @Column(name = "codigo_vara_sisbajud", nullable = false, unique = true)
    private Long codigoVaraSisbajud;
}
