package cace.processos_api.repository.administrator;

import cace.processos_api.model.administrator.DetailsProcesses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


public interface DetailsProcessesRepository extends JpaRepository<DetailsProcesses, Long> {

    List<DetailsProcesses> findByDataHoraCriacaoBetween(LocalDateTime start, LocalDateTime end);

    List<DetailsProcesses> findByDataHoraCriacaoBetweenOrderByDataHoraAtualizacaoDesc(LocalDateTime inicio, LocalDateTime fim);

}

