package nexum.com.nexumbank.service;

import lombok.AllArgsConstructor;
import nexum.com.nexumbank.dto.cliente.ClienteResponseDTO;
import nexum.com.nexumbank.dto.conta.ContaResponseDTO;
import nexum.com.nexumbank.dto.usuario.UsuarioResponseDTO;
import nexum.com.nexumbank.exception.ContaNaoEncontrada;
import nexum.com.nexumbank.model.Cliente;
import nexum.com.nexumbank.model.Conta;
import nexum.com.nexumbank.model.Usuario;
import nexum.com.nexumbank.model.enums.StatusConta;
import nexum.com.nexumbank.repository.IConta;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class ContaService {

    private final IConta repository;

    public List<ContaResponseDTO> listarContas() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public ContaResponseDTO buscarConta(Long id) {
        Conta conta = repository.findById(id).orElseThrow(() -> new ContaNaoEncontrada("Conta não encontrada. Id: " + id));
        return toDTO(conta);
    }

    public void criarConta(Cliente cliente) {
        Conta conta = new Conta();
        conta.setNumeroConta(gerarNumeroConta());
        conta.setAgencia(cliente.getUsuario().getEstado().getNumeroEstado());
        conta.setCliente(cliente);
        conta.setStatusConta(StatusConta.Ativo);
        repository.save(conta);
    }

    private String gerarNumeroConta() {
        Random random = new Random();
        String numeroConta;
        do {
            numeroConta = String.valueOf(1000 + random.nextInt(9000));
        } while (repository.existsByNumeroConta(numeroConta));
        return numeroConta;
    }

    private UsuarioResponseDTO usuarioToDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getIdUsuario().toString(), usuario.getNome(), usuario.getCpfCnpj(), usuario.getEmail(), usuario.getTelefone(), usuario.getEndereco(), usuario.getEstado().toString(), usuario.getTipoUsuario().toString(), usuario.getDataNascimento().toString());
    }

    private ClienteResponseDTO clienteToDTO(Cliente cliente) {
        UsuarioResponseDTO usuarioDTO = usuarioToDTO(cliente.getUsuario());
        return new ClienteResponseDTO(cliente.getIdCliente(), cliente.getProfissao(), cliente.getRendaMensal(), usuarioDTO);
    }

    private ContaResponseDTO toDTO(Conta conta) {
        ClienteResponseDTO clienteDTO = clienteToDTO(conta.getCliente());
        return new ContaResponseDTO(conta.getIdConta(), conta.getNumeroConta(), conta.getAgencia(),
                conta.getSaldo(), clienteDTO, conta.getStatusConta().name(), conta.getDataCriacao());
    }

}
