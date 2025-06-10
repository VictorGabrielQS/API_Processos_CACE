package cace.processos_api.repository.administrator;

import cace.processos_api.model.administrator.DetailsProcesses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DetailsProcessesRepository extends JpaRepository<DetailsProcesses, Long> {

    Optional<DetailsProcesses> findByDataHoraCriacaoBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

}
