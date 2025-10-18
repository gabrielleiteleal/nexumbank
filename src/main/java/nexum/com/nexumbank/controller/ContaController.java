package nexum.com.nexumbank.controller;

import lombok.AllArgsConstructor;
import nexum.com.nexumbank.dto.conta.ContaRequestDTO;
import nexum.com.nexumbank.dto.conta.ContaResponseDTO;
import nexum.com.nexumbank.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conta")
@AllArgsConstructor
public class ContaController {

    private ContaService contaService;

    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> listarContas() {
        return ResponseEntity.status(200).body(contaService.listarContas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> buscarConta(@PathVariable Long id) {
        return ResponseEntity.status(200).body(contaService.buscarConta(id));
    }

    @PostMapping("/depositar")
    public ResponseEntity<Boolean> depositarDinheiro(@RequestBody ContaRequestDTO contaRequestDTO){
        return ResponseEntity.status(201).body(contaService.depositarDinheiro(contaRequestDTO));
    }

    @PostMapping("/sacar")
    public ResponseEntity<Boolean> sacarDinheiro(@RequestBody ContaRequestDTO contaRequestDTO){
        return ResponseEntity.status(200).body(contaService.depositarDinheiro(contaRequestDTO));
    }


}