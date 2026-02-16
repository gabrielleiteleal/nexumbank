package nexum.com.nexumbank.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import nexum.com.nexumbank.dto.usuario.*;
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

    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = toEntity(usuarioRequestDTO);
        if (validarCpfEmail(usuario)) {
            System.out.println("CPF/CNPJ e E-mail válidos");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        if (usuario.getTipoUsuario() == TipoUsuario.CLIENTE) {
            clienteService.criarCliente(usuario);
            //TODO identificar como será salvo a profissão e renda mensal (OUTRO ENDPOINT)
        }
        //TODO fazer o mesmo para o tipo GERENTE quando criar a entidade Gerente


        repository.save(usuario);
        return toDTO(usuario);
    }

    public UsuarioResponseDTO editarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado. Id: " + id));
        usuario.setNome(usuarioRequestDTO.nome());
        usuario.setTelefone(usuarioRequestDTO.telefone());
        usuario.setEndereco(usuarioRequestDTO.endereco());

        repository.save(usuario);
        return toDTO(usuario);
    }

    public UsuarioResponseDTO editarEndereco(Long id, AddressRequestDTO addressRequestDTO){
        Usuario usuario = repository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado. Id: " + id));
        usuario.setEndereco(addressRequestDTO.endereco());

        repository.save(usuario);
        return toDTO(usuario);
    }

    public Boolean deletarUsuario(Long id) {
        repository.deleteById(id);
        return true;
    }

    //TODO verificar lógica
    private Usuario toEntity(UsuarioRequestDTO usuarioRequestDTO) {
        return new Usuario(usuarioRequestDTO);
    }

    private UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getIdUsuario().toString(), usuario.getNome(), usuario.getCpfCnpj(), usuario.getEmail(), usuario.getTelefone(), usuario.getEndereco(), usuario.getEstado().toString(), usuario.getTipoUsuario().toString(), usuario.getDataNascimento().toString());
    }

    protected Boolean validarCpfEmail(Usuario usuario) {
        if (repository.existsByCpfCnpj(usuario.getCpfCnpj())) {
            throw new CpfCnpjJaCadastrado("CPF/CNPJ já cadastrado no sistema");
        }
        if (repository.existsByEmail(usuario.getEmail())) {
            throw new EmailJaCadastrado("E-mail já cadastrado no sistema");
        }
        return true;
    }

    //TODO Método não utilizado, verificar necessidade
//    private Boolean validarEmail(Usuario usuario) {
//        if (repository.existsByEmail(usuario.getEmail())) {
//            throw new EmailJaCadastrado("E-mail já cadastrado no sistema");
//        }
//        return true;
//    }

    protected Boolean validarSenha(Usuario usuario) {

        Usuario usuarioBanco = repository.findById(usuario.getIdUsuario()).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado. Id: " + usuario.getIdUsuario()));
        if (!(passwordEncoder.matches(usuarioBanco.getSenha(), usuario.getSenha()))) {
            throw new SenhaIncorreta("Senha incorreta");
        }
        return true;

    }

    public UsuarioLoginResponseDTO login(UsuarioLoginRequestDTO loginRequest) {
        Usuario usuario = repository.findByCpfCnpj(loginRequest.cpf_cnpj())
                .orElseThrow(() -> new UsuarioNaoEncontrado("CPF/CNPJ não encontrado no sistema"));

        if (!passwordEncoder.matches(loginRequest.senha(), usuario.getSenha())) {
            throw new SenhaIncorreta("Senha incorreta");
        }

        Long idCliente = null;
        Long idConta = null;

        if (usuario.getTipoUsuario() == TipoUsuario.CLIENTE && usuario.getCliente() != null) {
            idCliente = usuario.getCliente().getIdCliente();
            if (usuario.getCliente().getConta() != null) {
                idConta = usuario.getCliente().getConta().getIdConta();
            }
        }

        return new UsuarioLoginResponseDTO(
                usuario.getIdUsuario().toString(),
                usuario.getNome(),
                usuario.getCpfCnpj(),
                usuario.getEmail(),
                usuario.getTipoUsuario().toString(),
                idCliente,
                idConta
        );
    }

}
