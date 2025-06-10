package cace.processos_api.repository;

import cace.processos_api.model.process.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario , Long> {

       Optional<Usuario> findByUsername(String username);

       Optional<Usuario> findByCpf(String cpf);

       Optional<Usuario> findByEmail(String email);
}
