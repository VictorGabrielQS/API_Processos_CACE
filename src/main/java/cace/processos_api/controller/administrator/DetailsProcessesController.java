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
import java.util.Optional;

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

        // Extrai a data da criação (considerando a data passada ou a atual se nula)
        LocalDate dataCriacao = detailsProcessesDTO.getDataHoraCriacao() != null ? detailsProcessesDTO.getDataHoraCriacao().toLocalDate() : LocalDate.now();

        // Define o intervalo do dia (início e fim)
        LocalDateTime startOfDay = dataCriacao.atStartOfDay();
        LocalDateTime endOfDay = dataCriacao.atTime(LocalTime.MAX);

        // Busca registro já existente para o mesmo dia
        Optional<DetailsProcesses> optionalEntity = detailsProcessesRepository.findByDataHoraCriacaoBetween(startOfDay, endOfDay);

        DetailsProcesses detailsProcesses;

        if (optionalEntity.isPresent()) {
            // Atualiza registro existente
            detailsProcesses = optionalEntity.get();

            detailsProcesses.setProcessosVerificar(detailsProcessesDTO.getProcessosVerificar());
            detailsProcesses.setProcessosRenajud(detailsProcessesDTO.getProcessosRenajud());
            detailsProcesses.setProcessosInfojud(detailsProcessesDTO.getProcessosInfojud());
            detailsProcesses.setProcessosErroCertidao(detailsProcessesDTO.getProcessosErroCertidao());

            // Atualiza timestamp da atualização
            detailsProcesses.setDataHoraAtualizacao(LocalDateTime.now());

        } else {

            // Cria novo registro
            detailsProcesses = new DetailsProcesses();

            detailsProcesses.setProcessosVerificar(detailsProcessesDTO.getProcessosVerificar());
            detailsProcesses.setProcessosRenajud(detailsProcessesDTO.getProcessosRenajud());
            detailsProcesses.setProcessosInfojud(detailsProcessesDTO.getProcessosInfojud());
            detailsProcesses.setProcessosErroCertidao(detailsProcessesDTO.getProcessosErroCertidao());

            // Define dataHoraCriacao e dataHoraAtualizacao com o horário atual
            LocalDateTime now = LocalDateTime.now();
            detailsProcesses.setDataHoraCriacao(now);
            detailsProcesses.setDataHoraAtualizacao(now);
        }

        DetailsProcesses salvo = detailsProcessesRepository.save(detailsProcesses);

        return service.salvar(detailsProcessesDTO);

    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}