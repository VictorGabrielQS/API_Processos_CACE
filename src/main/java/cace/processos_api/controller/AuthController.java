package cace.processos_api.controller;


import cace.processos_api.config.WebConfig;
import cace.processos_api.dto.*;
import cace.processos_api.exception.ApiResponseException;
import cace.processos_api.model.process.Usuario;
import cace.processos_api.repository.PasswordResetTokenRepository;
import cace.processos_api.repository.UsuarioRepository;
import cace.processos_api.service.JwtService;
import cace.processos_api.service.TokenBlacklistService;
import cace.processos_api.service.UsuarioDetailsService;
import cace.processos_api.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/auth")

@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioDetailsService usuarioDetailsService;
    private final TokenBlacklistService blacklistService;

    @Autowired
    private final EmailService emailService;

    @Autowired
    private final WebConfig webConfig;


    //Registra usuario
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegisterRequest request) {

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
    public ResponseEntity<?> autenticarUsuario(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthRequest authRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        var usuario = usuarioRepository.findByUsername(authRequest.getUsername())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(usuario);

        // Cria o cookie HttpOnly
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .secure(true) // true em produção (https)
                .path("/")
                .maxAge(24 * 60 * 60) // 1 dia
                .sameSite("None") // usar "None" se front e back estiverem em domínios diferentes
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok(
                new ApiResponseException("Login realizado com sucesso!", null)
        );
    }



    // Desautentica o usuário e limpa o cookie JWT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        String token = jwtService.extractTokenFromRequest(request);

        if ( token != null){

            // Adiciona o token na blacklist por 1 hora (ou a duração do token)
            blacklistService.blacklistToken(token, Duration.ofHours(1));

        }

        // Limpa o cookie JWT
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok("Logout realizado com sucesso");
    }


    // Limpa a sessão do usuário, removendo o cookie JWT
    @PostMapping("/clear-session")
    public ResponseEntity<?> clearSession(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok("Session cleared");
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


        String url = webConfig.getFrontendUrl() + "/redefinir-senha?token=" + token;

        try {
            emailService.sendResetToken(request.getEmail(), url);
        } catch (MessagingException e) {
            // Aqui você pode logar ou lançar uma exceção customizada
            e.printStackTrace();
            throw new RuntimeException("Falha ao enviar e-mail de redefinição de senha.");
        }


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

        if ("senha".equalsIgnoreCase(usuario.getUsername())){
            usuario.setNivelAcesso(3);

        }else {
            usuario.setNivelAcesso(2); // Atualiza acesso após redefinir

        }



        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Senha alterada com sucesso.");
    }




    // ✅ 3. Redefinir senha primeiro Acesso
    @PostMapping("/first-access")
    public ResponseEntity<?> firstAccess(@RequestBody FirstAccessRequest request){
        String token = request.getToken();
        String senhaAtual = request.getSenhaAtual();
        String novaSenha = request.getNovaSenha();

        // Extrai o username do token JWT
        String username = jwtService.extractUsername(token);
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(token, userDetails)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido ou expirado.");
        }

        var usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Verifica se senha atual (provisória) está correta
        if (!passwordEncoder.matches(senhaAtual, usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha atual incorreta.");
        }

        // Expressão regular: apenas letras e números
        String regex = "^[a-zA-Z0-9]+$";

        if (novaSenha == null || novaSenha.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A nova senha não pode estar vazia.");
        }

        if (!novaSenha.matches(regex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A nova senha não pode conter caracteres especiais.");
        }


        if (usuario.getNivelAcesso() != 3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("O usuário não possui nível 3 para acesso.");
        }

        // Atualiza a senha
        usuario.setPassword(passwordEncoder.encode(novaSenha));

         // Se for um usuário de teste, mantém nível 3
        if ("senha".equalsIgnoreCase(usuario.getUsername())) {
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Usuário de teste detectado. Senha atualizada, mas nível mantido.");
        }

        // Caso contrário, libera o acesso (nível 2)
        usuario.setNivelAcesso(2);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Senha redefinida com sucesso. Acesso liberado.");

    }


    // Retorna o Nível do Usuario atraves do seu UserName gerado pelo token JWT
    @GetMapping("/nivel")
    public ResponseEntity<?> validarToken(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseException("Usuário não autenticado", null));
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("username", usuario.getUsername());
        response.put("nivelAcesso", usuario.getNivelAcesso());
        //response.put("precisaRedefinirSenha", usuario.getNivelAcesso() == 3);

        return ResponseEntity.ok(response);
    }



    //Envio de envio de email teste
    @PostMapping("/email-teste")
    public ResponseEntity<String> emailTeste(@RequestBody EmailRequest request) {
        try {
            String urlTeste = "https://sua-url.com/redefinir-senha?token=token-de-teste-123";
            emailService.sendResetToken(request.getEmail(), urlTeste);
            return ResponseEntity.ok("E-mail de teste enviado para " + request.getEmail() + "!");
        } catch (MessagingException e) {
            e.printStackTrace(); // ou log.error("Erro ao enviar e-mail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao enviar e-mail para " + request.getEmail());
        }
    }













}