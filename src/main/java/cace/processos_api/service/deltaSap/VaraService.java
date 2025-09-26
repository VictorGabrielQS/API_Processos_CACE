package cace.processos_api.service.deltaSap;

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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VaraService {
    private final VaraRepository varaRepository;



    // Criar Vara
    public VaraResponseDTO criarVara(VaraRequestDTO varaRequestDTO) {
        Vara vara = new Vara();
        vara.setNomeVara(varaRequestDTO.getNomeVara());
        vara.setCodigoVaraSisbajud(varaRequestDTO.getCodigoVaraSisbajud());

        Vara salvo = varaRepository.save(vara);
        return toResponse(salvo);
    }




    // Criar Lista de Varas
    public List<VaraResponseDTO> criarListaVara(List<VaraRequestDTO> varaRequestDTOS) {
        List<Vara> varas = varaRequestDTOS.stream().map(req -> {
            Vara v = new Vara();
            v.setNomeVara(req.getNomeVara());
            v.setCodigoVaraSisbajud(req.getCodigoVaraSisbajud());
            return v;
        }).collect(Collectors.toList());

        List<Vara> salvos = varaRepository.saveAll(varas);
        return salvos.stream().map(this::toResponse).collect(Collectors.toList());
    }




    // Listar Varas Paginadas
    public Page<VaraResponseDTO> listarVarasPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return varaRepository.findAll(pageable).map(this::toResponse);
    }




    // Listar Vara por Nome
    public VaraResponseDTO buscarVaraPorNome(String nomeVara) {
        Vara vara = varaRepository.findByNomeVara(nomeVara)
                .orElseThrow(() -> new RuntimeException("Vara não encontrada com o nome: " + nomeVara));
        return toResponse(vara);
    }



    // Listar Vara por Código
    public VaraResponseDTO buscarVaraPorCodigoVara(Long codigoVaraSisbajud) {
        Vara vara = varaRepository.findByCodigoVaraSisbajud(codigoVaraSisbajud)
                .orElseThrow(() -> new RuntimeException("Vara não encontrada com esse código: " + codigoVaraSisbajud));
        return toResponse(vara);
    }



    // Deletar Vara por ID
    @Transactional
    public void deletarVara(Integer id) {
        if (!varaRepository.existsById(id)) {
            throw new RuntimeException("Vara com id " + id + " não encontrada.");
        }
        varaRepository.deleteById(id);
    }



    // Atualizar Vara
    public VaraResponseDTO atualizarVara(Integer id, VaraRequestDTO varaRequestDTO) {
        Vara existente = varaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vara não encontrada com id " + id));

        existente.setNomeVara(varaRequestDTO.getNomeVara());
        existente.setCodigoVaraSisbajud(varaRequestDTO.getCodigoVaraSisbajud());

        Vara salvo = varaRepository.save(existente);
        return toResponse(salvo);
    }




    // =====================
    // Conversão manual
    // =====================
    private VaraResponseDTO toResponse(Vara vara) {
        return new VaraResponseDTO(vara.getId(), vara.getNomeVara(), vara.getCodigoVaraSisbajud());
    }
}
