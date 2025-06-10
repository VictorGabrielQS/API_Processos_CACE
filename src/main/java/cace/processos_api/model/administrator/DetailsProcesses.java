package cace.processos_api.model.administrator;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "details_processes")
@Data
public class DetailsProcesses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer processosVerificar;   // Quantidade de processos a verificar

    @Column(nullable = false)
    private Integer processosRenajud;     // Quantidade de processos Renajud

    @Column(nullable = false)
    private Integer processosInfojud;     // Quantidade de processos Infojud

    @Column(nullable = false)
    private Integer processosErroCertidao; // Quantidade de processos com erro

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataHoraCriacao;

    @Column(nullable = false)
    private LocalDateTime dataHoraAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataHoraCriacao = LocalDateTime.now();
        dataHoraAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataHoraAtualizacao = LocalDateTime.now();
    }
}
