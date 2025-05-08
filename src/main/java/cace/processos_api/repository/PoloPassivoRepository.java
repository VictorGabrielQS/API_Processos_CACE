package cace.processos_api.repository;

import cace.processos_api.model.PoloAtivo;
import org.springframework.data.jpa.repository.JpaRepository;

import cace.processos_api.model.PoloPassivo;

import java.util.Optional;


public interface PoloPassivoRepository extends JpaRepository<PoloPassivo, Long> {
    Optional<PoloPassivo> findByCpfCnpj(String cpfCnpj);

    Optional<PoloPassivo> findByNome(String nome);
}