package cace.processos_api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import lombok.*;



@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Processo {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false , unique = true , updatable = false)
    private String numeroCompleto;


    @Column(nullable = false , unique = true , updatable = false)
    private String numeroCurto;


    @ManyToOne
    @JoinColumn(name = "polo_passivo_id" , nullable = false )
    private PoloAtivo poloAtivo;


    @ManyToOne
    @JoinColumn(name = "polo_ativo_id" , nullable =  false)
    private PoloPassivo poloPassivo;


    @Column(nullable = false)
    private String serventia;


    @Column(nullable = false)
    private String status;


    @Column(nullable = false)
    private String responsavel;


    @Column(columnDefinition = "TEXT")
    private String descricao;


    @Column(nullable = false)
    private String tipoCertidao;

    @Column(nullable = true)
    private String urlProcessoProjudi;


    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();


    @Column
    private LocalDateTime dataAtualizacao;


    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
        System.out.println("Processo atualizado ... " + "na data : " + dataAtualizacao);
    }

    public void setNumeroCompleto(String numeroCompleto) {
        this.numeroCompleto = numeroCompleto;
        if (numeroCompleto != null && numeroCompleto.length() >= 9) {
            this.numeroCurto = numeroCompleto.substring(0, 9);
        }
    }

    


}
