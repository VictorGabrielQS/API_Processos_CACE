package cace.processos_api.controller.administrator;

import cace.processos_api.dto.administrator.DetailsProcessesDTO;
import cace.processos_api.service.administrator.DetailsProcessesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/details-processes")
public class DetailsProcessesController {

    @Autowired
    private DetailsProcessesService service;

    @GetMapping
    public List<DetailsProcessesDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public DetailsProcessesDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Detalhe não encontrado com ID: " + id));
    }

    @PostMapping
    public DetailsProcessesDTO salvar(@RequestBody DetailsProcessesDTO dto) {
        return service.salvar(dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}