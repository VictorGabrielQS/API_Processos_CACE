package cace.processos_api.controller.administrator;

import cace.processos_api.dto.administrator.DetailsProcessesDTO;
import cace.processos_api.model.administrator.DetailsProcesses;
import cace.processos_api.repository.administrator.DetailsProcessesRepository;
import cace.processos_api.service.administrator.DetailsProcessesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@RestController
@RequestMapping("/details-processes")
public class DetailsProcessesController {


    @Autowired
    private DetailsProcessesRepository detailsProcessesRepository;


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
    public DetailsProcessesDTO salvar(@RequestBody DetailsProcessesDTO detailsProcessesDTO) {
        LocalDate dataCriacao = detailsProcessesDTO.getDataHoraCriacao() != null
                ? detailsProcessesDTO.getDataHoraCriacao().toLocalDate()
                : LocalDate.now();

        LocalDateTime startOfDay = dataCriacao.atStartOfDay();
        LocalDateTime endOfDay = dataCriacao.atTime(LocalTime.MAX);

        List<DetailsProcesses> listaRegistros = detailsProcessesRepository.findByDataHoraCriacaoBetween(startOfDay, endOfDay);

        DetailsProcesses detailsProcesses;

        if (!listaRegistros.isEmpty()) {
            // Atualiza o primeiro registro encontrado do dia
            detailsProcesses = listaRegistros.get(0);
        } else {
            // Cria novo registro
            detailsProcesses = new DetailsProcesses();
            LocalDateTime dataHoraCriacao = detailsProcessesDTO.getDataHoraCriacao() != null
                    ? detailsProcessesDTO.getDataHoraCriacao()
                    : LocalDateTime.now();
            detailsProcesses.setDataHoraCriacao(dataHoraCriacao);
        }

        // Atualiza dados
        detailsProcesses.setProcessosVerificar(detailsProcessesDTO.getProcessosVerificar());
        detailsProcesses.setProcessosRenajud(detailsProcessesDTO.getProcessosRenajud());
        detailsProcesses.setProcessosInfojud(detailsProcessesDTO.getProcessosInfojud());
        detailsProcesses.setProcessosErroCertidao(detailsProcessesDTO.getProcessosErroCertidao());
        detailsProcesses.setDataHoraAtualizacao(LocalDateTime.now());

        DetailsProcesses salvo = detailsProcessesRepository.save(detailsProcesses);

        return new DetailsProcessesDTO(salvo);
    }



    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}