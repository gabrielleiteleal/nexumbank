package nexum.com.nexumbank.dto.cliente;

import nexum.com.nexumbank.dto.usuario.UsuarioResponseDTO;

public record ClienteResponseDTO(Long id, String profissao, Double renda_mensal, UsuarioResponseDTO usuario) {
}
