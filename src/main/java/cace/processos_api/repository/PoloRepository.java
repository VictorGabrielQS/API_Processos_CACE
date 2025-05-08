package cace.processos_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cace.processos_api.model.Polo;

public interface PoloRepository extends JpaRepository<Polo , Long> {

    Optional<Polo> findByCpfCnpj(String cpfCnpj);

    Optional<Polo> findByNome(String nome);
    
    boolean existsByCpfCnpj(String cpfCnpj);

    void deleteByCpfCnpj(String cpfCnpj);
    
}
