package nexum.com.nexumbank.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexum.com.nexumbank.dto.cliente.ClienteResponseDTO;
import nexum.com.nexumbank.dto.conta.ContaRequestDTO;
import nexum.com.nexumbank.dto.conta.ContaResponseDTO;
import nexum.com.nexumbank.dto.usuario.UsuarioResponseDTO;
import nexum.com.nexumbank.exception.ContaNaoEncontrada;
import nexum.com.nexumbank.exception.SaldoInvalido;
import nexum.com.nexumbank.model.Cliente;
import nexum.com.nexumbank.model.Conta;
import nexum.com.nexumbank.model.Usuario;
import nexum.com.nexumbank.model.enums.StatusConta;
import nexum.com.nexumbank.repository.ICliente;
import nexum.com.nexumbank.repository.IConta;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class ContaService {

    private final IConta repository;
    private final ICliente clienteRepository;

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

    public void deletarConta(Long idCliente) {
        Conta conta = clienteRepository.findById(idCliente).orElseThrow(() -> new ContaNaoEncontrada("Conta não encontrada para o cliente. Id do Cliente: " + idCliente)).getConta();
        repository.delete(conta);
    }

    //TODO adicionar um limite para depósito
    public Boolean depositarDinheiro(ContaRequestDTO contaRequestDTO) {
        Conta conta = repository.findById(contaRequestDTO.id_cliente()).orElseThrow(() -> new ContaNaoEncontrada("Conta não encontrada. Id: " + contaRequestDTO.id_cliente()));
        conta.setSaldo(conta.getSaldo() + contaRequestDTO.saldo());
        System.out.println(contaRequestDTO.saldo());
        repository.save(conta);
        return true;
    }

    public Boolean sacarDinheiro(ContaRequestDTO contaRequestDTO) {
        Conta conta = repository.findById(contaRequestDTO.id_cliente()).orElseThrow(() -> new ContaNaoEncontrada("Conta não encontrada. Id: " + contaRequestDTO.id_cliente()));
        if (contaRequestDTO.saldo() <= conta.getSaldo()) {
            conta.setSaldo(conta.getSaldo() - contaRequestDTO.saldo());
            repository.save(conta);
        } else {
            throw new SaldoInvalido("Saldo insuficiente para saque. Saldo atual: " + conta.getSaldo());
        }
        return true;
    }

    //TODO adicionar metodo para transferencia entre contas

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

//    public Conta toEntity(ContaResponseDTO contaResponseDTO) {
//        Conta conta = new Conta();
//        conta.setIdConta(contaResponseDTO.id_conta());
//        conta.setNumeroConta(contaResponseDTO.numero_conta());
//        conta.setAgencia(contaResponseDTO.agencia());
//        conta.setSaldo(contaResponseDTO.saldo());
//        conta.setStatusConta(StatusConta.valueOf(contaResponseDTO.status_conta()));
//        conta.setDataCriacao(contaResponseDTO.data_criacao());
//        return conta;
//    }

}
