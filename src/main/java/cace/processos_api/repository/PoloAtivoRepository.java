package cace.processos_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cace.processos_api.model.process.PoloAtivo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PoloAtivoRepository extends JpaRepository <PoloAtivo , Long>{

    Optional<PoloAtivo> findByCpfCnpj(String cpfCnpj);

    Optional<PoloAtivo> findByNome(String nome);

    @Query(value = """
    SELECT pa.* FROM polo_ativo pa
    JOIN polo p ON pa.id = p.id
    WHERE unaccent(lower(p.nome)) LIKE unaccent(lower(concat('%', :nome, '%')))
    """, nativeQuery = true)
    List<PoloAtivo> searchByNomeAproximado(@Param("nome") String nome);



}
 