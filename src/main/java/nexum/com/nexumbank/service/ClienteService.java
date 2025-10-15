package nexum.com.nexumbank.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import nexum.com.nexumbank.dto.cliente.ClienteResponseDTO;
import nexum.com.nexumbank.dto.usuario.UsuarioResponseDTO;
import nexum.com.nexumbank.exception.ClienteNaoEncontrado;
import nexum.com.nexumbank.model.Cliente;
import nexum.com.nexumbank.model.Usuario;
import nexum.com.nexumbank.repository.ICliente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class ClienteService {

    private final ICliente repository;
    private final ContaService contaService;
    private final PasswordEncoder passwordEncoder;

    public List<ClienteResponseDTO> listarClientes() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public ClienteResponseDTO buscarCliente(Long id) {
        Cliente cliente = repository.findById(id).orElseThrow(() -> new ClienteNaoEncontrado("Cliente não encontrado. Id: " + id));
        return toDTO(cliente);
    }

    public void criarCliente(Usuario usuario, String profissao, Double rendaMensal) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setProfissao(profissao);
        cliente.setRendaMensal(rendaMensal);
        repository.save(cliente);
        contaService.criarConta(cliente);
    }

    private UsuarioResponseDTO usuarioToDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getIdUsuario().toString(), usuario.getNome(), usuario.getCpfCnpj(), usuario.getEmail(), usuario.getTelefone(), usuario.getEndereco(), usuario.getEstado().toString(), usuario.getTipoUsuario().toString(), usuario.getDataNascimento().toString());
    }

    public ClienteResponseDTO toDTO(Cliente cliente) {
        UsuarioResponseDTO usuarioDto = usuarioToDTO(cliente.getUsuario());
        return new ClienteResponseDTO(cliente.getIdCliente(), cliente.getProfissao(), cliente.getRendaMensal(), usuarioDto);
    }


}
