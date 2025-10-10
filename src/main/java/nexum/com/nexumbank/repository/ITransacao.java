package nexum.com.nexumbank.repository;

import nexum.com.nexumbank.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITransacao extends JpaRepository<Transacao, Long> {
}
