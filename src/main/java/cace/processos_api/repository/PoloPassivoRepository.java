package cace.processos_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cace.processos_api.model.process.PoloPassivo;

import java.util.Optional;


public interface PoloPassivoRepository extends JpaRepository<PoloPassivo, Long> {
    Optional<PoloPassivo> findByCpfCnpj(String cpfCnpj);

    Optional<PoloPassivo> findByNome(String nome);
}