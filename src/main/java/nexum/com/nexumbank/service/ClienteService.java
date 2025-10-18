package nexum.com.nexumbank.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import nexum.com.nexumbank.dto.cliente.ClienteProfissaoERenda;
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

    public void criarCliente(Usuario usuario) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        repository.save(cliente);
        contaService.criarConta(cliente);
    }

    public void deletarCliente(Long id) {
        contaService.deletarConta(id);
        repository.deleteById(id);
    }

    public void adicionarProfissaoERenda(ClienteProfissaoERenda clienteProfissaoERenda) {
        Cliente cliente = repository.findById(clienteProfissaoERenda.id_cliente()).orElseThrow(() -> new ClienteNaoEncontrado("Cliente não encontrado. Id: " + clienteProfissaoERenda.id_cliente()));
        cliente.setProfissao(clienteProfissaoERenda.profissao());
        cliente.setRendaMensal(clienteProfissaoERenda.renda_mensal());
        repository.save(cliente);
    }

    private UsuarioResponseDTO usuarioToDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getIdUsuario().toString(), usuario.getNome(), usuario.getCpfCnpj(), usuario.getEmail(), usuario.getTelefone(), usuario.getEndereco(), usuario.getEstado().toString(), usuario.getTipoUsuario().toString(), usuario.getDataNascimento().toString());
    }

    public ClienteResponseDTO toDTO(Cliente cliente) {
        UsuarioResponseDTO usuarioDto = usuarioToDTO(cliente.getUsuario());
        return new ClienteResponseDTO(cliente.getIdCliente(), cliente.getProfissao(), cliente.getRendaMensal(), usuarioDto);
    }


}
