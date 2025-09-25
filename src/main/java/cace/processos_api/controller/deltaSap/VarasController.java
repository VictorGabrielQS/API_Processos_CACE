package cace.processos_api.controller.deltaSap;

import cace.processos_api.dto.deltaSap.VarasRequest;
import cace.processos_api.dto.deltaSap.VarasResponse;

import cace.processos_api.service.deltaSap.VarasService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/varas")
@AllArgsConstructor
public class VarasController {

    private final VarasService varasService;

    // Criar Vara
    @PostMapping("/criar")
    public ResponseEntity<VarasResponse> criarVara(@Valid @RequestBody VarasRequest varasRequest) {
        return ResponseEntity.ok(varasService.criarVara(varasRequest));
    }


    // Criar Varas em lote
    @PostMapping("/criar-lote")
    public ResponseEntity<List<VarasResponse>> salvarEmLote(@Valid @RequestBody List<VarasRequest> varasRequests) {
        return ResponseEntity.ok(varasService.criarVarasEmLote(varasRequests));
    }

    // Listar Varas paginadas
    @GetMapping("/paginado")
    public ResponseEntity<Page<VarasResponse>> listarPaginaVaras(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(varasService.listarVarasPaginada(page, size));
    }

    // Listar todas as Varas
    @GetMapping("/todas")
    public ResponseEntity<List<VarasResponse>> listarVaras() {
        return ResponseEntity.ok(varasService.listarTodasAsVaras());
    }

    // Buscar Vara por nome
    @GetMapping("/buscar")
    public ResponseEntity<VarasResponse> listarVaraPorNome(@RequestParam String nomeVara) {
        return ResponseEntity.ok(varasService.buscarVaraPorNome(nomeVara));
    }

    // Deletar Vara por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVaraPorId(@PathVariable Integer id) {
        varasService.deletarVaraPorId(id);
        return ResponseEntity.noContent().build();
    }
}
