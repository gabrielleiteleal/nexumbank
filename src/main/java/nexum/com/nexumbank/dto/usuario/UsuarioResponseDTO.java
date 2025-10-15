package nexum.com.nexumbank.dto.usuario;

public record UsuarioResponseDTO(String id_usuario, String nome, String cpf_cnpj, String email, String telefone, String endereco,
                                 String estado, String tipoUsuario, String data_nascimento) {

}
