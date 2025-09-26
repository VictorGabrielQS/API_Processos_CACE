package cace.processos_api.service.deltaSap;

import cace.processos_api.dto.mapper.VaraMapper;
import cace.processos_api.dto.deltaSap.VaraRequestDTO;
import cace.processos_api.dto.deltaSap.VaraResponseDTO;
import cace.processos_api.model.deltaSap.Vara;
import cace.processos_api.repository.deltaSap.VaraRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class VaraService {

    private final VaraMapper varaMapper;
    private final VaraRepository varaRepository;


    // Criar Vara
    public VaraResponseDTO criarVara(VaraRequestDTO varaRequestDTO){
        Vara vara = varaMapper.toEntity(varaRequestDTO);
        vara = varaRepository.save(vara);
        return varaMapper.toResponse(vara);
    }


    // Criar Lista de Varas
    public List<VaraResponseDTO> criarListaVara(List<VaraRequestDTO> varaRequestDTOS){
        List<Vara> varas = varaMapper.toEntityList(varaRequestDTOS);
        varas = varaRepository.saveAll(varas);
        return varaMapper.toResponseList(varas);
    }


    // Listar Varas Paginadas
    public Page<VaraResponseDTO> listarVarasPaginadas(int page , int size){
        Pageable pageable = PageRequest.of(page , size);
        Page<Vara> varas = varaRepository.findAll(pageable);
        return  varas.map(varaMapper::toResponse);
    }


    // Listar Vara por Nome
    public VaraResponseDTO buscarVaraPorNome(String nomeVara){
        Vara vara = varaRepository.findByNomeVara(nomeVara).orElseThrow(() -> new RuntimeException("Vara não encontrada com o nome: " + nomeVara));
        return varaMapper.toResponse(vara);
    }


    // Listar Vara por CodigoVara
    public VaraResponseDTO buscarVaraPorCodigoVara(Long codigoVaraSisbajud){
        Vara vara = varaRepository.findByCodigoVaraSisbajud(codigoVaraSisbajud)
                .orElseThrow(() -> new RuntimeException("Vara não encontrada com esse codigo : " + codigoVaraSisbajud));
        return varaMapper.toResponse(vara);
    }


    // Deletar vara por id
    @Transactional
    public void deletarVara(Integer id){
        if (!varaRepository.existsById(id)){
            throw  new RuntimeException("Vara com id " + id + " não encontrada.");
        }
        varaRepository.deleteById(id);
    }


    // Atualizar Vara
    public VaraResponseDTO atualizarVara(Integer id , VaraRequestDTO varaRequestDTO){
        Vara varaExiste = varaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vara não encontrada com id " + id));

        varaExiste.setNomeVara(varaRequestDTO.getNomeVara());
        varaExiste.setCodigoVaraSisbajud(varaRequestDTO.getCodigoVaraSisbajud());

        Vara varaSalva = (varaRepository.save(varaExiste));
        return varaMapper.toResponse(varaSalva);
    }


}
