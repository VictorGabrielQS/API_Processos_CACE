package cace.processos_api.controller;

import cace.processos_api.dto.PoloDTO;
import cace.processos_api.service.PoloAtivoService;
import cace.processos_api.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/polos-ativos")
public class PoloAtivoController {
    private final PoloAtivoService poloAtivoService;

    public PoloAtivoController(PoloAtivoService poloAtivoService) {
        this.poloAtivoService = poloAtivoService;
    }

    @PostMapping
    public ResponseEntity<PoloDTO> createPoloAtivo (@RequestBody PoloDTO poloDTO){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        PoloDTO createdPolo = poloAtivoService.createPoloAtivo(poloDTO);
        return new ResponseEntity<>(createdPolo , HttpStatus.CREATED);
    }

    @GetMapping
    public  ResponseEntity<List<PoloDTO>> getAllPolosAtivos(){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        List<PoloDTO> polos = poloAtivoService.getAllPolosAtivos();
        return ResponseEntity.ok(polos);
    }

    @GetMapping("/cpf-cnpj")
    public ResponseEntity<PoloDTO> getPoloAtivoByCpfCnpj(@RequestParam String cpfCnpj ){
        PoloDTO polo = poloAtivoService.getPoloAtivoByCpfCnpj(cpfCnpj);
        return ResponseEntity.ok(polo);
    }

    @GetMapping("/nome")
    public ResponseEntity<PoloDTO> getPoloAtivoByNome(@RequestParam String nome ) {
          PoloDTO polo = poloAtivoService.getPoloAtivoByNome(nome);
        return ResponseEntity.ok(polo);
    }


    @PutMapping("updateByCpfCnpj/{cpfCnpj}")
    public ResponseEntity<PoloDTO> updatePoloAtivoCpfCnpj(@PathVariable String cpfCnpj , @RequestBody PoloDTO poloDTO ){
           AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
           PoloDTO updatedPolo = poloAtivoService.updatePolo(cpfCnpj , poloDTO);
           return  ResponseEntity.ok(updatedPolo);
    }



    @PutMapping("updateById/{id}")
    public ResponseEntity<PoloDTO> updatePoloAtivoId(@PathVariable Long id , @RequestBody PoloDTO poloDTO ){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        PoloDTO updatedPolo = poloAtivoService.updatePoloId(id , poloDTO);
        return  ResponseEntity.ok(updatedPolo);
    }


}
