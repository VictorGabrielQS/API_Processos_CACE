package cace.processos_api.controller;

import cace.processos_api.dto.PoloDTO;
import cace.processos_api.dto.ProcessoDTO;
import cace.processos_api.service.ProcessoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ProcessoDTO> createProcesso(@RequestBody ProcessoDTO processoDTO){
        ProcessoDTO createdProcesso = processoService.createProcesso(processoDTO);
        return  new ResponseEntity<>(createdProcesso , HttpStatus.CREATED);
    }



    //Busca de Processos :


    // Rota para Buscar Todos os Processos
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<ProcessoDTO>> getAllProcessos(){
        List<ProcessoDTO> processos = processoService.getAllProcessos();
        return ResponseEntity.ok(processos);
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


    // Rota para Buscar Todos os Processos com um determinado PoloAtivo pelo cpf/cnpj
    @GetMapping("/por-polo-ativo/{cpfCnpj}")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByPoloAtivo(@PathVariable String cpfCnpj){
        List<ProcessoDTO> processos = processoService.getProcessosByPoloAtivoCpfCnpj(cpfCnpj);

        if(processos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(processos);

    }


    // Rota para Buscar Todos os Processos com um determinado PoloPassivo pelo cpf/cnpj
    @GetMapping("/por-polo-passivo/{cpfCnpj}")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByPoloPassivo(@PathVariable String cpfCnpj){
        List<ProcessoDTO> processos = processoService.getProcessosByPoloPassivoCpfCnpj(cpfCnpj);

        if(processos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(processos);

    }



    //Atualizar Processo :

    // Rota para atualizar Processo por número curto
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/por-curto/{numeroCurto}")
    public ResponseEntity<ProcessoDTO> updateProcessoNumeroCurto(@PathVariable String numeroCurto , @RequestBody ProcessoDTO processoDTO){
        ProcessoDTO updateProcesso = processoService.updateProcessoByNumeroCurto(numeroCurto , processoDTO);
        return ResponseEntity.ok(updateProcesso);
    }


    // Rota para atualizar Processo por número completo
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/por-completo/{numeroCompleto}")
    public ResponseEntity<ProcessoDTO> updateProcessoNumeroCompleto(@PathVariable String numeroCompleto , @RequestBody ProcessoDTO processoDTO){
        ProcessoDTO updateProcesso = processoService.updateProcessoByNumeroCompleto(numeroCompleto , processoDTO);
        return ResponseEntity.ok(updateProcesso);
    }




}
