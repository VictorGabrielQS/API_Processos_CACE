package cace.processos_api.repository.administrator;

import cace.processos_api.model.administrator.DetailsProcesses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailsProcessesRepository  extends JpaRepository<DetailsProcesses, Long> {
}
