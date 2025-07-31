package cace.processos_api.util;

import cace.processos_api.model.process.Usuario;
import cace.processos_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private static UsuarioRepository usuarioRepository;

    @Autowired
    public AuthUtil(UsuarioRepository usuarioRepository) {
        AuthUtil.usuarioRepository = usuarioRepository;
    }

    public static Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Usuario) {
            return (Usuario) principal;
        } else if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return usuarioRepository.findByUsername(username).orElse(null);
        }

        return null;
    }

    public static void validarAcesso(Integer... nivelRequerido) {
        /*
            DESCRIÇÃO DOS NÍVEIS DE ACESSO

            Nivel 1 - acesso total ✅
            Nivel 2 - acesso de usuario, possui limitações, não tem acesso às opções de ADM 👤
            Nivel 3 - sem acesso a nada, cadastro inicial até o usuário trocar a senha
        */

        Usuario usuario = getUsuarioLogado();
        if (usuario == null) {
            throw new RuntimeException("Acesso negado: usuário não autenticado.");
        }

        for (Integer nivel : nivelRequerido) {
            if (usuario.getNivelAcesso() == nivel) return;
        }

        throw new RuntimeException("Acesso negado: nível de acesso insuficiente.");
    }
}
