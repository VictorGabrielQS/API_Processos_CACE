package cace.processos_api.service;

import cace.processos_api.dto.PoloDTO;
import cace.processos_api.dto.PoloDetalhadoDTO;
import cace.processos_api.exception.UserNotFoundException;
import cace.processos_api.model.PoloAtivo;
import cace.processos_api.model.PoloPassivo;
import cace.processos_api.repository.PoloPassivoRepository;
import cace.processos_api.repository.PoloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class PoloPassivoService extends PoloService {
    private final PoloPassivoRepository poloPassivoRepository;

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
    public PoloDTO getPoloPassivoByCpfCnpj(String cpfCnpj){
        PoloPassivo poloPassivo = poloPassivoRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new UserNotFoundException("Polo Passivo não encontrado com CPF/CNPJ: " + cpfCnpj));
        return  convertToDTO(poloPassivo);
    }


    //Filtra polo Passivo por nome
    public PoloDTO getPoloPassivoByNome(String nome){
        PoloPassivo poloPassivo = poloPassivoRepository.findByNome(nome)
                .orElseThrow(() -> new UserNotFoundException("Polo Passivo não encontrado com nome : " + nome));
        return convertToDTO(poloPassivo);

    }


    //Detalhadamente :

    public PoloDetalhadoDTO getDetalhadoByCpfCnpj(String cpfCnpj) {
        PoloPassivo polo = poloPassivoRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new UserNotFoundException("Polo Passivo não encontrado com CPF/CNPJ: " + cpfCnpj));
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
