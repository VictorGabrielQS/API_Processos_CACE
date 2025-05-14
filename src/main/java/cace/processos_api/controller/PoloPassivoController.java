package cace.processos_api.controller;

import cace.processos_api.dto.PoloDTO;
import cace.processos_api.service.PoloPassivoService;
import cace.processos_api.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/polos-passivos")
public class PoloPassivoController {
    private  final PoloPassivoService poloPassivoService;

    public PoloPassivoController(PoloPassivoService poloPassivoService){
        this.poloPassivoService = poloPassivoService;
    }

    @PostMapping
    public ResponseEntity<PoloDTO> createPoloPassivo (@RequestBody PoloDTO poloDTO){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        PoloDTO createPolo = poloPassivoService.createPoloPassivo(poloDTO);
        return  new ResponseEntity<>(createPolo , HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PoloDTO>> getAllPolosPassivos(){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        List<PoloDTO> polos = poloPassivoService.getAllPolosPassivos();
        return ResponseEntity.ok(polos);
    }

    @GetMapping("/cpf-cnpj")
    public ResponseEntity<PoloDTO> getPoloPassivoByCpfCnpj(@RequestParam String cpfCnpj ){
           PoloDTO polo = poloPassivoService.getPoloPassivoByCpfCnpj(cpfCnpj);
           return ResponseEntity.ok(polo);
    }


    @GetMapping("/nome")
    public ResponseEntity <PoloDTO> getPoloAtivoByNome(@RequestParam String nome ) {
        PoloDTO polo = poloPassivoService.getPoloPassivoByNome(nome);
        return ResponseEntity.ok(polo);
    }



    @PutMapping("updateByCpfCnpj/{cpfCnpj}")
    public ResponseEntity<PoloDTO> updatePoloPassivoCpfCnpj(@PathVariable String cpfCnpj , @RequestBody PoloDTO poloDTO ){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        PoloDTO updatedPolo = poloPassivoService.updatePolo(cpfCnpj , poloDTO);
        return ResponseEntity.ok(updatedPolo);
    }


    @PutMapping("updateById/{id}")
    public ResponseEntity<PoloDTO> updatePoloPassivoId(@PathVariable Long id , @RequestBody PoloDTO poloDTO ){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        PoloDTO updatedPolo = poloPassivoService.updatePoloId(id , poloDTO);
        return ResponseEntity.ok(updatedPolo);
    }




}
