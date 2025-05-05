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

    @Column(name = "endereco_parte_cep")
    private String enderecoParteCep;

    @Column(name = "filiacao_parte")
    private String filiacaoParte;

    @Column(name = "filiacao_parte_cpf" )
    private String filiacaoParteCpf;


    @Column(name = "antecedente_criminal")
    private Boolean antecedenteCriminal;

    @Column(name = "descricao")
    private String descricao;


}
