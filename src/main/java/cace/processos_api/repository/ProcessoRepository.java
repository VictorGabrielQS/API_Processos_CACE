package cace.processos_api.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import cace.processos_api.model.process.Processo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProcessoRepository extends JpaRepository <Processo , Long>{

    // Busca por número curto
    Optional<Processo> findByNumeroCurto(String numeroCurto);

    // Busca por número completo
    Optional<Processo> findByNumeroCompleto(String numeroCompleto);

    // Verifica existência por número curto
    boolean existsByNumeroCurto(String numeroCurto);

    // Verifica existência por número completo
    boolean existsByNumeroCompleto(String numeroCompleto);

    // Delete por número curto
    void deleteByNumeroCurto(String numeroCurto);

    // Delete por número completo
    void deleteByNumeroCompleto(String numeroCompleto);


    //Retorna todos os processos com o PoloAtivo
    @Query("SELECT p FROM Processo p WHERE p.poloAtivo.cpfCnpj = :cpfCnpj")
    List<Processo> findAllProcessosByPoloAtivoCpfCnpj(@Param("cpfCnpj") String cpfCnpj);


    //Retorna todos os processos com o PoloPassivo
    @Query("SELECT p FROM Processo p WHERE p.poloPassivo.cpfCnpj = :cpfCnpj")
    List<Processo> findAllProcessosByPoloPassivoCpfCnpj(@Param("cpfCnpj") String cpfCnpj);


    //Retorna todos os processos com o determinado Responsavel
    List<Processo> findAllProcessosByResponsavel(String responsavel);


    List<Processo> findAllProcessosByStatus(String status);

    List<Processo> findAllByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Processo> findAllProcessosByServentia(String serventia);

    List<Processo> findAllProcessosByTipoCertidao(String tipoCertidao);

    @EntityGraph(attributePaths = {"poloAtivo", "poloPassivo"})
    List<Processo> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);



}
