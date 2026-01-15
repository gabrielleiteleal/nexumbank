package nexum.com.nexumbank.dto.usuario;

public record UsuarioLoginResponseDTO(
        String id_usuario,
        String nome,
        String cpf_cnpj,
        String email,
        String tipo_usuario,
        Long id_cliente,
        Long id_conta
) {
}

