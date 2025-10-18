package nexum.com.nexumbank.dto.transacao;

import nexum.com.nexumbank.dto.conta.ContaResponseDTO;

public record TransacaoResponseDTO(Long id_trasacao, String tipo_trasacao, Double valor, ContaResponseDTO conta_origem,
                                   ContaResponseDTO conta_destino, String data_hora) {
}
