package cace.processos_api.dto.Mapper;


import cace.processos_api.dto.deltaSap.VaraRequestDTO;
import cace.processos_api.dto.deltaSap.VaraResponseDTO;
import cace.processos_api.model.deltaSap.Vara;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VaraMapper {

    //DTORequest -> Entidade
    Vara toEntity(VaraRequestDTO varaRequestDTO);

    //Entidade -> DTOResponse
    VaraResponseDTO toResponse(Vara vara);


    // Lista DTORequest -> Lista Entidade
    List<Vara> toEntityList(List<VaraRequestDTO> varaRequestDTOList);


    // Lista de Entidades -> Lista DTO Response
    List<VaraResponseDTO> toResponseList(List<Vara> varas);




}
