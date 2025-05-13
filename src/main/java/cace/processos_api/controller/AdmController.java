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
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios (){
        return ResponseEntity.ok(usuarioRepository.findAll());
    }


    //Deletar usuario do sistema
    @PreAuthorize("hasRole('NIVEL_2')")
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


    //Trocar o nível do usuário:
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/nivel/{id}")
    public ResponseEntity<?> atualizarNivelAcesso(@PathVariable Long id, @RequestParam int novoNivel) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setNivelAcesso(novoNivel); // 👈 Altera o nível
            usuarioRepository.save(usuario);

            return ResponseEntity.ok()
                    .body(new ApiResponseException("Nível de acesso atualizado com sucesso!", usuario));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseException("Usuário não encontrado", null));
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
    public ResponseEntity<ProcessoDTO> updateResponsavel(@PathVariable Long id, @PathVariable String novoResponsavel){
        ProcessoDTO processo = processoService.updateResponsavel(id , novoResponsavel);
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
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/buscar-porResponsavel")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByResponsavel(@RequestParam String responsavel) {
        List<ProcessoDTO> processos = processoService.getProcessoByResponsavel(responsavel);
        return ResponseEntity.ok(processos);
    }



    // Buscar Processos por Serventia
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/buscar-porServentia")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByServentia(@RequestParam String serventia) {
        List<ProcessoDTO> processos = processoService.getProcessoByServentia(serventia);
        return ResponseEntity.ok(processos);
    }


    // Buscar Processos por Tipo de Certidão
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/buscar-porCertidao")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByTipoCertidao(@RequestParam String tipoCertidao) {
        List<ProcessoDTO> processos = processoService.getProcessoByTipoCertidao(tipoCertidao);
        return ResponseEntity.ok(processos);
    }


    // Buscar Processos por Situação/Status
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/buscar-porStatus")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByStatus(@RequestParam String status) {
        List<ProcessoDTO> processos = processoService.getProcessoByStatus(status);
        return ResponseEntity.ok(processos);
    }



    //1. Buscar Processos criados em um Periodo
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/buscar-porPeriodo")
    public ResponseEntity<List<ProcessoDTO>>  getProcessosByPeriodo( @RequestParam("dataInicio") String dataInicio, @RequestParam("dataFim") String dataFim){
        List<ProcessoDTO> processos = processoService.getProcessoByPeriodo(dataInicio , dataFim);
        return ResponseEntity.ok(processos);
    }




    //Polo ativo detalhado

    // Buscar Polo Ativo detalhado por CPF/CNPJ
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/polo-ativo/detalhado/byCpfCnpj")
    public ResponseEntity<PoloDetalhadoDTO> getDetalhadoAtivoByCpfCnpj(@RequestParam String cpfCnpj) {
        return ResponseEntity.ok(poloAtivoService.getDetalhadoByCpfCnpj(cpfCnpj));
    }


    // Buscar Todos Polo Ativo detalhados
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/polo-ativo/detalhado/todos")
    public ResponseEntity<List<PoloDetalhadoDTO>> getAllPoloAtivoDetalhado() {
        List<PoloDetalhadoDTO> polosDetalhados = poloAtivoService.getTodosPolosDetalhados();
        return ResponseEntity.ok(polosDetalhados);
    }


    // Buscar Polo Ativo detalhado por Id
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/polo-ativo/detalhado/byId")
    public ResponseEntity<PoloDetalhadoDTO> getDetalhadoAtivoById(@RequestParam Long id) {
        return ResponseEntity.ok(poloAtivoService.getDetalhadoById(id));
    }


    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/detalhado-ativo/{id}")
    public ResponseEntity<PoloDetalhadoDTO> atualizarPoloAtivo(@PathVariable Long id, @RequestBody PoloDetalhadoDTO dto) {
        PoloDetalhadoDTO atualizado = poloAtivoService.updatePoloAtivo(id, dto);
        return ResponseEntity.ok(atualizado);
    }


    //Polo Passivo

    // Buscar Todos Polo Passivo detalhados
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/polo-passivo/detalhado/todos")
    public ResponseEntity<List<PoloDetalhadoDTO>> getAllPoloPassivoDetalhado() {
        List<PoloDetalhadoDTO> poloDetalhadoDTOS = poloPassivoService.getTodosPolosDetalhados();
        return ResponseEntity.ok(poloDetalhadoDTOS);
    }



    // Buscar Polo Passivo detalhado por CPF/CNPJ
    @GetMapping("/polo-passivo/detalhado/byCpfCnpj")
    public ResponseEntity<PoloDetalhadoDTO> getDetalhadoPassivoByCpfCnpj(@RequestParam String cpfCnpj) {
        return ResponseEntity.ok(poloPassivoService.getDetalhadoByCpfCnpj(cpfCnpj));
    }


    // Buscar Polo Passivo detalhado por Id
    @GetMapping("/polo-passivo/detalhado/byId")
    public ResponseEntity<PoloDetalhadoDTO> getDetalhadoPassivoById(@RequestParam Long id) {
        return ResponseEntity.ok(poloPassivoService.getDetalhadoById(id));
    }



    @PutMapping("/detalhado-passivo/{id}")
    public ResponseEntity<PoloDetalhadoDTO> atualizarPoloPassivo(@PathVariable Long id, @RequestBody PoloDetalhadoDTO dto) {
        PoloDetalhadoDTO atualizado = poloPassivoService.updatePoloPassivo(id, dto);
        return ResponseEntity.ok(atualizado);
    }





}
