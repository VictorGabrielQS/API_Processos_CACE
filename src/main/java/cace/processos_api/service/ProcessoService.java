package cace.processos_api.service;

import cace.processos_api.dto.ProcessoDTO;
import cace.processos_api.exception.ResourceNotFoundException;
import cace.processos_api.model.process.PoloAtivo;
import cace.processos_api.model.process.PoloPassivo;
import cace.processos_api.model.process.Processo;
import cace.processos_api.repository.PoloAtivoRepository;
import cace.processos_api.repository.PoloPassivoRepository;
import cace.processos_api.repository.ProcessoRepository;
import cace.processos_api.util.CpfCnpjUtil;
import cace.processos_api.util.NumeroProcessoUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        String numeroCurtoLimpo = NumeroProcessoUtil.limparCurto(processoDTO.getNumeroCurto());

        // Verifica se já existe processo com esse numeroCurto
        return processoRepository.findByNumeroCurto(numeroCurtoLimpo)
                .map(this::convertToDTO) // retorna o processo existente, não insere outro
                .orElseGet(() -> {
                    String cpfCnpjAtivo = CpfCnpjUtil.limpar(processoDTO.getPoloAtivoCpfCnpj());
                    String cpfCnpjPassivo = CpfCnpjUtil.limpar(processoDTO.getPoloPassivoCpfCnpj());

                    PoloAtivo poloAtivo = null;
                    PoloPassivo poloPassivo = null;

                    if (cpfCnpjAtivo != null && !cpfCnpjAtivo.isEmpty() && !cpfCnpjAtivo.equalsIgnoreCase("Não encontrado")) {
                        poloAtivo = poloAtivoRepository.findByCpfCnpj(cpfCnpjAtivo).orElse(null); // ❌ NÃO lança exceção
                    }

                    if (cpfCnpjPassivo != null && !cpfCnpjPassivo.isEmpty() && !cpfCnpjPassivo.equalsIgnoreCase("Não encontrado")) {
                        poloPassivo = poloPassivoRepository.findByCpfCnpj(cpfCnpjPassivo).orElse(null);
                    }

                    Processo processo = new Processo();
                    processo.setNumeroCompleto(NumeroProcessoUtil.limparCompleto(processoDTO.getNumeroCompleto()));
                    processo.setNumeroCurto(numeroCurtoLimpo);
                    processo.setPoloAtivo(poloAtivo); // pode ser null
                    processo.setPoloPassivo(poloPassivo); // pode ser null
                    processo.setServentia(processoDTO.getServentia());
                    processo.setStatus(processoDTO.getStatus());
                    processo.setResponsavel(processoDTO.getResponsavel());
                    processo.setDescricao(processoDTO.getDescricao());
                    processo.setTipoCertidao(processoDTO.getCertidao());
                    processo.setUrlProcessoProjudi(processoDTO.getUrlProcessoProjudi());

                    Processo savedProcesso = processoRepository.save(processo);
                    return convertToDTO(savedProcesso);
                });
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
    //    String numeroCurtoLimpo = NumeroProcessoUtil.limparCurto(numeroCurto);

        Processo processo = processoRepository.findByNumeroCurto(numeroCurto)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com esse numero Curto : " + numeroCurto));
        return convertToDTO(processo);

    }

    public  ProcessoDTO getProcessoByNumeroCompleto(String numeroCompleto){
        String numeroCompletoLimpo = NumeroProcessoUtil.limparCompleto(numeroCompleto);

        Processo processo = processoRepository.findByNumeroCompleto(numeroCompletoLimpo)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com esse numero Completo : " + numeroCompleto));
        return convertToDTO(processo);

    }




    //Buscar Todos os Processos com um determinado PoloAtivo pelo cpf/cnpj ou pelo Nome do polo ativo
    public Object getProcessosByCpfCnpjOuNomeAproximadoPoloAtivo(String identificador) {

        if (identificador.matches("\\d+")) {
            // CPF/CNPJ: retorna apenas a lista direta
            List<Processo> processos = processoRepository.findAllProcessosByPoloAtivoCpfCnpj(identificador);
            return processos.stream().map(this::convertToDTO).toList();
        } else {
            // Nome: retorna agrupado por nome aproximado
            Map<String, List<ProcessoDTO>> resultado = new LinkedHashMap<>();
            List<PoloAtivo> polosEncontrados = poloAtivoRepository.searchByNomeAproximado(identificador);

            for (PoloAtivo polo : polosEncontrados) {
                List<Processo> processos = processoRepository.findAllProcessosByPoloAtivoCpfCnpj(polo.getCpfCnpj());
                if (!processos.isEmpty()) {
                    resultado.put(polo.getNome(), processos.stream().map(this::convertToDTO).toList());
                }
            }

            return resultado;
        }
    }


    //Buscar Todos os Processos com um determinado PoloPassivo pelo cpf/cnpj ou pelo Nome do polo passivo
    public Object getProcessosByCpfCnpjOuNomeAproximadoPoloPassivo(String identificador) {

        if (identificador.matches("\\d+")) {
            // CPF/CNPJ: retorna apenas a lista direta
            List<Processo> processos = processoRepository.findAllProcessosByPoloPassivoCpfCnpj(identificador);
            return processos.stream().map(this::convertToDTO).toList();
        } else {
            // Nome: retorna agrupado por nome aproximado
            Map<String, List<ProcessoDTO>> resultado = new LinkedHashMap<>();
            List<PoloPassivo> polosEncontrados = poloPassivoRepository.searchByNomeAproximado(identificador);

            for (PoloPassivo polo : polosEncontrados) {
                List<Processo> processos = processoRepository.findAllProcessosByPoloPassivoCpfCnpj(polo.getCpfCnpj());
                if (!processos.isEmpty()) {
                    resultado.put(polo.getNome(), processos.stream().map(this::convertToDTO).toList());
                }
            }

            return resultado;
        }
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

    public ProcessoDTO updateResponsavel(Long id, String novoResponsavel) {
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Processo não encontrado com id : " + id));

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

    // Converte antes de retornar o resultado
    private ProcessoDTO convertToDTO(Processo processo) {
        return new ProcessoDTO(
                processo.getId(),
                NumeroProcessoUtil.formatarNumeroCompleto(processo.getNumeroCompleto()),
                NumeroProcessoUtil.formatarNumeroCurto(processo.getNumeroCurto()),
                processo.getPoloAtivo() != null ? processo.getPoloAtivo().getCpfCnpj() : null,
                processo.getPoloPassivo() != null ? processo.getPoloPassivo().getCpfCnpj() : null,
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


    public List<ProcessoDTO> getProcessosPorData(String data) {
        LocalDate localDate = LocalDate.parse(data); // valida se está no formato correto
        LocalDateTime inicioDoDia = localDate.atStartOfDay();
        LocalDateTime fimDoDia = localDate.atTime(LocalTime.MAX);

        return processoRepository.findByDataCriacaoBetween(inicioDoDia, fimDoDia)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }




}
