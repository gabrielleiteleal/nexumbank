package nexum.com.nexumbank.controller;

import lombok.AllArgsConstructor;
import nexum.com.nexumbank.dto.cliente.ClienteInformacoesPessoais;
import nexum.com.nexumbank.dto.cliente.ClienteProfissaoERenda;
import nexum.com.nexumbank.dto.cliente.ClienteResponseDTO;
import nexum.com.nexumbank.service.ClienteService;
import nexum.com.nexumbank.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
@AllArgsConstructor
public class ClienteController {

    private final UsuarioService usuarioService;
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        return ResponseEntity.status(200).body(clienteService.listarClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarCliente(@PathVariable Long id){
        return ResponseEntity.status(200).body(clienteService.buscarCliente(id));
    }

    @PostMapping("profissao-renda")
    public ResponseEntity<Boolean> adicionarProfissaoERenda(@RequestBody ClienteProfissaoERenda clienteProfissaoERenda) {
        clienteService.adicionarProfissaoERenda(clienteProfissaoERenda);
        return ResponseEntity.status(200).body(true);
    }

    @PutMapping("/{idCliente}/informacoes")
    public ResponseEntity<Boolean> editarInformacoesPessoais(@PathVariable Long idCliente, @RequestBody ClienteInformacoesPessoais clienteInformacoesPessoais) {
        clienteService.editarInformacoesPessoais(idCliente, clienteInformacoesPessoais);
        return ResponseEntity.status(200).body(true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletarCliente(@PathVariable Long id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.status(204).body(true);
    }


}
