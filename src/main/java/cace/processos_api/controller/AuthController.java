package cace.processos_api.controller;


import cace.processos_api.dto.AuthRequest;
import cace.processos_api.dto.AuthResponse;
import cace.processos_api.dto.ForgotPasswordRequest;
import cace.processos_api.exception.ApiResponseException;
import cace.processos_api.model.PasswordResetToken;
import cace.processos_api.model.RegisterRequest;
import cace.processos_api.model.ResetPasswordRequest;
import cace.processos_api.model.Usuario;
import cace.processos_api.repository.PasswordResetTokenRepository;
import cace.processos_api.repository.UsuarioRepository;
import cace.processos_api.security.JwtService;
import cace.processos_api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private EmailService emailService;



    //Registra usuario
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario (@RequestBody RegisterRequest request) {

        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()
        ){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseException("Usuário ou senha não podem ser vazios ! " , null));
        }

        var usuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .cpf(request.getCpf())
                .nivelAcesso(2) // ✅ Definido como nível 3 por padrão
                .build();

        usuarioRepository.save(usuario);

        var jwtToken = jwtService.generateToken(usuario);

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwtToken)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseException("Usuario registrado com sucesso ! " , authResponse));
    }



    //Authentica o usuario e gera o token JWT
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> autenticarUsuario (@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(usuario);
        return ResponseEntity.ok(AuthResponse.builder().token(jwtToken).build());
    }



    // ✅ 1. Solicitação de redefinição (esqueci a senha)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String cpf = request.getCpf();
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new ApiResponseException("CPF não encontrado", null));

        // Gera e salva o token...
        String token = UUID.randomUUID().toString();
        // (salvar em PasswordResetToken...)

        // ENVIA o token por e-mail:
        try {
            emailService.sendResetToken(usuario.getEmail(), token);
        } catch (Exception e) {
            // log e resposta de erro adequado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseException("Erro ao enviar e-mail", null));
        }

        return ResponseEntity.ok(new ApiResponseException(
                "Token enviado para o e-mail associado ao CPF.", null
        ));
    }



    // ✅ 2. Redefinir senha com token
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken())
                .orElse(null);

        if (token == null || token.getExpirationDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseException("Token inválido ou expirado", null));
        }

        Usuario usuario = token.getUsuario();
        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);
        passwordResetTokenRepository.delete(token);

        return ResponseEntity.ok(new ApiResponseException("Senha redefinida com sucesso.", null));
    }


    //3. Implemente um “endpoint de teste”
    @GetMapping("/email-teste")
    public ResponseEntity<String> emailTeste() {
        emailService.sendResetToken("victor.git24@gmail.com", "token-de-teste-123");
        return ResponseEntity.ok("E-mail de teste enviado!");
    }


}