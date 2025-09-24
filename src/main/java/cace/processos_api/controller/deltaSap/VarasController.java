package cace.processos_api.controller.deltaSap;

import cace.processos_api.dto.deltaSap.VarasRequest;
import cace.processos_api.dto.deltaSap.VarasResponse;
import cace.processos_api.model.deltaSap.Varas;
import cace.processos_api.service.deltaSap.VarasService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/varas")
@AllArgsConstructor
public class VarasController {

    private  final VarasService varasService;


    // Criar varas
    @PostMapping("/criarVara")
    public ResponseEntity<VarasResponse> criarVara(@RequestBody VarasRequest varasRequest){
        return ResponseEntity.ok(varasService.criarVara(varasRequest));
    }


    @PostMapping("criarVarasLote")
    public List<VarasResponse> salvarEmLote(@RequestBody List<VarasRequest> varasRequests){
        return varasService.criarVarasEmLote(varasRequests);
    }


    // Varas Paginadas
    @GetMapping("/varasPaginada")
    public ResponseEntity<Page<VarasResponse>> listarPaginaVaras(@RequestParam int page , @RequestParam int size){
        return ResponseEntity.ok(varasService.listarVarasPaginada(page , size));
    }


    // Buscar todas as Varas
    @GetMapping
    public ResponseEntity<List<VarasResponse>> listarVaras(){
        return ResponseEntity.ok(varasService.listarTodasAsVaras());
    }


    // Listar vara por Nome
    @GetMapping
    public ResponseEntity<VarasResponse> listarVaraPorNome(@RequestParam String nomeVara){
        return ResponseEntity.ok(varasService.buscarVaraPorNome(nomeVara));
    }


    // Deletar vara por ID
    public ResponseEntity<Void> deletarVaraPorId(@PathVariable Integer id){
        varasService.deletarVaraPorId(id);
        return ResponseEntity.ok().build();
    }

}
