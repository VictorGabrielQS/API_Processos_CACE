package cace.processos_api.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false , unique = true)
    private String username;


    @Column(nullable = false)
    private String password;



    // Implementação dos métodos do UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Sem roles/autoridades por enquanto
    }


    @Override
    public boolean isAccountNonExpired() {
        return contaNaoExpirada();
    }

    @Override
    public boolean isAccountNonLocked() {
        return contaNaoBloqueada();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credenciaisNaoExpiradas();
    }

    @Override
    public boolean isEnabled() {
        return estaHabilitado();
    }

    // Métodos auxiliares em português
    public boolean contaNaoExpirada() {
        return true;
    }

    public boolean contaNaoBloqueada() {
        return true;
    }

    public boolean credenciaisNaoExpiradas() {
        return true;
    }

    public boolean estaHabilitado() {
        return true;
    }

}
