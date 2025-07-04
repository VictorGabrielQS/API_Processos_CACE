package cace.processos_api.controller.administrator;

import cace.processos_api.dto.ProcessoDTO;
import cace.processos_api.dto.administrator.DetailsProcessesDTO;
import cace.processos_api.dto.administrator.RelatorioProcessoDTO;
import cace.processos_api.model.administrator.DetailsProcesses;
import cace.processos_api.model.process.Processo;
import cace.processos_api.repository.ProcessoRepository;
import cace.processos_api.repository.administrator.DetailsProcessesRepository;
import cace.processos_api.service.administrator.DetailsProcessesService;

import java.io.*;

import com.itextpdf.html2pdf.HtmlConverter;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;






@RestController
@RequestMapping("/details-processes")
public class DetailsProcessesController {


    @Autowired
    private DetailsProcessesRepository detailsProcessesRepository;


    @Autowired
    private DetailsProcessesService service;

    @Autowired
    private ProcessoRepository processoRepository;



    @GetMapping
    public List<DetailsProcessesDTO> listarTodos() {
        return service.listarTodos();
    }


    @GetMapping("/{id}")
    public DetailsProcessesDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Detalhe não encontrado com ID: " + id));
    }


    @PostMapping
    public DetailsProcessesDTO salvar(@RequestBody DetailsProcessesDTO detailsProcessesDTO) {
        LocalDate dataCriacao = detailsProcessesDTO.getDataHoraCriacao() != null
                ? detailsProcessesDTO.getDataHoraCriacao().toLocalDate()
                : LocalDate.now();

        LocalDateTime startOfDay = dataCriacao.atStartOfDay();
        LocalDateTime endOfDay = dataCriacao.atTime(LocalTime.MAX);

        List<DetailsProcesses> listaRegistros = detailsProcessesRepository.findByDataHoraCriacaoBetween(startOfDay, endOfDay);

        DetailsProcesses detailsProcesses;

        if (!listaRegistros.isEmpty()) {
            // Atualiza o primeiro registro encontrado do dia
            detailsProcesses = listaRegistros.get(0);
        } else {
            // Cria novo registro
            detailsProcesses = new DetailsProcesses();
            LocalDateTime dataHoraCriacao = detailsProcessesDTO.getDataHoraCriacao() != null
                    ? detailsProcessesDTO.getDataHoraCriacao()
                    : LocalDateTime.now();
            detailsProcesses.setDataHoraCriacao(dataHoraCriacao);
        }

        // Atualiza dados
        detailsProcesses.setProcessosVerificar(detailsProcessesDTO.getProcessosVerificar());
        detailsProcesses.setProcessosRenajud(detailsProcessesDTO.getProcessosRenajud());
        detailsProcesses.setProcessosInfojud(detailsProcessesDTO.getProcessosInfojud());
        detailsProcesses.setProcessosErroCertidao(detailsProcessesDTO.getProcessosErroCertidao());
        detailsProcesses.setProcessosTotais(detailsProcessesDTO.getProcessosTotais());
        detailsProcesses.setPercentualErros(detailsProcessesDTO.getPercentualErros());
        detailsProcesses.setDataHoraAtualizacao(LocalDateTime.now());

        DetailsProcesses salvo = detailsProcessesRepository.save(detailsProcesses);

        return new DetailsProcessesDTO(salvo);
    }


    @PostMapping("/salvar-lote")
    public List<DetailsProcessesDTO> salvarLote(@RequestBody List<DetailsProcessesDTO> listaDTO) {
        List<DetailsProcessesDTO> salvos = new ArrayList<>();

        for (DetailsProcessesDTO dto : listaDTO) {
            LocalDate dataCriacao = dto.getDataHoraCriacao() != null
                    ? dto.getDataHoraCriacao().toLocalDate()
                    : LocalDate.now();

            LocalDateTime startOfDay = dataCriacao.atStartOfDay();
            LocalDateTime endOfDay = dataCriacao.atTime(LocalTime.MAX);

            List<DetailsProcesses> listaRegistros = detailsProcessesRepository.findByDataHoraCriacaoBetween(startOfDay, endOfDay);

            DetailsProcesses detailsProcesses;

            if (!listaRegistros.isEmpty()) {
                // Atualiza o primeiro registro encontrado do dia
                detailsProcesses = listaRegistros.get(0);
            } else {
                // Cria novo registro
                detailsProcesses = new DetailsProcesses();
                LocalDateTime dataHoraCriacao = dto.getDataHoraCriacao() != null
                        ? dto.getDataHoraCriacao()
                        : LocalDateTime.now();
                detailsProcesses.setDataHoraCriacao(dataHoraCriacao);
            }

            // Atualiza os dados
            detailsProcesses.setProcessosVerificar(dto.getProcessosVerificar());
            detailsProcesses.setProcessosRenajud(dto.getProcessosRenajud());
            detailsProcesses.setProcessosInfojud(dto.getProcessosInfojud());
            detailsProcesses.setProcessosErroCertidao(dto.getProcessosErroCertidao());
            detailsProcesses.setProcessosTotais(dto.getProcessosTotais());
            detailsProcesses.setPercentualErros(dto.getPercentualErros());
            detailsProcesses.setDataHoraAtualizacao(LocalDateTime.now());

            // Salva e adiciona à lista de retorno
            DetailsProcesses salvo = detailsProcessesRepository.save(detailsProcesses);
            salvos.add(new DetailsProcessesDTO(salvo));
        }

        return salvos;
    }


    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }


    @GetMapping("/data")
    public ResponseEntity<DetailsProcessesDTO> buscarPorData(@RequestParam String data) {
        LocalDate localDate = LocalDate.parse(data);
        LocalDateTime inicio = localDate.atStartOfDay();
        LocalDateTime fim = localDate.atTime(LocalTime.MAX);

        return detailsProcessesRepository
                .findByDataHoraCriacaoBetween(inicio, fim)
                .stream()
                .findFirst()
                .map(DetailsProcessesDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }



    //Editar detalhes de um processo específico por id
    @PatchMapping("/{id}/quantidades")
    public ResponseEntity<DetailsProcessesDTO> atualizarQuantidades(
            @PathVariable Long id,
            @RequestBody Map<String, Object> camposAtualizados) {

        Optional<DetailsProcesses> optional = detailsProcessesRepository.findById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DetailsProcesses details = optional.get();

        if (camposAtualizados.containsKey("processosVerificar")) {
            details.setProcessosVerificar((Integer) camposAtualizados.get("processosVerificar"));
        }

        if (camposAtualizados.containsKey("processosRenajud")) {
            details.setProcessosRenajud((Integer) camposAtualizados.get("processosRenajud"));
        }

        if (camposAtualizados.containsKey("processosInfojud")) {
            details.setProcessosInfojud((Integer) camposAtualizados.get("processosInfojud"));
        }

        if (camposAtualizados.containsKey("processosErroCertidao")) {
            details.setProcessosErroCertidao((Integer) camposAtualizados.get("processosErroCertidao"));
        }

        if (camposAtualizados.containsKey("processosTotais")) {
            details.setProcessosTotais((Integer) camposAtualizados.get("processosTotais"));
        }

        if (camposAtualizados.containsKey("percentualErros")) {
            Object valor = camposAtualizados.get("percentualErros");
            if (valor instanceof Number) {
                details.setPercentualErros(((Number) valor).doubleValue());
            }
        }

        details.setDataHoraAtualizacao(LocalDateTime.now());

        DetailsProcesses atualizado = detailsProcessesRepository.save(details);

        return ResponseEntity.ok(new DetailsProcessesDTO(atualizado));
    }




    //Filtros :


    //✅ 1. Filtrar por intervalo de data - retornar todos os registros entre duas datas, útil para análises históricas.
    @GetMapping("/periodo")
    public List<DetailsProcessesDTO> buscarPorPeriodo(
            @RequestParam String inicio,
            @RequestParam String fim
    ){

        LocalDateTime dataInicio = LocalDate.parse(inicio).atStartOfDay();
        LocalDateTime dataFim = LocalDate.parse(fim).atTime(LocalTime.MAX);

        List<DetailsProcesses> resultados = detailsProcessesRepository.findByDataHoraCriacaoBetween(dataInicio, dataFim);


        return resultados.stream().map(DetailsProcessesDTO::new).toList();
    }


    @GetMapping("/relatorio-pdf")
    public void gerarRelatorioPdfPorPeriodoComHtml(
            @RequestParam String inicio,
            @RequestParam String fim,
            HttpServletResponse response
    ) throws IOException {

        LocalDateTime dataInicio = LocalDate.parse(inicio).atStartOfDay();
        LocalDateTime dataFim = LocalDate.parse(fim).atTime(LocalTime.MAX);

        List<DetailsProcesses> registros = detailsProcessesRepository.findByDataHoraCriacaoBetween(dataInicio, dataFim);

        // ORDENA por data de criação ASCENDENTE (mais antiga primeiro)
        registros.sort(Comparator.comparing(DetailsProcesses::getDataHoraCriacao));



        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String logoSvgBase64 = encodeSvgToBase64("static/logo.svg");

        StringBuilder html = new StringBuilder();
        html.append("""
        <!DOCTYPE html>
        <html><head>
        <meta charset="UTF-8" />
                <style>
                  body {
                    font-family: sans-serif;
                    padding: 40px;
                    color: #333;
                    background-color: #ffffff;
                  }
                
                  .logo {
                    display: block;
                    margin: 0 auto 30px auto;
                    width: 160px;
                  }
                
                  h1 {
                    text-align: center;
                    color: #004A99;
                    font-size: 26pt;
                    margin-bottom: 10px;
                  }
                
                  h3 {
                    text-align: center;
                    font-size: 14pt;
                    margin-top: 0;
                    color: #555;
                  }
                
                  .periodo {
                    text-align: center;
                    margin-top: 20px;
                    font-size: 12pt;
                    color: #444;
                    font-weight: bold;
                  }
                
                  table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 40px;
                    font-size: 10pt;
                  }
                
                  th {
                    background-color: #004A99;
                    color: #fff;
                    padding: 10px;
                    border: 1px solid #ddd;
                  }
                
                  td {
                    padding: 8px;
                    border: 1px solid #ccc;
                    text-align: center;
                  }
                
                  tr:nth-child(even) {
                    background-color: #f9f9f9;
                  }
                
                  tr:hover {
                    background-color: #f1f1f1;
                  }
                
                  .footer {
                    text-align: center;
                    font-size: 9pt;
                    color: #888;
                    margin-top: 60px;
                  }
                </style>
                
        </head><body>
    """);

        html.append("<img src='data:image/svg+xml;base64,").append(logoSvgBase64).append("' class='logo' />");
        html.append("<h1>Relatório de Execução de Processos</h1>");
        html.append("<h3>Recebidos Pela CACE TI e Enviados aos Colaboradores</h3>");
        html.append("<div class='periodo'>Período: ")
                .append(dataInicio.format(formatter))
                .append(" até ")
                .append(dataFim.format(formatter))
                .append("</div>");

        html.append("<table>");
        html.append("<thead><tr>");
        html.append("<th>Data</th><th>Verificar</th><th>Renajud</th><th>Infojud</th>");
        html.append("<th>Erro Certidão</th><th>Totais</th><th>Erro (%)</th>");
        html.append("</tr></thead><tbody>");

        for (DetailsProcesses dp : registros) {
            html.append("<tr>")
                    .append("<td>").append(dp.getDataHoraCriacao().toLocalDate().format(formatter)).append("</td>")
                    .append("<td>").append(dp.getProcessosVerificar()).append("</td>")
                    .append("<td>").append(dp.getProcessosRenajud()).append("</td>")
                    .append("<td>").append(dp.getProcessosInfojud()).append("</td>")
                    .append("<td>").append(dp.getProcessosErroCertidao()).append("</td>")
                    .append("<td>").append(dp.getProcessosTotais()).append("</td>")
                    .append("<td>").append(String.format("%.2f%%", dp.getPercentualErros())).append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table>");
        html.append("<div class='footer'>Relatório gerado automaticamente por CACE TI</div>");
        html.append("</body></html>");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio-processos.pdf");

        try (OutputStream out = response.getOutputStream()) {
            HtmlConverter.convertToPdf(html.toString(), out);
        }
    }



    @GetMapping("/relatorio-processos-pdf")
    public void gerarRelatorioDeProcessosPdf(
            @RequestParam String inicio,
            @RequestParam String fim,
            HttpServletResponse response
    ) throws IOException {

        LocalDateTime dataInicio = LocalDate.parse(inicio).atStartOfDay();
        LocalDateTime dataFim = LocalDate.parse(fim).atTime(LocalTime.MAX);

        List<RelatorioProcessoDTO> processos = processoRepository.buscarRelatorioEntreDatas(dataInicio, dataFim);

        processos.sort(Comparator.comparing(RelatorioProcessoDTO::getDataCriacao));// ordena por data

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String logoSvgBase64 = encodeSvgToBase64("static/logo.svg");

        StringBuilder html = new StringBuilder();
        html.append("""
    <!DOCTYPE html>
    <html><head>
    <meta charset="UTF-8" />
    <style>
      body {
        font-family: sans-serif;
        padding: 40px;
        color: #333;
        background-color: #ffffff;
      }
      .logo {
        display: block;
        margin: 0 auto 30px auto;
        width: 160px;
      }
      h1 {
        text-align: center;
        color: #004A99;
        font-size: 26pt;
        margin-bottom: 10px;
      }
      h3 {
        text-align: center;
        font-size: 14pt;
        margin-top: 0;
        color: #555;
      }
      .periodo {
        text-align: center;
        margin-top: 20px;
        font-size: 12pt;
        color: #444;
        font-weight: bold;
      }
      table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 40px;
        font-size: 10pt;
      }
      th {
        background-color: #004A99;
        color: #fff;
        padding: 10px;
        border: 1px solid #ddd;
      }
      td {
        padding: 8px;
        border: 1px solid #ccc;
        text-align: center;
      }
      tr:nth-child(even) {
        background-color: #f9f9f9;
      }
      tr:hover {
        background-color: #f1f1f1;
      }
      .footer {
        text-align: center;
        font-size: 9pt;
        color: #888;
        margin-top: 60px;
      }
    </style>
    </head><body>
    """);

        html.append("<img src='data:image/svg+xml;base64,").append(logoSvgBase64).append("' class='logo' />");
        html.append("<h1>Relatório de Processos Judiciais</h1>");
        html.append("<h3>Gerado pela API - CACE TI</h3>");
        html.append("<div class='periodo'>Período: ")
                .append(dataInicio.format(formatter))
                .append(" até ")
                .append(dataFim.format(formatter))
                .append("</div>");

        html.append("<table>");
        html.append("<thead><tr>");
        html.append("<th>Data</th><th>Nº Processo</th><th>Polo Ativo</th><th>Polo Passivo</th>");
        html.append("<th>Serventia</th><th>Responsável</th><th>Status</th>");
        html.append("</tr></thead><tbody>");

        for (RelatorioProcessoDTO p : processos) {
            html.append("<tr>")
                    .append("<td>").append(p.getDataCriacao() != null ? p.getDataCriacao().format(formatter) : "-").append("</td>")
                    .append("<td>").append(p.getNumeroCompleto()).append("</td>")
                    .append("<td>").append(p.getPoloAtivoNome() != null ? p.getPoloAtivoNome() : "-").append("</td>")
                    .append("<td>").append(p.getPoloPassivoNome() != null ? p.getPoloPassivoNome() : "-").append("</td>")
                    .append("<td>").append(p.getServentia()).append("</td>")
                    .append("<td>").append(p.getResponsavel()).append("</td>")
                    .append("<td>").append(p.getStatus()).append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table>");
        html.append("<div class='footer'>Relatório gerado automaticamente por CACE TI</div>");
        html.append("</body></html>");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio-processos.pdf");

        try (OutputStream out = response.getOutputStream()) {
            HtmlConverter.convertToPdf(html.toString(), out);
            System.out.println("✅ PDF gerado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro ao gerar PDF: " + e.getMessage());
        }

    }









    private String encodeSvgToBase64(String path) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new FileNotFoundException("SVG não encontrado no classpath: " + path);
            byte[] bytes = is.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        }
    }





//
}