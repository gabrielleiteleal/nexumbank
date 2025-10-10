package nexum.com.nexumbank.repository;

import nexum.com.nexumbank.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IConta extends JpaRepository<Conta, Long> {
}
