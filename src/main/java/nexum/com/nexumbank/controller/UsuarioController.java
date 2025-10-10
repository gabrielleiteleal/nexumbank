package nexum.com.nexumbank.controller;

import nexum.com.nexumbank.dto.UsuarioRequestDTO;
import nexum.com.nexumbank.dto.UsuarioResponseDTO;
import nexum.com.nexumbank.model.Usuario;
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
    public ResponseEntity<UsuarioResponseDTO> buscarUsuario(@PathVariable Long id){
        return ResponseEntity.status(200).body(usuarioService.buscarUsuario(id));
    }

    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO){
        return ResponseEntity.status(201).body(usuarioService.criarUsuario(usuarioRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletarUsuario(@PathVariable Long id){
        return ResponseEntity.status(204).body(usuarioService.deletarUsuario(id));
    }

    //TODO método de teste para ver se está chegando o JSON corretamente
//    @PostMapping
//    public void printarUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO){
//        System.out.println(usuarioRequestDTO);
//    }

}
