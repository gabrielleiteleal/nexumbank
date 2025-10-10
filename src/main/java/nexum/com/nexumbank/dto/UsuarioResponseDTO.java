package nexum.com.nexumbank.dto;

public record UsuarioResponseDTO(String nome, String cpf_cnpj, String email, String telefone, String endereco,
                                 String tipoUsuario, String data_nascimento) {

}
