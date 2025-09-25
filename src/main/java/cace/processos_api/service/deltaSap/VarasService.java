package cace.processos_api.service.deltaSap;

import cace.processos_api.dto.Mapper.VarasMapper;
import cace.processos_api.dto.deltaSap.VarasRequest;
import cace.processos_api.dto.deltaSap.VarasResponse;
import cace.processos_api.model.deltaSap.Varas;
import cace.processos_api.repository.deltaSap.VarasRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VarasService {

    final private VarasRepository varasRepository;
    final private VarasMapper varasMapper;


    // Criar Vara
    public VarasResponse criarVara(VarasRequest varasRequest) {
        if(varasRequest.getNomeVara() == null || varasRequest.getNomeVara().isBlank()) {
            throw new IllegalArgumentException("nomeVara é obrigatório");
        }
        if(varasRequest.getCodigoVaraSisbajud() == null) {
            throw new IllegalArgumentException("codigoVaraSisbajud é obrigatório");
        }

        Varas varas = varasMapper.toEntity(varasRequest);
        varas = varasRepository.save(varas);
        return varasMapper.toDTO(varas);
    }



    // Criar varas em Lote
    public List<VarasResponse> criarVarasEmLote(List<VarasRequest> varasRequests){
        List<Varas> varas = varasMapper.toEntityList(varasRequests);
        List<Varas> salvos = varasRepository.saveAll(varas);
        return varasMapper.toDTOList(salvos);
    }


    // Buscar Varas Paginas
    public Page<VarasResponse> listarVarasPaginada(int page , int size){
        Pageable pageable = PageRequest.of(page , size);
        Page<Varas> varas = varasRepository.findAll(pageable);
        return varas.map(varasMapper::toDTO);

    }


    // Buscar Todas as varas Cadastradas
    public List<VarasResponse> listarTodasAsVaras(){
        return varasRepository.findAll()
                .stream()
                .map(varasMapper::toDTO)
                .collect(Collectors.toList());
    }


    // Buscar Vara por Nome
    public VarasResponse buscarVaraPorNome(String nomeVara) {
        return varasMapper.toDTO(varasRepository.findByNomeVara(nomeVara)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado")));
    }


    // Deleta vara por ID
    @Transactional
    public void  deletarVaraPorId(Integer id ){
        Varas varas = varasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vara não Encontrada."));
        varasRepository.deleteById(varas.getId());
    }


}
