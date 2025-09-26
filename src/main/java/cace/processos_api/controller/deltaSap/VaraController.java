package cace.processos_api.controller.deltaSap;

import cace.processos_api.dto.deltaSap.VaraRequestDTO;
import cace.processos_api.dto.deltaSap.VaraResponseDTO;
import cace.processos_api.service.deltaSap.VaraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/varas")
@RequiredArgsConstructor
public class VaraController {
    private final VaraService varaService;



    // Criar Vara
    @PostMapping
    public ResponseEntity<VaraResponseDTO> criarVara(@RequestBody VaraRequestDTO varaRequestDTO){
        return ResponseEntity.ok(varaService.criarVara(varaRequestDTO));
    }



    // Criar Lista de Varas
    @PostMapping("/lista")
    public ResponseEntity<List<VaraResponseDTO>> criarListaVaras(@RequestBody List<VaraRequestDTO> varaRequestDTOS){
        return ResponseEntity.ok(varaService.criarListaVara(varaRequestDTOS));
    }



    // Listar Varas Paginadas
    @GetMapping
    public ResponseEntity<Page<VaraResponseDTO>> listarVaraPaginada(@RequestParam int page , @RequestParam int size ){
        return ResponseEntity.ok(varaService.listarVarasPaginadas(page,size));
    }



    // Listar Vara por Nome
    @GetMapping("/busca/nome")
    public ResponseEntity<VaraResponseDTO> buscarVaraPorNome(@RequestParam String nomeVara){
        return ResponseEntity.ok(varaService.buscarVaraPorNome(nomeVara));
    }



    // Lista Vara por Codigo da Vara
    @GetMapping("/busca/codigo")
    public ResponseEntity<VaraResponseDTO> buscarVaraPorCodigo(@RequestParam Long codigoVaraSisbajud){
        return ResponseEntity.ok(varaService.buscarVaraPorCodigoVara(codigoVaraSisbajud));
    }



    // Deletar Vara por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVara(@PathVariable Integer id){
        varaService.deletarVara(id);
        return ResponseEntity.ok().build();
    }



    // Atualizar Vara
    @PutMapping("/{id}")
    public ResponseEntity<VaraResponseDTO> atualizarVara(
            @PathVariable Integer id ,
            @RequestBody VaraRequestDTO varaRequestDTO ){
        return ResponseEntity.ok(varaService.atualizarVara( id , varaRequestDTO ));
    }




}


