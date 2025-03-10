package tech.buildrun.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.api.model.Processo;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/processos")
public class ApiController {

    //Lista de processos
    private List<Processo> processos = new ArrayList<>();


    //Metodo Get

    @GetMapping
    public ResponseEntity<List<Processo>> getProcessos()  {
        return ResponseEntity.ok(processos);
    }


    //Metodo Post
    @PostMapping
    public ResponseEntity<Void> createProcesso(@RequestBody Processo processo) {


        // Verifica se já existe um processo com o mesmo número
        boolean existeProcessoComNumero = processos.stream()
                .anyMatch(p -> p.getNumero().equals(processo.getNumero()));


        // Retorna um erro 400 (Bad Request) se o processo com o número já existir
        if (existeProcessoComNumero) {
            return ResponseEntity.badRequest().build();
        }

        // Define a data atual no campo 'ultimaAlteracao'
        processo.setUltimaAlteracao(LocalDate.now());

        // Adiciona o processo se o número não for duplicado
        processos.add(processo);
        return ResponseEntity.ok().build();
    }



    //Metodo Delete
    @DeleteMapping
    public  ResponseEntity<Void> deleteProcesso(@RequestBody Processo processo){

            if (processo.getNumero() != null) {
                //Percorre a Lista de processos utilizando a função getNumero e remove o processo que ouver o numeroCompleto determinado
                processos.removeIf(p -> p.getNumero().equals(processo.getNumero()));

            } else if (processo.getNumeroCurto() != null) {
                //Percorre a Lista de processos utilizando a função getNumeroCurto e remove o processo que ouver o NumeroCurto determinado
                processos.removeIf(p -> p.getNumeroCurto().equals(processo.getNumeroCurto()));

            } else {
                return ResponseEntity.badRequest().build();
            }

        return ResponseEntity.ok().build();

    }


    //Metodo Put
    @PutMapping
    public ResponseEntity<Processo> updateProcesso(@RequestBody Processo processo){

        // Verifica se o número ou número curto foi fornecido para localizar o processo
        if (processo.getNumero() != null) {

            Processo processoExistente = processos.stream()
                    .filter(p -> p.getNumero().equals(processo.getNumero()))
                    .findFirst()
                    .orElse(null);

            if (processoExistente == null ){
                // Retorna 404 caso o processo não seja encontrado
                return ResponseEntity.notFound().build();
            }


            // Atualiza somente os campos que foram enviados
            if (processo.getDataEntradaProjudi() != null) {
                processoExistente.setDataEntradaProjudi(processo.getDataEntradaProjudi());
            }

            if (processo.getDataEntradaSapjud() != null) {
                processoExistente.setDataEntradaSapjud(processo.getDataEntradaSapjud());
            }

            if (processo.getConsultor() != null) {
                processoExistente.setConsultor(processo.getConsultor());
            }

            if (processo.getPrazo() != 0) {
                processoExistente.setPrazo(processo.getPrazo());
            }

            if (processo.getSituacao() != null) {
                processoExistente.setSituacao(processo.getSituacao());
            }

            if (processo.getDescricao() != null) {
                processoExistente.setDescricao(processo.getDescricao());
            }

            // 🔹 Atualiza a data da última alteração
            processoExistente.setUltimaAlteracao(LocalDate.now());

            return ResponseEntity.ok(processoExistente);

        } else if (processo.getNumeroCurto() != null) {

            Processo processoExistente = processos.stream()
                    .filter(p -> p.getNumeroCurto().equals(processo.getNumeroCurto()))
                    .findFirst()
                    .orElse(null);

            if (processoExistente == null) {
                return ResponseEntity.notFound().build(); // Retorna 404 se não encontrar
            }

            // Atualiza somente os campos que foram enviados
            if (processo.getDataEntradaProjudi() != null) {
                processoExistente.setDataEntradaProjudi(processo.getDataEntradaProjudi());
            }

            if (processo.getDataEntradaSapjud() != null) {
                processoExistente.setDataEntradaSapjud(processo.getDataEntradaSapjud());
            }

            if (processo.getConsultor() != null) {
                processoExistente.setConsultor(processo.getConsultor());
            }

            if (processo.getPrazo() != 0) {
                processoExistente.setPrazo(processo.getPrazo());
            }

            if (processo.getSituacao() != null) {
                processoExistente.setSituacao(processo.getSituacao());
            }

            if (processo.getDescricao() != null) {
                processoExistente.setDescricao(processo.getDescricao());
            }

            // 🔹 Atualiza a data da última alteração
            processoExistente.setUltimaAlteracao(LocalDate.now());

            return ResponseEntity.ok(processoExistente);

        }else {
            // Retorna 400 caso não tenha um número ou número curto
            return ResponseEntity.badRequest().build();
        }

    }



}
