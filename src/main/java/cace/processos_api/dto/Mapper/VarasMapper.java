package cace.processos_api.dto.Mapper;


import cace.processos_api.dto.deltaSap.VarasRequest;
import cace.processos_api.dto.deltaSap.VarasResponse;
import cace.processos_api.model.deltaSap.Varas;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VarasMapper {


    // DTORequest -> Entidade
    Varas toEntity(VarasRequest varasRequest);


    // Entidade -> DTOResponse
    VarasResponse toDTO(Varas varas);


    // ✅ Mapear listas automaticamente
    List<Varas> toEntityList(List<VarasRequest> dtoList);
    List<VarasResponse> toDTOList(List<Varas> entityList);


}
