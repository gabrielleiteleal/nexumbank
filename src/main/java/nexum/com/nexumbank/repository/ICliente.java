package nexum.com.nexumbank.repository;

import nexum.com.nexumbank.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICliente extends JpaRepository<Cliente, Long> {

}
