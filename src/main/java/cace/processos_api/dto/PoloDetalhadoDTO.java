package cace.processos_api.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class PoloDetalhadoDTO {
    private Long id;
    private String nome;
    private String cpfCnpj;
    private LocalDate dataNascimentoParte;
    private String enderecoParte;
    private String enderecoParteCep;
    private String filiacaoParte;
    private String filiacaoParteCpf;
    private Boolean antecedenteCriminal;
    private String descricao;

}
