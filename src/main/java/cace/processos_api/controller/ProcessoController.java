package cace.processos_api.controller;


import cace.processos_api.dto.ProcessoDTO;
import cace.processos_api.dto.administrator.ProcessoResumoDTO;
import cace.processos_api.service.ProcessoService;
import cace.processos_api.util.AuthUtil;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/processos")
public class ProcessoController {
    private final ProcessoService processoService;

    public ProcessoController(ProcessoService processoService){
        this.processoService = processoService;
    }




    //Criar Processo :

    // Rota para Criar Processo
    @PostMapping
    public ResponseEntity<ProcessoDTO> createProcesso(@RequestBody ProcessoDTO processoDTO){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        ProcessoDTO createdProcesso = processoService.createProcesso(processoDTO);
        return  new ResponseEntity<>(createdProcesso , HttpStatus.CREATED);
    }



    //Busca de Processos :


    // Rota para Buscar Todos os Processos
    @GetMapping
    public ResponseEntity<List<ProcessoDTO>> getAllProcessos(){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        List<ProcessoDTO> processos = processoService.getAllProcessos();
        return ResponseEntity.ok(processos);
    }


    // Rota para Buscar Todos os Processos por uma determinada Data
    @GetMapping("/resumo-por-data")
    public ResponseEntity<List<ProcessoResumoDTO>> getResumoPorData(@RequestParam String data) {
        AuthUtil.validarAcesso(1);
        List<ProcessoResumoDTO> resumo = processoService.getResumoPorData(data);
        return resumo.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resumo);
    }






    // Rota para Buscar Processos por número curto
    @GetMapping("/por-curto/{numeroCurto}")
    public ResponseEntity<ProcessoDTO> getProcessoByNumeroCurto(@PathVariable String numeroCurto){
        ProcessoDTO processo = processoService.getProcessoByNumeroCurto(numeroCurto);
        return processo != null ? ResponseEntity.ok(processo) : ResponseEntity.notFound().build();
    }


    // Rotas para Buscar Processos por número completo
    @GetMapping("/por-completo/{numeroCompleto}")
    public ResponseEntity<ProcessoDTO> getProcessoByNumeroCompleto(@PathVariable String numeroCompleto){
        ProcessoDTO processo = processoService.getProcessoByNumeroCompleto(numeroCompleto);
        return ResponseEntity.ok(processo);
    }




    //Atualizar Processo :

    // Rota para atualizar Processo por número curto
    @PutMapping("/por-curto/{numeroCurto}")
    public ResponseEntity<ProcessoDTO> updateProcessoNumeroCurto(@PathVariable String numeroCurto , @RequestBody ProcessoDTO processoDTO){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        ProcessoDTO updateProcesso = processoService.updateProcessoByNumeroCurto(numeroCurto , processoDTO);
        return ResponseEntity.ok(updateProcesso);
    }


    // Rota para atualizar Processo por número completo
    @PutMapping("/por-completo/{numeroCompleto}")
    public ResponseEntity<ProcessoDTO> updateProcessoNumeroCompleto(@PathVariable String numeroCompleto , @RequestBody ProcessoDTO processoDTO){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        ProcessoDTO updateProcesso = processoService.updateProcessoByNumeroCompleto(numeroCompleto , processoDTO);
        return ResponseEntity.ok(updateProcesso);
    }




}
