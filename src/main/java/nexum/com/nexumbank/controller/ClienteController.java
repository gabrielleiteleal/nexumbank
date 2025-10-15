package nexum.com.nexumbank.controller;

import lombok.AllArgsConstructor;
import nexum.com.nexumbank.dto.cliente.ClienteResponseDTO;
import nexum.com.nexumbank.model.Cliente;
import nexum.com.nexumbank.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
@AllArgsConstructor
public class ClienteController {

    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        return ResponseEntity.status(200).body(clienteService.listarClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarCliente(@PathVariable Long id){
        return ResponseEntity.status(200).body(clienteService.buscarCliente(id));
    }


}
