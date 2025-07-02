package cace.processos_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cace.processos_api.model.process.PoloPassivo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PoloPassivoRepository extends JpaRepository<PoloPassivo, Long> {
    Optional<PoloPassivo> findByCpfCnpj(String cpfCnpj);
    Optional<PoloPassivo> findByNome(String nome);

    @Query(value = """
        SELECT * FROM polo
        WHERE unaccent(lower(nome)) LIKE unaccent(lower(concat('%', :nome, '%')))
        """, nativeQuery = true)
    List<PoloPassivo> searchByNomeAproximado(@Param("nome") String nome);
}