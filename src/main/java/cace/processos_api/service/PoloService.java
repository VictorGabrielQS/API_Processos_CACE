package cace.processos_api.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cace.processos_api.dto.PoloDetalhadoDTO;
import cace.processos_api.model.process.PoloAtivo;
import cace.processos_api.model.process.PoloPassivo;
import cace.processos_api.util.CpfCnpjUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import cace.processos_api.dto.PoloDTO;
import cace.processos_api.exception.ResourceNotFoundException;
import cace.processos_api.model.process.Polo;
import cace.processos_api.repository.PoloRepository;

@Service
public class PoloService {

    protected final PoloRepository poloRepository;

    //Construtor de service
    public PoloService(PoloRepository poloRepository) {
        this.poloRepository = poloRepository;
    }



// =======================
// CRIAR NOVO POLO (ATIVO OU PASSIVO)
// =======================
public PoloDTO createPolo(PoloDTO poloDTO, Class<? extends Polo> poloClass) {
    try {
        String nome = poloDTO.getNome().trim();
        String cpfCnpj = poloDTO.getCpfCnpj() != null ? CpfCnpjUtil.limpar(poloDTO.getCpfCnpj()) : null;

        Optional<Polo> poloExistente = Optional.empty();

        // 1. Tenta buscar por CPF/CNPJ primeiro (caso informado)
        if (cpfCnpj != null && !cpfCnpj.isEmpty()) {
            poloExistente = poloRepository.findByCpfCnpj(cpfCnpj);
        }

        // 2. Se não achou por CPF/CNPJ, tenta por nome
        if (poloExistente.isEmpty()) {
            poloExistente = poloRepository.findByNome(nome);
        }

        // 3. Se já existir, retorna o existente
        if (poloExistente.isPresent()) {
            return convertToDTO(poloExistente.get());
        }

        // 4. Cria nova instância da subclasse
        Polo novoPolo = poloClass.getDeclaredConstructor().newInstance();
        novoPolo.setNome(nome);
        novoPolo.setCpfCnpj(cpfCnpj);

        Polo salvo = poloRepository.save(novoPolo);
        return convertToDTO(salvo);

    } catch (Exception e) {
        throw new RuntimeException("Erro ao criar polo", e);
    }
}




// =======================
// LISTAR TODOS OS POLOS
// =======================
public List<PoloDTO> getAllPolos() {
    return poloRepository.findAll().stream() // busca todos os registros da tabela polo
            .map(this::convertToDTO)         // converte cada Polo (entidade) para PoloDTO
            .collect(Collectors.toList());   // retorna uma lista de PoloDTO
}




// =======================
// BUSCAR POLO POR CPF/CNPJ
// =======================
public PoloDTO getPoloByCpfCnpj(String cpfCnpj) {
    Polo polo = poloRepository.findByCpfCnpj(cpfCnpj) // busca o polo pelo ID
                .orElseThrow(() -> new ResourceNotFoundException("Polo não encontrado com CPF / CNPJ : " + cpfCnpj));  // se não encontrar, lança exceção personalizada
    return convertToDTO(polo); // converte e retorna o DTO
}




// =======================
// BUSCAR POLO POR NOME
// =======================

    public  PoloDTO getPoloByNome(String nome){
        Polo polo = poloRepository.findByNome(nome) // busca o polo pelo nome
                .orElseThrow(() -> new ResourceNotFoundException("Polo não encontrado com Nome : " + nome));
        return convertToDTO(polo);
    }




// =======================
// ATUALIZAR POLO
// =======================
public PoloDTO updatePolo(String cpfCnpj , PoloDTO poloDTO) {
    Polo polo = poloRepository.findByCpfCnpj(cpfCnpj) // busca o polo existente pelo cpf / cnpj
                .orElseThrow(() -> new ResourceNotFoundException("Polo não encontrado com CPF / CNPJ : " + cpfCnpj));


       // Atualiza os campos com os dados do DTO recebido
       polo.setNome(poloDTO.getNome());
       polo.setCpfCnpj(poloDTO.getCpfCnpj());

       Polo updatePolo = poloRepository.save(polo);
       return convertToDTO(updatePolo);
}


public PoloDTO updatePoloId(Long id , PoloDTO poloDTO) {
        Polo polo = poloRepository.findById(id) // busca o polo existente pelo id
                .orElseThrow(() -> new ResourceNotFoundException("Polo não encontrado com esse id : " + id));


        // Atualiza os campos com os dados do DTO recebido
        polo.setNome(poloDTO.getNome());
        polo.setCpfCnpj(poloDTO.getCpfCnpj());

        Polo updatePolo = poloRepository.save(polo);
        return convertToDTO(updatePolo);
    }



// =======================
// EXCLUIR POLO POR CPF/CNPJ
// =======================
@Transactional
public void deletePolo(String cpfCnpj){
    if (!poloRepository.existsByCpfCnpj(cpfCnpj)) { // verifica se o polo existe
        throw new ResourceNotFoundException("Polo não encontrado com CPF / CNPJ : " + cpfCnpj);
    }

    poloRepository.deleteByCpfCnpj(cpfCnpj); // se existir, deleta do banco
}


// =======================
// EXCLUIR POLO POR Id
// =======================
    @Transactional
    public void deletePoloId(Long id){
        if (!poloRepository.existsById(id)) { // verifica se o polo existe
            throw new ResourceNotFoundException("Polo não encontrado com ID : " + id); // se não existir, lança exceção

        }
        poloRepository.deleteById(id); // se existir, deleta do banco
    }



// =======================
// CONVERTE ENTIDADE PARA DTO
// =======================
protected PoloDTO convertToDTO(Polo polo) {
    return new PoloDTO(
            polo.getId(),
            polo.getNome(),
            formatarCpfCnpj(polo.getCpfCnpj()) // aplica a formatação aqui
    );
}


// =======================
// CONVERTE POLO Simples para detalhada
// =======================
protected PoloDetalhadoDTO convertToDetalhadoDTO(Polo polo) {
    PoloDetalhadoDTO dto = new PoloDetalhadoDTO();
    dto.setId(polo.getId());
    dto.setNome(polo.getNome());
    dto.setCpfCnpj(polo.getCpfCnpj());

    if (polo instanceof PoloAtivo ativo) {
        dto.setDataNascimentoParte(ativo.getDataNascimentoParte());
        dto.setEnderecoParte(ativo.getEnderecoParte());
        dto.setFiliacaoParte(ativo.getFiliacaoParte());
        dto.setFiliacaoParteCpf(ativo.getFiliacaoParteCpf());
        dto.setAntecedenteCriminal(ativo.getAntecedenteCriminal());
        dto.setDescricao(ativo.getDescricao());
    }

    if (polo instanceof PoloPassivo passivo) {
        dto.setDataNascimentoParte(passivo.getDataNascimentoParte());
        dto.setEnderecoParte(passivo.getEnderecoParte());
        dto.setFiliacaoParte(passivo.getFiliacaoParte());
        dto.setFiliacaoParteCpf(passivo.getFiliacaoParteCpf());
        dto.setAntecedenteCriminal(passivo.getAntecedenteCriminal());
        dto.setDescricao(passivo.getDescricao());
    }

    return dto;
}



    // =======================
// Formata o cpf e o cnpj
// =======================
    private String formatarCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.isBlank()) {
            return null; // ou retornar "" se preferir
        }

        if (cpfCnpj.length() == 11) {
            return cpfCnpj.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (cpfCnpj.length() == 14) {
            return cpfCnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }

        return cpfCnpj;
    }


}
