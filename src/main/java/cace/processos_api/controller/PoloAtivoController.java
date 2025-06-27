package cace.processos_api.controller;

import cace.processos_api.dto.PoloDTO;
import cace.processos_api.dto.ResponseDTO;
import cace.processos_api.model.process.PoloAtivo;
import cace.processos_api.repository.PoloAtivoRepository;
import cace.processos_api.service.PoloAtivoService;
import cace.processos_api.util.AuthUtil;
import cace.processos_api.util.CpfCnpjUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<ResponseDTO<PoloDTO>> getPoloAtivoByCpfCnpj(@RequestParam String cpfCnpj) {
        ResponseDTO<PoloDTO> resposta = poloAtivoService.getPoloAtivoByCpfCnpj(cpfCnpj);
        return ResponseEntity.ok(resposta);
    }



    @GetMapping("/nome")
    public ResponseEntity<ResponseDTO<PoloDTO>> getPoloAtivoByNome(@RequestParam String nome) {
        ResponseDTO<PoloDTO> response = poloAtivoService.getPoloAtivoByNome(nome);
        return ResponseEntity.ok(response);
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
