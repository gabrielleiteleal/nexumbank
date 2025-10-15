package nexum.com.nexumbank.controller;

import lombok.AllArgsConstructor;
import nexum.com.nexumbank.dto.conta.ContaResponseDTO;
import nexum.com.nexumbank.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ContaResponseDTO> buscarConta(Long id) {
        return ResponseEntity.status(200).body(contaService.buscarConta(id));
    }


}