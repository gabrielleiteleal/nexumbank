package nexum.com.nexumbank.dto;

public record UsuarioRequestDTO(String nome, String cpf_cnpj, String email, String telefone, String senha,
                                String endereco, String tipo_usuario, String data_nascimento) {

}
