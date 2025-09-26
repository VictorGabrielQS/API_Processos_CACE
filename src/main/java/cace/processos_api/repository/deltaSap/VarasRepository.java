package cace.processos_api.repository.deltaSap;


import cace.processos_api.model.deltaSap.Varas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VarasRepository extends JpaRepository<Varas,Integer> {

    Optional<Varas> findByNomeVara(String nomeVara);


}
