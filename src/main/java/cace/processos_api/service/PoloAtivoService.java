package cace.processos_api.service;

import cace.processos_api.dto.PoloDTO;
import cace.processos_api.dto.PoloDetalhadoDTO;
import cace.processos_api.dto.ResponseDTO;
import cace.processos_api.exception.UserNotFoundException;
import cace.processos_api.model.process.PoloAtivo;
import cace.processos_api.repository.PoloAtivoRepository;
import cace.processos_api.repository.PoloRepository;
import cace.processos_api.util.CpfCnpjUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PoloAtivoService extends PoloService {
    private final PoloAtivoRepository poloAtivoRepository;


    public PoloAtivoService(PoloAtivoRepository poloAtivoRepository , PoloRepository poloRepository) {
        super(poloRepository);
        this.poloAtivoRepository = poloAtivoRepository;
    }


    public PoloDTO createPoloAtivo(PoloDTO poloDTO) {
        if (poloDTO.getCpfCnpj() != null) {
            poloDTO.setCpfCnpj(CpfCnpjUtil.limpar(poloDTO.getCpfCnpj()));
        }
        return super.createPolo(poloDTO, PoloAtivo.class);
    }



    //Retorna todos os polos Ativos
    public List<PoloDTO> getAllPolosAtivos() {
        return poloAtivoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    //Filtra polo ativo por cpf/cnpj
    public ResponseDTO<PoloDTO> getPoloAtivoByCpfCnpj(String cpfCnpj) {
        String cpfCnpjLimpo = CpfCnpjUtil.limpar(cpfCnpj);

        return poloAtivoRepository.findByCpfCnpj(cpfCnpjLimpo)
                .map(polo -> new ResponseDTO<>(true, convertToDTO(polo), "Polo Ativo encontrado"))
                .orElse(new ResponseDTO<>(false, null, "Polo Ativo não encontrado"));
    }



    //Filtra polo ativo por nome
    public ResponseDTO<List<PoloDTO>> getPoloAtivoByNome(String nome) {
        List<PoloAtivo> polos = poloAtivoRepository.searchByNomeAproximado(nome.trim());

        if (!polos.isEmpty()) {
            List<PoloDTO> dtoList = polos.stream()
                    .map(this::convertToDTO)
                    .toList();

            return new ResponseDTO<>(true, dtoList, "Polo(s) Ativo(s) encontrado(s)");
        } else {
            return new ResponseDTO<>(false, null, "Nenhum polo ativo parecido encontrado");
        }
    }





    //Detalhadamente :


    public PoloDetalhadoDTO getDetalhadoByCpfCnpj(String cpfCnpj) {
        PoloAtivo poloAtivo = poloAtivoRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new UserNotFoundException("Polo Ativo não encontrado com CPF/CNPJ: " + cpfCnpj));
        return convertToDetalhadoDTO(poloAtivo);
    }


    public PoloDetalhadoDTO getDetalhadoById(Long id) {

        PoloAtivo polo = poloAtivoRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Polo Ativo não encontrado com esse Id: " + id));
        return convertToDetalhadoDTO(polo);


    }


    private void preencherDadosDetalhados(PoloAtivo polo, PoloDetalhadoDTO dto) {
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


    //Atualizar dados detalhado PoloAtivo
    public PoloDetalhadoDTO updatePoloAtivo(Long id, PoloDetalhadoDTO dto) {
        PoloAtivo polo = poloAtivoRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Polo Ativo não encontrado com ID: " + id));
        preencherDadosDetalhados(polo, dto);
        PoloAtivo atualizado = poloAtivoRepository.save(polo);
        return convertToDetalhadoDTO(atualizado);
    }

    public List<PoloDetalhadoDTO> getTodosPolosDetalhados() {
        List<PoloAtivo> polos = poloAtivoRepository.findAll(); // ou o equivalente no seu projeto
        return polos.stream()
                .map(this::convertToDetalhadoDTO)
                .collect(Collectors.toList());
    }
}
