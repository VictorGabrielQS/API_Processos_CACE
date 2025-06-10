package cace.processos_api.service.administrator;


import cace.processos_api.dto.administrator.DetailsProcessesDTO;
import cace.processos_api.model.administrator.DetailsProcesses;
import cace.processos_api.repository.administrator.DetailsProcessesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public DetailsProcessesDTO salvar(DetailsProcessesDTO dto) {
        DetailsProcesses entity;

        if(dto.getId() != null) {
            entity = detailsProcessesRepository.findById(dto.getId())
                    .orElse(new DetailsProcesses());
        } else {
            entity = new DetailsProcesses();
        }

        entity.setProcessosVerificar(dto.getProcessosVerificar());
        entity.setProcessosRenajud(dto.getProcessosRenajud());
        entity.setProcessosInfojud(dto.getProcessosInfojud());
        entity.setProcessosErroCertidao(dto.getProcessosErroCertidao());

        return toDTO(detailsProcessesRepository.save(entity));
    }


    public void deletar(Long id) {
        detailsProcessesRepository.deleteById(id);
    }





}
