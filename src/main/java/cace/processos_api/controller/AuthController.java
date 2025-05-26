package cace.processos_api.controller;


import cace.processos_api.config.WebConfig;
import cace.processos_api.dto.*;
import cace.processos_api.exception.ApiResponseException;
import cace.processos_api.model.Usuario;
import cace.processos_api.repository.PasswordResetTokenRepository;
import cace.processos_api.repository.UsuarioRepository;
import cace.processos_api.service.JwtService;
import cace.processos_api.service.UsuarioDetailsService;
import cace.processos_api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private  final UsuarioDetailsService usuarioDetailsService;

    @Autowired
    private final EmailService emailService;

    @Autowired
    private final WebConfig webConfig;


    //Registra usuario
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario (@RequestBody RegisterRequest request) {

        // Verifica se username ou senha estão vazios
        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseException("Usuário ou senha não podem ser vazios!", null));
        }

        // Expressão regular: apenas letras e números
        String regex = "^[a-zA-Z0-9]+$";

        if (!request.getUsername().matches(regex) || !request.getPassword().matches(regex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseException("Usuário e senha não podem conter caracteres especiais!", null));
        }

        var usuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .cpf(request.getCpf())
                .email(request.getEmail())
                .nivelAcesso(3) // padrão
                .build();

        usuarioRepository.save(usuario);

        var jwtToken = jwtService.generateToken(usuario);

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwtToken)
                .nivelAcesso(usuario.getNivelAcesso())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseException("Usuário registrado com sucesso!", authResponse));
    }





    //Authentica o usuario e gera o token JWT
    @PostMapping("/authenticate")
    public ResponseEntity<?> autenticarUsuario (@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow();

        // ✅ Se nível de acesso for 3, exige troca de senha antes de permitir login normal
        if (usuario.getNivelAcesso() == 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseException(
                            "Usuário precisa redefinir a senha antes de acessar o sistema.",
                            Map.of("nivelAcesso", usuario.getNivelAcesso())
                    ));
        }

        var jwtToken = jwtService.generateToken(usuario);

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwtToken)
                .nivelAcesso(usuario.getNivelAcesso())
                .build();

        return ResponseEntity.ok(authResponse);
    }




    // ✅ 1. Solicitação de redefinição (esqueci a senha)
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest request) {



        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));


        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(usuario.getUsername());


        Map<String, Object> claims = new HashMap<>();
        claims.put("reset", true); // claim personalizado


        String token = jwtService.generateResetToken(claims, userDetails);


        String url =  webConfig.getFrontendUrl() + "/redefinir-senha?token=" + token;
        emailService.sendResetToken(request.getEmail(), url);

        return ResponseEntity.ok(" Link de Redefinição enviado com sucesso ! .");

    }




    // ✅ 2. Redefinir senha com token
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {

        String token = request.getToken();

        String username = jwtService.extractResetUsername(token);
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(username);

        if (!jwtService.isResetTokenValid(token, userDetails)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido ou expirado");
        }

        // Expressão regular: apenas letras e números
        String regex = "^[a-zA-Z0-9]+$";
        if (request.getNovaSenha() == null || request.getNovaSenha().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A nova senha não pode estar vazia.");
        }

        if (!request.getNovaSenha().matches(regex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A nova senha não pode conter caracteres especiais.");
        }

        var usuario = usuarioRepository.findByUsername(username).orElseThrow();

        usuario.setPassword(passwordEncoder.encode(request.getNovaSenha()));
        usuario.setNivelAcesso(2); // Atualiza acesso após redefinir
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Senha alterada com sucesso.");
    }




    //Envio de envio de email teste
    @PostMapping("/email-teste")
    public ResponseEntity<String> emailTeste(@RequestBody EmailRequest request) {
        emailService.sendResetToken(request.getEmail(), "token-de-teste-123");
        return ResponseEntity.ok("E-mail de teste enviado para " + request.getEmail() + "!");
    }


}