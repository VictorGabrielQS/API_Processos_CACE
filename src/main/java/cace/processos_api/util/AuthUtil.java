package cace.processos_api.util;

import cace.processos_api.exception.AccessDeniedException;
import cace.processos_api.model.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

public class AuthUtil {
    public static Usuario getUsuarioLogado(){
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }




    public  static  void validarAcesso(int... niveisPermitidos){


        /*                DESCRIÇÃO DOS NÍVEIS DE ACESSO

        Nivel 1 - acesso total ✅

        Nivel 2 - acesso de usuario , possui limitações , não tem acesso as opções de ADM  👤

        */


        Usuario usuario = getUsuarioLogado();
        boolean permitido = Arrays.stream(niveisPermitidos)
                .anyMatch(n -> n == usuario.getNivelAcesso());

        if (!permitido) {
               throw new AccessDeniedException(" Acesso Negado você não possui nível suficiente ! ");
        }
    }
}
