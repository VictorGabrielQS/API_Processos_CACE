package cace.processos_api.service;

import cace.processos_api.dto.ProcessoDTO;
import cace.processos_api.exception.ResourceNotFoundException;
import cace.processos_api.model.PoloAtivo;
import cace.processos_api.model.PoloPassivo;
import cace.processos_api.model.Processo;
import cace.processos_api.repository.PoloAtivoRepository;
import cace.processos_api.repository.PoloPassivoRepository;
import cace.processos_api.repository.ProcessoRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessoService {
    private final ProcessoRepository processoRepository;
    private final PoloAtivoRepository poloAtivoRepository;
    private final PoloPassivoRepository poloPassivoRepository;

    public ProcessoService(ProcessoRepository processoRepository, PoloAtivoRepository poloAtivoRepository,  PoloPassivoRepository poloPassivoRepository) {
        this.processoRepository = processoRepository;
        this.poloAtivoRepository = poloAtivoRepository;
        this.poloPassivoRepository = poloPassivoRepository;
    }

    public ProcessoDTO createProcesso(ProcessoDTO processoDTO) {
        PoloAtivo poloAtivo = poloAtivoRepository.findByCpfCnpj(processoDTO.getPoloAtivoCpfCnpj())
                .orElseThrow(() -> new ResourceNotFoundException("Polo Ativo não encontrado com cpf/cnpj: " + processoDTO.getPoloAtivoCpfCnpj()));

        PoloPassivo poloPassivo = poloPassivoRepository.findByCpfCnpj(processoDTO.getPoloPassivoCpfCnpj())
                .orElseThrow(() -> new ResourceNotFoundException("Polo Passivo não encontrado com cpf/cnpj: " + processoDTO.getPoloPassivoCpfCnpj()));

        Processo processo = new Processo();
        processo.setNumeroCompleto(processoDTO.getNumeroCompleto());
        processo.setNumeroCurto(processoDTO.getNumeroCurto());
        processo.setPoloAtivo(poloAtivo);
        processo.setPoloPassivo(poloPassivo);
        processo.setServentia(processoDTO.getServentia());
        processo.setStatus(processoDTO.getStatus());
        processo.setResponsavel(processoDTO.getResponsavel());
        processo.setDescricao(processoDTO.getDescricao());
        processo.setTipoCertidao(processoDTO.getCertidao());
        processo.setUrlProcessoProjudi(processoDTO.getUrlProcessoProjudi());

        Processo savedProcesso = processoRepository.save(processo);
        return convertToDTO(savedProcesso);
    }


    //Métodos de Get Processo

    public List<ProcessoDTO> getAllProcessos() {
        return processoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProcessoDTO getProcessoById(Long id) {
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com id: " + id));
        return convertToDTO(processo);
    }

    public  ProcessoDTO getProcessoByNumeroCurto(String numeroCurto){
        Processo processo = processoRepository.findByNumeroCurto(numeroCurto)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com esse numero Curto : " + numeroCurto));
        return convertToDTO(processo);

    }

    public  ProcessoDTO getProcessoByNumeroCompleto(String numeroCompleto){
        Processo processo = processoRepository.findByNumeroCompleto(numeroCompleto)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com esse numero Completo : " + numeroCompleto));
        return convertToDTO(processo);

    }

    public  List<ProcessoDTO> getProcessosByPoloAtivoCpfCnpj(String cpfCnpj){
        List<Processo> processos = processoRepository.findAllProcessosByPoloAtivoCpfCnpj(cpfCnpj);
        return processos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public  List<ProcessoDTO> getProcessosByPoloPassivoCpfCnpj(String cpfCnpj){
        List<Processo> processos = processoRepository.findAllProcessosByPoloPassivoCpfCnpj(cpfCnpj);
        return processos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProcessoDTO> getProcessoByStatus(String status) {
        List<Processo> processos = processoRepository.findAllProcessosByStatus(status);
        return processos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProcessoDTO> getProcessoByPeriodo(String dataInicioStr, String dataFimStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataInicio = LocalDate.parse(dataInicioStr, formatter);
        LocalDate dataFim = LocalDate.parse(dataFimStr, formatter);

// Define intervalo completo do dia (de 00:00 a 23:59:59)
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

        List<Processo> processos = processoRepository.findAllByDataCriacaoBetween(inicio, fim);

        return processos.stream()
                .map(ProcessoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProcessoDTO> getProcessoByResponsavel(String responsavel) {
        List<Processo> processos = processoRepository.findAllProcessosByResponsavel(responsavel);
        return  processos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }

    public List<ProcessoDTO> getProcessoByServentia(String serventia) {
      List<Processo> processos = processoRepository.findAllProcessosByServentia(serventia);
      return  processos.stream()
              .map(this::convertToDTO)
              .collect(Collectors.toList());
    }


    public List<ProcessoDTO> getProcessoByTipoCertidao(String tipoCertidao) {
        List <Processo> processos = processoRepository.findAllProcessosByTipoCertidao(tipoCertidao);
        return  processos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }






    //Métodos de Update Processo
    public ProcessoDTO updateProcessoByNumeroCurto(String numeroCurto, ProcessoDTO processoDTO) {
        Processo processo = processoRepository.findByNumeroCurto(numeroCurto)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com numeroCurto: " + numeroCurto));

        return updateProcesso(processo, processoDTO);
    }

    public ProcessoDTO updateProcessoByNumeroCompleto(String numeroCompleto, ProcessoDTO processoDTO) {
        Processo processo = processoRepository.findByNumeroCompleto(numeroCompleto)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com numeroCompleto: " + numeroCompleto));

        return updateProcesso(processo, processoDTO);
    }

    private ProcessoDTO updateProcesso(Processo processo, ProcessoDTO processoDTO) {
        PoloAtivo poloAtivo = poloAtivoRepository.findByCpfCnpj(processoDTO.getPoloAtivoCpfCnpj())
                .orElseThrow(() -> new ResourceNotFoundException("Polo Ativo não encontrado com CPF/CNPJ: " + processoDTO.getPoloAtivoCpfCnpj()));

        PoloPassivo poloPassivo = poloPassivoRepository.findByCpfCnpj(processoDTO.getPoloPassivoCpfCnpj())
                .orElseThrow(() -> new ResourceNotFoundException("Polo Passivo não encontrado com CPF/CNPJ: " + processoDTO.getPoloPassivoCpfCnpj()));

        processo.setNumeroCompleto(processoDTO.getNumeroCompleto());
        processo.setNumeroCurto(processoDTO.getNumeroCurto());
        processo.setPoloAtivo(poloAtivo);
        processo.setPoloPassivo(poloPassivo);
        processo.setServentia(processoDTO.getServentia());
        processo.setStatus(processoDTO.getStatus());
        processo.setResponsavel(processoDTO.getResponsavel());
        processo.setDescricao(processoDTO.getDescricao());
        processo.setTipoCertidao(processoDTO.getCertidao());

        Processo updatedProcesso = processoRepository.save(processo);
        return convertToDTO(updatedProcesso);
    }

    public ProcessoDTO updateStatus(String numeroCurto, String novoStatus) {
        Processo processo = processoRepository.findByNumeroCurto(numeroCurto)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado : " + numeroCurto));

        processo.setStatus(novoStatus);
        processoRepository.save(processo);

        return ProcessoDTO.fromEntity(processo);
    }

    public ProcessoDTO updateResponsavel(String numeroCurto, String novoResponsavel) {
        Processo processo = processoRepository.findByNumeroCurto(numeroCurto)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado : " + numeroCurto));

        processo.setResponsavel(novoResponsavel);
        processoRepository.save(processo);

        return ProcessoDTO.fromEntity(processo);
    }



    // Métodos para deletar
    public void deleteProcessoByNumeroCurto(String numeroCurto) {
        Processo processo = processoRepository.findByNumeroCurto(numeroCurto)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com numeroCurto: " + numeroCurto));
        processoRepository.delete(processo);
    }

    public void deleteProcessoByNumeroCompleto(String numeroCompleto) {
        Processo processo = processoRepository.findByNumeroCompleto(numeroCompleto)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com numeroCompleto: " + numeroCompleto));
        processoRepository.delete(processo);
    }

    private ProcessoDTO convertToDTO(Processo processo) {
        return new ProcessoDTO(
                processo.getId(),
                processo.getNumeroCompleto(),
                processo.getNumeroCurto(),
                processo.getPoloAtivo().getCpfCnpj(),
                processo.getPoloPassivo().getCpfCnpj(),
                processo.getServentia(),
                processo.getStatus(),
                processo.getResponsavel(),
                processo.getDescricao(),
                processo.getTipoCertidao(),
                processo.getUrlProcessoProjudi(),
                ProcessoDTO.formatarData(processo.getDataCriacao()),
                ProcessoDTO.formatarData(processo.getDataAtualizacao())
        );
    }



}
