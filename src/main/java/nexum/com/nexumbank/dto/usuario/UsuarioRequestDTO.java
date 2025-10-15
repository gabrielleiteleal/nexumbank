package nexum.com.nexumbank.dto.usuario;

public record UsuarioRequestDTO(String nome, String cpf_cnpj, String email, String telefone, String senha,
                                String endereco, String estado, String tipo_usuario, String data_nascimento) {

}
