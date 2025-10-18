package nexum.com.nexumbank.controller;

import lombok.AllArgsConstructor;
import nexum.com.nexumbank.dto.transacao.TransacaoRequestDTO;
import nexum.com.nexumbank.dto.transacao.TransacaoResponseDTO;
import nexum.com.nexumbank.service.TransacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/transacao")
@RestController
@AllArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;

    @GetMapping
    public ResponseEntity<List<TransacaoResponseDTO>> listarTransacoes() {
        return ResponseEntity.status(200).body(transacaoService.listarTransacoes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponseDTO> buscarTransacao(@PathVariable Long id) {
        return ResponseEntity.status(200).body(transacaoService.buscarTransacao(id));
    }

    @PostMapping("/transferir")
    public ResponseEntity<TransacaoResponseDTO> transferirDinheiro(@RequestBody TransacaoRequestDTO transacaoRequestDTO){
        return ResponseEntity.status(201).body(transacaoService.transferirEntreContas(transacaoRequestDTO));
    }
}
