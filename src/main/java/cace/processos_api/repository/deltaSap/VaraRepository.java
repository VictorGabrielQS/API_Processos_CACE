package cace.processos_api.repository.deltaSap;


import cace.processos_api.model.deltaSap.Vara;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface VaraRepository extends JpaRepository<Vara, Integer> {

    Optional<Vara> findByNomeVara(String nomeVara);
    Optional<Vara> findByCodigoVaraSisbajud(Long codigoVaraSisbajud);

}
