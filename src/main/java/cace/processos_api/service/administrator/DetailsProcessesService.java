package cace.processos_api.service.administrator;


import cace.processos_api.dto.administrator.DetailsProcessesDTO;
import cace.processos_api.model.administrator.DetailsProcesses;
import cace.processos_api.repository.administrator.DetailsProcessesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DetailsProcessesService {

    @Autowired
    private DetailsProcessesRepository detailsProcessesRepository;



    private DetailsProcessesDTO toDTO(DetailsProcesses entity){
        return new DetailsProcessesDTO(
                entity.getId(),
                entity.getProcessosVerificar(),
                entity.getProcessosRenajud(),
                entity.getProcessosInfojud(),
                entity.getProcessosErroCertidao(),
                entity.getProcessosTotais(),
                entity.getPercentualErros(),
                entity.getDataHoraCriacao(),
                entity.getDataHoraAtualizacao()
        );
    }


    private DetailsProcesses toEntity(DetailsProcessesDTO dto) {
        DetailsProcesses entity = new DetailsProcesses();
        entity.setId(dto.getId());
        entity.setProcessosVerificar(dto.getProcessosVerificar());
        entity.setProcessosRenajud(dto.getProcessosRenajud());
        entity.setProcessosInfojud(dto.getProcessosInfojud());
        entity.setProcessosErroCertidao(dto.getProcessosErroCertidao());
        return entity;
    }


    public List<DetailsProcessesDTO> listarTodos() {
        return detailsProcessesRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }


    public Optional<DetailsProcessesDTO> buscarPorId(Long id) {
        return detailsProcessesRepository.findById(id).map(this::toDTO);
    }


    public List<DetailsProcessesDTO> salvarLote(List<DetailsProcessesDTO> listaDTO) {
        return listaDTO.stream()
                .map(this::salvar)
                .toList();
    }



    public DetailsProcessesDTO salvar(DetailsProcessesDTO dto) {
        LocalDate dataCriacao = dto.getDataHoraCriacao() != null
                ? dto.getDataHoraCriacao().toLocalDate()
                : LocalDate.now();

        LocalDateTime startOfDay = dataCriacao.atStartOfDay();
        LocalDateTime endOfDay = dataCriacao.atTime(LocalTime.MAX);

        List<DetailsProcesses> listaRegistros = detailsProcessesRepository.findByDataHoraCriacaoBetween(startOfDay, endOfDay);

        DetailsProcesses detailsProcesses;

        if (!listaRegistros.isEmpty()) {
            detailsProcesses = listaRegistros.get(0); // Atualiza existente
        } else {
            detailsProcesses = new DetailsProcesses();
            detailsProcesses.setDataHoraCriacao(dto.getDataHoraCriacao() != null
                    ? dto.getDataHoraCriacao()
                    : LocalDateTime.now());
        }

        // Atualiza dados
        detailsProcesses.setProcessosVerificar(dto.getProcessosVerificar());
        detailsProcesses.setProcessosRenajud(dto.getProcessosRenajud());
        detailsProcesses.setProcessosInfojud(dto.getProcessosInfojud());
        detailsProcesses.setProcessosErroCertidao(dto.getProcessosErroCertidao());
        detailsProcesses.setProcessosTotais(dto.getProcessosTotais());
        detailsProcesses.setPercentualErros(dto.getPercentualErros());
        detailsProcesses.setDataHoraAtualizacao(LocalDateTime.now());

        DetailsProcesses salvo = detailsProcessesRepository.save(detailsProcesses);

        return new DetailsProcessesDTO(salvo);
    }



    public void deletar(Long id) {
        detailsProcessesRepository.deleteById(id);
    }





}
