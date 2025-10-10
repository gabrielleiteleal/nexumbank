package nexum.com.nexumbank.repository;

import nexum.com.nexumbank.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuario extends JpaRepository<Usuario, Long> {
    Boolean existsByCpfCnpj(String cpfCnpj);
    Boolean existsByEmail(String email);
}
