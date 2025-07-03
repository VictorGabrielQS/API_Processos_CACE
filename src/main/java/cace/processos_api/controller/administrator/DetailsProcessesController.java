package cace.processos_api.controller.administrator;

import cace.processos_api.dto.administrator.DetailsProcessesDTO;
import cace.processos_api.model.administrator.DetailsProcesses;
import cace.processos_api.repository.administrator.DetailsProcessesRepository;
import cace.processos_api.service.administrator.DetailsProcessesService;
import java.io.IOException;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


//Itext PDF imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;



@RestController
@RequestMapping("/details-processes")
public class DetailsProcessesController {


    @Autowired
    private DetailsProcessesRepository detailsProcessesRepository;


    @Autowired
    private DetailsProcessesService service;


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


    //✅ 1. Filtrar por intervalo de dataa - retornar todos os registros entre duas datas, útil para análises históricas.
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
    public void gerarRelatorioPdfPorPeriodo(
            @RequestParam String inicio,
            @RequestParam String fim,
            HttpServletResponse response
    ) throws java.io.IOException {

        LocalDateTime dataInicio = LocalDate.parse(inicio).atStartOfDay();
        LocalDateTime dataFim = LocalDate.parse(fim).atTime(LocalTime.MAX);

        List<DetailsProcesses> registros = detailsProcessesRepository.findByDataHoraCriacaoBetween(dataInicio, dataFim);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio-processos.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // LOGO (ajuste o caminho conforme a pasta do projeto)
        String caminhoLogo = "src/main/resources/static/logo.png";
        ImageData imageData = ImageDataFactory.create(caminhoLogo);
        Image logo = new Image(imageData).scaleToFit(80, 80).setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(logo);

        // Nome da Central
        Paragraph tituloCentral = new Paragraph("Central de Atos de Constrições Eletrônicas - CACE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12)
                .setMarginBottom(20);
        document.add(tituloCentral);

        // Título do Relatório
        Paragraph titulo = new Paragraph("RELATÓRIO " + inicio.toUpperCase() + " / " + fim.split("-")[0])
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16)
                .setUnderline()
                .setMarginBottom(20);
        document.add(titulo);

        // Resumo (simples)
        int totalProcessos = registros.stream().mapToInt(DetailsProcesses::getProcessosTotais).sum();
        Paragraph resumo = new Paragraph("📈 Total de Processos: " + totalProcessos)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(10);
        document.add(resumo);

        // Tabela
        float[] columnWidths = {100F, 70F, 70F, 70F, 80F, 80F, 70F};
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell("Data");
        table.addHeaderCell("Verificar");
        table.addHeaderCell("Renajud");
        table.addHeaderCell("Infojud");
        table.addHeaderCell("Erro Cert.");
        table.addHeaderCell("Totais");
        table.addHeaderCell("Erro (%)");

        for (DetailsProcesses dp : registros) {
            table.addCell(dp.getDataHoraCriacao().toLocalDate().toString());
            table.addCell(String.valueOf(dp.getProcessosVerificar()));
            table.addCell(String.valueOf(dp.getProcessosRenajud()));
            table.addCell(String.valueOf(dp.getProcessosInfojud()));
            table.addCell(String.valueOf(dp.getProcessosErroCertidao()));
            table.addCell(String.valueOf(dp.getProcessosTotais()));
            table.addCell(String.format("%.2f%%", dp.getPercentualErros()));
        }

        document.add(table);
        document.close();
    }



}