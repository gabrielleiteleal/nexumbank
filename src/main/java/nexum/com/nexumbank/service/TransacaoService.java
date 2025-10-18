package nexum.com.nexumbank.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import nexum.com.nexumbank.dto.conta.ContaResponseDTO;
import nexum.com.nexumbank.dto.transacao.TransacaoRequestDTO;
import nexum.com.nexumbank.dto.transacao.TransacaoResponseDTO;
import nexum.com.nexumbank.exception.ContaNaoEncontrada;
import nexum.com.nexumbank.exception.TransacaoNaoEncontrada;
import nexum.com.nexumbank.model.Conta;
import nexum.com.nexumbank.model.Transacao;
import nexum.com.nexumbank.repository.IConta;
import nexum.com.nexumbank.repository.ITransacao;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
@AllArgsConstructor
public class TransacaoService {

    private final ITransacao repository;
    private final ContaService contaService;
    private final IConta contaRepository;

    public List<TransacaoResponseDTO> listarTransacoes(){
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public TransacaoResponseDTO buscarTransacao(Long id){
        Transacao transacao = repository.findById(id).orElseThrow(() -> new TransacaoNaoEncontrada("Transação não encontrada. Id: " + id));
        return toDTO(transacao);
    }

    public TransacaoResponseDTO transferirEntreContas(TransacaoRequestDTO transacaoRequestDTO) {
        Transacao transacao = new Transacao();
        Conta contaOrigem = contaRepository.findById(transacaoRequestDTO.conta_origem()).orElseThrow(() -> new ContaNaoEncontrada("Conta de origem não encontrada. Id: " + transacaoRequestDTO.conta_origem()));
        Conta contaDestino = contaRepository.findById(transacaoRequestDTO.conta_destino()).orElseThrow(() -> new ContaNaoEncontrada("Conta de destino não encontrada. Id: " + transacaoRequestDTO.conta_destino()));
        transacao.setTipoTransacao(transacaoRequestDTO.tipo_transacao());
        transacao.setValor(transacao.getValor() + transacaoRequestDTO.valor());
        transacao.setContaOrigem(contaOrigem);
        transacao.setContaDestino(contaDestino);
        //
        contaOrigem.setSaldo(contaOrigem.getSaldo() - transacaoRequestDTO.valor());
        contaDestino.setSaldo(contaDestino.getSaldo() + transacaoRequestDTO.valor());
        repository.save(transacao);
        return toDTO(transacao);
    }

    private TransacaoResponseDTO toDTO(Transacao transacao) {
        ContaResponseDTO contaOrigem = contaService.buscarConta(transacao.getContaOrigem().getIdConta());
        ContaResponseDTO contaDestino = contaService.buscarConta(transacao.getContaDestino().getIdConta());
        return new TransacaoResponseDTO(transacao.getIdTransacao(), transacao.getTipoTransacao().toString(), transacao.getValor(), contaOrigem, contaDestino, transacao.getDataHora().toString());
    }

}
