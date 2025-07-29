package cace.processos_api.controller;

import cace.processos_api.dto.ProcessoDTO;
import cace.processos_api.exception.ApiResponseException;
import cace.processos_api.model.process.Usuario;
import cace.processos_api.repository.UsuarioRepository;
import cace.processos_api.util.AuthUtil;
import cace.processos_api.service.ProcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/adm" , produces = {"application/json"})
@Tag(name = "API-processos")
@RequiredArgsConstructor
public class AdmController {

    private final UsuarioRepository usuarioRepository;
    private final ProcessoService processoService;





    //Usuarios :

    //Retorna todos os usuarios cadastrados no sistema

    @Operation(
            summary = "Retorna todos os usuários cadastrados no sistema.",
            description = "Requer nível de acesso 1. Retorna uma lista de todos os usuários.",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Usuario.class)))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado por falta de permissão.")
            }
    )
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios (){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        return ResponseEntity.ok(usuarioRepository.findAll());
    }


    //Deletar usuario do sistema : Customize Toolbar…
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletarUsuario (@PathVariable Long id){

        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()){
            Usuario usuarioDeletado = usuarioOptional.get();

            if ("admin".equalsIgnoreCase(usuarioDeletado.getUsername())){
                return  ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseException("Não é permitido Deletar o usuário administrador (admin).", null));
            }

            usuarioRepository.deleteById(id);
            return ResponseEntity.ok()
                    .body(new ApiResponseException("Usuário deletado com Sucesso : " , usuarioDeletado)); // Retorna o usuario deletado
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseException("Usuário não encontrado", null)); // Se não encontrar o usuario
        }

    }


    //Trocar o nível do usuário: Atualiza o nível de acesso de um usuário pelo id.
    @PutMapping("/nivel/{id}")
    public ResponseEntity<?> atualizarNivelAcesso(@PathVariable Long id, @RequestParam int novoNivel) {
        AuthUtil.validarAcesso(1,2); // Apenas usuários com nível 1 podem acessar
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



    //Troca email de um usuario
    @Operation(
            summary = "Atualiza o e-mail de um usuário.",
            description = "Requer nível de acesso 1 ou 2. Valida formato e unicidade do e-mail antes da atualização.",
            method = "PUT",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"novoEmail\": \"novo@email.com\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "E-mail atualizado com sucesso.",
                            content = @Content(schema = @Schema(implementation = ApiResponseException.class))),
                    @ApiResponse(responseCode = "400", description = "E-mail inválido ou não fornecido.",
                            content = @Content(schema = @Schema(implementation = ApiResponseException.class))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                            content = @Content(schema = @Schema(implementation = ApiResponseException.class))),
                    @ApiResponse(responseCode = "409", description = "E-mail já está em uso.",
                            content = @Content(schema = @Schema(implementation = ApiResponseException.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado por falta de permissão.")
            }
    )
    @PutMapping("/email/{id}")
    public ResponseEntity<?> atualizarEmail(@PathVariable Long id, @RequestBody Map<String, String> body) {

        // Verifica se o usuário autenticado tem permissão (nível 1 ou 2)
        AuthUtil.validarAcesso(1, 2);

        // Recupera o novo e-mail do corpo da requisição
        String novoEmail = body.get("novoEmail");

        // Valida se o novo e-mail foi fornecido
        if (novoEmail == null || novoEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseException("O campo 'novoEmail' é obrigatório.", null));
        }

        // Valida o formato do e-mail
        if (!novoEmail.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseException("Formato de e-mail inválido.", null));
        }

        // Verifica se o e-mail já está em uso
        if (usuarioRepository.findByEmail(novoEmail).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponseException("Este e-mail já está em uso por outro usuário.", null));
        }

        // Busca o usuário pelo ID
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseException("Usuário não encontrado.", null));
        }

        // Atualiza o e-mail do usuário
        Usuario usuario = usuarioOptional.get();
        usuario.setEmail(novoEmail);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok()
                .body(new ApiResponseException("E-mail atualizado com sucesso!", usuario));

    }






    //Processos :


    // Rota para deletar Processo por número curto
    @Operation(
            summary = "Deleta um Processo pelo número curto.",
            description = "Remove permanentemente um processo identificado pelo número curto do sistema. Requer nível de acesso 1.",
            method = "DELETE",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Processo deletado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
            }
    )
    @DeleteMapping("/delete-processo-curto/{numeroCurto}")
    public ResponseEntity<Void> deleteProcessoNumeroCurto(@PathVariable String numeroCurto){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        processoService.deleteProcessoByNumeroCurto(numeroCurto);
        return ResponseEntity.noContent().build();

    }


    // Rota para deletar Processo por número completo
    @Operation(
            summary = "Deleta um Processo pelo número completo.",
            description = "Remove permanentemente um processo identificado pelo número completo. Requer nível de acesso 1.",
            method = "DELETE",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Processo deletado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
            }
    )
    @DeleteMapping("/delete-processo-completo/{numeroCompleto}")
    public ResponseEntity<Void> deleteProcessoNumeroCompleto(@PathVariable String numeroCompleto){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        processoService.deleteProcessoByNumeroCompleto(numeroCompleto);
        return ResponseEntity.noContent().build();

    }


    // Rota para atualizar status de um Processo
    @Operation(
            summary = "Atualiza o status de um Processo.",
            description = "Atualiza o campo de status de um processo identificado pelo número curto. Requer nível de acesso 1.",
            method = "PUT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                            content = @Content(schema = @Schema(implementation = ProcessoDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
            }
    )
    @PutMapping("/status/{numeroCurto}/{novoStatus}")
    public ResponseEntity<ProcessoDTO> updateStatus(@PathVariable String numeroCurto, @PathVariable String novoStatus){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        ProcessoDTO processo = processoService.updateStatus(numeroCurto, novoStatus);
        return ResponseEntity.ok(processo);
    }


    // Rota para atualizar Responsavel de um Processo
    @Operation(
            summary = "Atualiza o responsável de um Processo.",
            description = "Altera o nome do responsável por um processo identificado pelo ID. Requer nível de acesso 1.",
            method = "PUT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Responsável atualizado com sucesso",
                            content = @Content(schema = @Schema(implementation = ProcessoDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
            }
    )
    @PutMapping("/responsavel/{numeroCurto}/{novoResponsavel}")
    public ResponseEntity<ProcessoDTO> updateResponsavel(@PathVariable Long id, @PathVariable String novoResponsavel){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        ProcessoDTO processo = processoService.updateResponsavel(id , novoResponsavel);
        return ResponseEntity.ok(processo);
    }









    // 🪪 - Dashboard



    //1. Buscar Processos por Responsavel
    @Operation(
            summary = "Buscar processos por responsável",
            description = "Retorna uma lista de processos atribuídos ao responsável informado. Requer nível de acesso 1.",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de processos retornada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado")
            }
    )
    @GetMapping("/buscar-porResponsavel")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByResponsavel(@RequestParam String responsavel) {
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        List<ProcessoDTO> processos = processoService.getProcessoByResponsavel(responsavel);
        return ResponseEntity.ok(processos);
    }



    // Buscar Processos por Serventia
    @Operation(
            summary = "Buscar processos por serventia",
            description = "Retorna uma lista de processos que pertencem à serventia informada. Requer nível de acesso 1.",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de processos retornada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado")
            }
    )
    @GetMapping("/buscar-porServentia")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByServentia(@RequestParam String serventia) {
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        List<ProcessoDTO> processos = processoService.getProcessoByServentia(serventia);
        return ResponseEntity.ok(processos);
    }


    // Buscar Processos por Tipo de Certidão
    @Operation(
            summary = "Buscar processos por tipo de certidão",
            description = "Retorna os processos filtrados pelo tipo de certidão. Requer nível de acesso 1.",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de processos retornada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado")
            }
    )
    @GetMapping("/buscar-porCertidao")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByTipoCertidao(@RequestParam String tipoCertidao) {
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        List<ProcessoDTO> processos = processoService.getProcessoByTipoCertidao(tipoCertidao);
        return ResponseEntity.ok(processos);
    }


    // Buscar Processos por Situação/Status
    @Operation(
            summary = "Buscar processos por status",
            description = "Retorna os processos com o status especificado. Requer nível de acesso 1.",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de processos retornada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado")
            }
    )
    @GetMapping("/buscar-porStatus")
    public ResponseEntity<List<ProcessoDTO>> getProcessosByStatus(@RequestParam String status) {
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        List<ProcessoDTO> processos = processoService.getProcessoByStatus(status);
        return ResponseEntity.ok(processos);
    }



    //1. Buscar Processos criados em um Periodo
    @Operation(
            summary = "Buscar processos por período",
            description = "Retorna os processos criados entre duas datas (formato esperado: yyyy-MM-dd). Requer nível de acesso 1.",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de processos retornada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "400", description = "Parâmetros de data inválidos")
            }
    )
    @GetMapping("/buscar-porPeriodo")
    public ResponseEntity<List<ProcessoDTO>>  getProcessosByPeriodo( @RequestParam("dataInicio") String dataInicio, @RequestParam("dataFim") String dataFim){
        AuthUtil.validarAcesso(1); // Apenas usuários com nível 1 podem acessar
        List<ProcessoDTO> processos = processoService.getProcessoByPeriodo(dataInicio , dataFim);
        return ResponseEntity.ok(processos);
    }









}
