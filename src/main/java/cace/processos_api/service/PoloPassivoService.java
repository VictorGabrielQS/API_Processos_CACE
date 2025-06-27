package cace.processos_api.service;

import cace.processos_api.dto.PoloDTO;
import cace.processos_api.dto.PoloDetalhadoDTO;
import cace.processos_api.dto.ResponseDTO;
import cace.processos_api.exception.UserNotFoundException;
import cace.processos_api.model.process.PoloPassivo;
import cace.processos_api.repository.PoloPassivoRepository;
import cace.processos_api.repository.PoloRepository;
import cace.processos_api.util.CpfCnpjUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class PoloPassivoService extends PoloService {
    private final PoloPassivoRepository poloPassivoRepository;
    public PoloDetalhadoDTO getDetalhadoByIdid;

    public PoloPassivoService(PoloPassivoRepository poloPassivoRepository , PoloRepository poloRepository){
        super(poloRepository);
        this.poloPassivoRepository = poloPassivoRepository;
    }

    public PoloDTO createPoloPassivo(PoloDTO poloDTO){
        poloDTO.setCpfCnpj(poloDTO.getCpfCnpj().replaceAll("[^\\d]", ""));
        return super.createPolo(poloDTO , PoloPassivo.class);
    }

    public List<PoloDTO> getAllPolosPassivos() {
        return poloPassivoRepository.findAll().stream()
                .map(super::convertToDTO)
                .collect(Collectors.toList());
    }


    //Filtra polo Passivo por cpf/cnpj
    public ResponseDTO<PoloDTO> getPoloPassivoByCpfCnpj(String cpfCnpj) {
        String cpfCnpjLimpo = CpfCnpjUtil.limpar(cpfCnpj);

        return poloPassivoRepository.findByCpfCnpj(cpfCnpjLimpo)
                .map(polo -> new ResponseDTO<>(true, convertToDTO(polo), "Polo Passivo encontrado"))
                .orElseGet(() -> new ResponseDTO<>(false, null, "Polo Passivo não encontrado"));
    }



    //Filtra polo Passivo por nome
    public ResponseDTO<PoloDTO> getPoloPassivoByNome(String nome) {
        return poloPassivoRepository.findByNome(nome.trim())
                .map(polo -> new ResponseDTO<>(true, convertToDTO(polo), "Polo Passivo encontrado"))
                .orElseGet(() -> new ResponseDTO<>(false, null, "Polo Passivo não encontrado"));
    }



    //Detalhadamente :


    public List<PoloDetalhadoDTO> getTodosPolosDetalhados() {
        List<PoloPassivo> polos = poloPassivoRepository.findAll(); // ou o equivalente no seu projeto
        return polos.stream()
                .map(this::convertToDetalhadoDTO)
                .collect(Collectors.toList());
    }

    public PoloDetalhadoDTO getDetalhadoByCpfCnpj(String cpfCnpj) {
        PoloPassivo polo = poloPassivoRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new UserNotFoundException("Polo Passivo não encontrado com CPF/CNPJ: " + cpfCnpj));
        return convertToDetalhadoDTO(polo);
    }

    public PoloDetalhadoDTO getDetalhadoById(Long id) {
        PoloPassivo polo = poloPassivoRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Polo Ativo não encontrado com esse Id: " + id));
        return convertToDetalhadoDTO(polo);

    }


    public PoloDetalhadoDTO updatePoloPassivo(Long id, PoloDetalhadoDTO dto) {
        PoloPassivo polo = poloPassivoRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Polo Ativo não encontrado com ID: " + id));
        preencherDadosDetalhados(polo, dto);
        PoloPassivo atualizado = poloPassivoRepository.save(polo);
        return convertToDetalhadoDTO(atualizado);
    }

    private void preencherDadosDetalhados(PoloPassivo polo, PoloDetalhadoDTO dto) {
        polo.setNome(dto.getNome());
        polo.setCpfCnpj(dto.getCpfCnpj());
        polo.setDataNascimentoParte(dto.getDataNascimentoParte());
        polo.setEnderecoParte(dto.getEnderecoParte());
        polo.setEnderecoParteCep(dto.getEnderecoParteCep());
        polo.setFiliacaoParte(dto.getFiliacaoParte());
        polo.setFiliacaoParteCpf(dto.getFiliacaoParteCpf());
        polo.setAntecedenteCriminal(dto.getAntecedenteCriminal());
        polo.setDescricao(dto.getDescricao());
    }


}
