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
    SELECT pp.*, p.nome AS nome, p.cpf_cnpj AS cpf_cnpj
    FROM polo_ativo pp
    JOIN polo p ON pp.id = p.id
    WHERE unaccent(lower(p.nome)) LIKE unaccent(lower(concat('%', :nome, '%')))
    LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<PoloAtivo> searchByNomeAproximadoPaged(@Param("nome") String nome, @Param("limit") int limit, @Param("offset") int offset);

}
 