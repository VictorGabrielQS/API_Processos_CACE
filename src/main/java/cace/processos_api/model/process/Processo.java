package cace.processos_api.model.process;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "processo")
public class Processo {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    // RESTO DA CLASSE PERMANECE IGUAL:
    @Column(nullable = false , unique = true , updatable = false)
    private String numeroCompleto;

    @Column(nullable = false , unique = true , updatable = false)
    private String numeroCurto;

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