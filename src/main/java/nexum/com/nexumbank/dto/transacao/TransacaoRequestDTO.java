package nexum.com.nexumbank.dto.transacao;

import nexum.com.nexumbank.dto.conta.ContaResponseDTO;
import nexum.com.nexumbank.model.enums.TipoTransacao;

public record TransacaoRequestDTO(TipoTransacao tipo_transacao, Double valor, Long conta_origem,
                                  Long conta_destino) {
}
