package nexum.com.nexumbank.controller;

import nexum.com.nexumbank.dto.usuario.UsuarioLoginRequestDTO;
import nexum.com.nexumbank.dto.usuario.UsuarioLoginResponseDTO;
import nexum.com.nexumbank.dto.usuario.UsuarioRequestDTO;
import nexum.com.nexumbank.dto.usuario.UsuarioResponseDTO;
import nexum.com.nexumbank.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.status(200).body(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuario(@PathVariable Long id) {
        return ResponseEntity.status(200).body(usuarioService.buscarUsuario(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return ResponseEntity.status(201).body(usuarioService.criarUsuario(usuarioRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> editarUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return ResponseEntity.status(201).body(usuarioService.editarUsuario(id, usuarioRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletarUsuario(@PathVariable Long id) {
        return ResponseEntity.status(204).body(usuarioService.deletarUsuario(id));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioLoginResponseDTO> login(@RequestBody UsuarioLoginRequestDTO loginRequest) {
        return ResponseEntity.status(200).body(usuarioService.login(loginRequest));
    }

}
