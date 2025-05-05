package cace.processos_api.controller;

import cace.processos_api.dto.PoloDetalhadoDTO;
import cace.processos_api.dto.ProcessoDTO;
import cace.processos_api.exception.ApiResponseException;
import cace.processos_api.model.Usuario;
import cace.processos_api.repository.UsuarioRepository;
import cace.processos_api.service.PoloAtivoService;
import cace.processos_api.service.PoloPassivoService;
import cace.processos_api.service.ProcessoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/adm")
@RequiredArgsConstructor
public class AdmController {

    private final UsuarioRepository usuarioRepository;
    private final ProcessoService processoService;
    private  final PoloPassivoService poloPassivoService;
    private final PoloAtivoService poloAtivoService;

    //Usuarios :

    //Retorna todos os usuarios cadastrados no sistema
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios (){
        return ResponseEntity.ok(usuarioRepository.findAll());
    }


    //Deletar usuario do sistema
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletarUsuario (@PathVariable Long id){
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()){
            Usuario usuarioDeletado = usuarioOptional.get();
            usuarioRepository.deleteById(id);
            return ResponseEntity.ok()
                    .body(new ApiResponseException("Usuário deletado com Sucesso : " , usuarioDeletado)); // Retorna o usuario deletado
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseException("Usuário não encontrado", null)); // Se não encontrar o usuario
        }

    }



    //Processos :

    // Rota para deletar Processo por número curto
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/delete-processo-curto/{numeroCurto}")
    public ResponseEntity<Void> deleteProcessoNumeroCurto(@PathVariable String numeroCurto){
        processoService.deleteProcessoByNumeroCurto(numeroCurto);
        return ResponseEntity.noContent().build();

    }


    // Rota para deletar Processo por número curto/completo
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/delete-processo-completo/{numeroCompleto}")
    public ResponseEntity<Void> deleteProcessoNumeroCompleto(@PathVariable String numeroCompleto){
        processoService.deleteProcessoByNumeroCompleto(numeroCompleto);
        return ResponseEntity.noContent().build();

    }


    // Rota para atualizar status de um Processo
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/status/{numeroCurto}/{novoStatus}")
    public ResponseEntity<ProcessoDTO> updateStatus(@PathVariable String numeroCurto, @PathVariable String novoStatus){
        ProcessoDTO processo = processoService.updateStatus(numeroCurto, novoStatus);
        return ResponseEntity.ok(processo);
    }


    // Rota para atualizar Responsavel de um Processo
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/responsavel/{numeroCurto}/{novoResponsavel}")
    public ResponseEntity<ProcessoDTO> updateResponsavel(@PathVariable String numeroCurto, @PathVariable String novoResponsavel){
        ProcessoDTO processo = processoService.updateResponsavel(numeroCurto, novoResponsavel);
        return ResponseEntity.ok(processo);
    }





    //Polo Passivo :


    // Rota deleta polo passivo por cpf/cnpj
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/delete-poloPassivoCpfCnpj/{cpfCnpj}")
    public ResponseEntity<Void> deletePoloPassivo(@PathVariable String cpfCnpj ){
        poloPassivoService.deletePolo(cpfCnpj);
        return ResponseEntity.noContent().build();
    }


    // Rota deleta polo passivo por id
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/delete-poloPassivoId/{id}")
    public ResponseEntity<Void> deletePoloPassivo(@PathVariable Long id ){
        poloPassivoService.deletePoloId(id);
        return ResponseEntity.noContent().build();
    }




    //Polo Ativo :


    // Rota deleta polo Ativo por cpf/cnpj
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/delete-poloAtivoCpfCnpj/{cpfCnpj}")
    public ResponseEntity<Void> deletePoloAtivo(@PathVariable String cpfCnpj){
        poloAtivoService.deletePolo(cpfCnpj);
        return ResponseEntity.noContent().build();
    }

    // Rota deleta polo Ativo por id
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/delete-poloAtivoId/{id}")
    public  ResponseEntity<Void> deletePoloAtivoId(@PathVariable Long id ){
        poloAtivoService.deletePoloId(id);
        return ResponseEntity.noContent().build();
    }





    // 🪪 - Dashboard



    //1. Buscar Processos por Responsavel
    @GetMapping("/buscar-porResponsavel/{responsavel}")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByResponsavel(@PathVariable String responsavel){
        List<ProcessoDTO> processos = processoService.getProcessoByResponsavel(responsavel);
        return ResponseEntity.ok(processos);
    }

    //1. Buscar Processos por Serventia
    @GetMapping("/buscar-porServentia/{serventia}")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByServentia(@PathVariable String serventia){
        List<ProcessoDTO> processos = processoService.getProcessoByServentia(serventia);
        return ResponseEntity.ok(processos);
    }


    //1. Buscar Processos por Tipo Certidao
    @GetMapping("/buscar-porCertidao/{tipoCertidao}")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByTipoCertidao(@PathVariable String tipoCertidao){
        List<ProcessoDTO> processos = processoService.getProcessoByTipoCertidao(tipoCertidao);
        return ResponseEntity.ok(processos);
    }


    //1. Buscar Processos por Situação/Status
    @GetMapping("/buscar-porStatus/{status}")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByStatus(@PathVariable String status){
        List<ProcessoDTO> processos = processoService.getProcessoByStatus(status);
        return ResponseEntity.ok(processos);
    }



    //1. Buscar Processos criados em um Periodo
    @GetMapping("/buscar-porPeriodo")
    public ResponseEntity<List<ProcessoDTO>>  getProcessosByPeriodo( @RequestParam("dataInicio") String dataInicio, @RequestParam("dataFim") String dataFim){
        List<ProcessoDTO> processos = processoService.getProcessoByPeriodo(dataInicio , dataFim);
        return ResponseEntity.ok(processos);
    }




    //Polo ativo detalhado

    @GetMapping("/polo-ativo/detalhado/{cpfCnpj}")
    public ResponseEntity<PoloDetalhadoDTO> getDetalhadoAtivo(@PathVariable String cpfCnpj) {
        return ResponseEntity.ok(poloAtivoService.getDetalhadoByCpfCnpj(cpfCnpj));
    }


    @PutMapping("/detalhado-ativo/{id}")
    public ResponseEntity<PoloDetalhadoDTO> atualizarPoloAtivo(@PathVariable Long id, @RequestBody PoloDetalhadoDTO dto) {
        PoloDetalhadoDTO atualizado = poloAtivoService.updatePoloAtivo(id, dto);
        return ResponseEntity.ok(atualizado);
    }


    //Polo Passivo detalhado

    @GetMapping("/polo-passivo/detalhado/{cpfCnpj}")
    public ResponseEntity<PoloDetalhadoDTO> getDetalhadoPassivo(@PathVariable String cpfCnpj) {
        return ResponseEntity.ok(poloPassivoService.getDetalhadoByCpfCnpj(cpfCnpj));
    }


    @PutMapping("/detalhado-passivo/{id}")
    public ResponseEntity<PoloDetalhadoDTO> atualizarPoloPassivo(@PathVariable Long id, @RequestBody PoloDetalhadoDTO dto) {
        PoloDetalhadoDTO atualizado = poloPassivoService.updatePoloPassivo(id, dto);
        return ResponseEntity.ok(atualizado);
    }





}
