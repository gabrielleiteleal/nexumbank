package nexum.com.nexumbank.dto.conta;

import nexum.com.nexumbank.dto.cliente.ClienteResponseDTO;

import java.time.LocalDateTime;

public record ContaResponseDTO(Long id_conta, String numero_conta, String agencia, Double saldo,
                               ClienteResponseDTO clienteResponseDTO,
                               String status_conta, LocalDateTime data_criacao) {
}
