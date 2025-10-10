package nexum.com.nexumbank.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
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
    private final PasswordEncoder passwordEncoder;

    public List<Cliente> listarClientes() {
        return repository.findAll();
    }

    public Cliente buscarCliente(Long id) {
        return repository.findById(id).orElseThrow(() -> new ClienteNaoEncontrado("Cliente não encontrado. Id: " + id));
    }

    public Cliente criarCliente(Usuario usuario) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        return repository.save(cliente);
    }


}
