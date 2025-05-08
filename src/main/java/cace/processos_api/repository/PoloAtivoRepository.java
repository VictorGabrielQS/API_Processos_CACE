package cace.processos_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cace.processos_api.model.PoloAtivo;

import java.util.Optional;

public interface PoloAtivoRepository extends JpaRepository <PoloAtivo , Long>{

    Optional<PoloAtivo> findByCpfCnpj(String cpfCnpj);

    Optional<PoloAtivo> findByNome(String nome);
}
 