package nexum.com.nexumbank.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import nexum.com.nexumbank.dto.UsuarioRequestDTO;
import nexum.com.nexumbank.dto.UsuarioResponseDTO;
import nexum.com.nexumbank.exception.CpfCnpjJaCadastrado;
import nexum.com.nexumbank.exception.EmailJaCadastrado;
import nexum.com.nexumbank.exception.SenhaIncorreta;
import nexum.com.nexumbank.exception.UsuarioNaoEncontrado;
import nexum.com.nexumbank.model.Usuario;
import nexum.com.nexumbank.model.enums.TipoUsuario;
import nexum.com.nexumbank.repository.IUsuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@AllArgsConstructor
public class UsuarioService {

    private final IUsuario repository;
    private final ClienteService clienteService;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> listarUsuarios() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public UsuarioResponseDTO buscarUsuario(Long id) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado. Id: " + id));
        return toDTO(usuario);
    }

    public Usuario criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = toEntity(usuarioRequestDTO);
        if (validarCpfEmail(usuario)) {
            System.out.println("CPF/CNPJ e E-mail válidos");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        if (usuario.getTipoUsuario() == TipoUsuario.CLIENTE) {
            clienteService.criarCliente(usuario);
        }
        return repository.save(usuario);
    }

    public Boolean deletarUsuario(Long id) {
        repository.deleteById(id);
        return true;
    }

    private Usuario toEntity(UsuarioRequestDTO usuarioRequestDTO) {
        return new Usuario(usuarioRequestDTO);
    }

    private UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getNome(), usuario.getCpfCnpj(), usuario.getEmail(), usuario.getTelefone(), usuario.getEndereco(), usuario.getTipoUsuario().toString(), usuario.getDataNascimento().toString());
    }

    private Boolean validarCpfEmail(Usuario usuario) {
        if (repository.existsByCpfCnpj(usuario.getCpfCnpj())) {
            throw new CpfCnpjJaCadastrado("CPF/CNPJ já cadastrado no sistema");
        }
        if (repository.existsByEmail(usuario.getEmail())) {
            throw new EmailJaCadastrado("E-mail já cadastrado no sistema");
        }
        return true;
    }

    private Boolean validarEmail(Usuario usuario) {
        if (repository.existsByEmail(usuario.getEmail())) {
            throw new EmailJaCadastrado("E-mail já cadastrado no sistema");
        }
        return true;
    }

    private Boolean validarSenha(Usuario usuario) {

        Usuario usuarioBanco = repository.findById(usuario.getIdUsuario()).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado. Id: " + usuario.getIdUsuario()));
        if(!(passwordEncoder.matches(usuarioBanco.getSenha(), usuario.getSenha()))){
            throw new SenhaIncorreta("Senha incorreta");
        }
        return true;

    }

}
