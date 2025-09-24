package cace.processos_api.model.deltaSap;


import jakarta.persistence.*;
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
@Table(name = "varas")
public class Varas {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @NotNull
    @Column(name = "nomeVara", unique = true)
    private String nomeVara;


    @NotNull
    @Column(name = "codigoVaraSisbajud", unique = true)
    private long codigoVaraSisbajud;


}
