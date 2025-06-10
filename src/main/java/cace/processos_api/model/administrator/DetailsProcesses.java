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
    private String processosVerificar;

    @Column(nullable = false)
    private String processosRenajud;

    @Column(nullable = false)
    private String processosInfojud;

    @Column(nullable = false)
    private String processosErroCertidao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataHoraCriacao; // Data e hora da criação

    @Column(nullable = false)
    private LocalDateTime dataHoraAtualizacao; // Data e hora da última atualização


    @PrePersist
    protected void onCreate() {
        this.dataHoraCriacao = LocalDateTime.now();
        this.dataHoraAtualizacao = LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {
        this.dataHoraAtualizacao = LocalDateTime.now();
    }



}
