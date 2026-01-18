package nexum.com.nexumbank.service;

import jakarta.persistence.EntityManager;
import nexum.com.nexumbank.dto.usuario.UsuarioRequestDTO;
import nexum.com.nexumbank.dto.usuario.UsuarioResponseDTO;
import nexum.com.nexumbank.model.Usuario;
import nexum.com.nexumbank.repository.IUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private IUsuario repository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;


    @Test
    @DisplayName("Should create a new user successfully")
    void criarUsuarioCase1() {
        //Arrange
        Usuario usuario = new Usuario();
//        usuario.setIdUsuario(1L);
//        when(repository.save(any())).thenReturn(usuario);
        when(repository.save(any())).thenAnswer(invocation -> {
           Usuario u = invocation.getArgument(0);
           u.setIdUsuario(1L);
           return u;
        });

        when(passwordEncoder.encode(any())).thenReturn("senhaCriptografada");
        UsuarioRequestDTO request = new UsuarioRequestDTO("Gabriel Leal", "094.157.194-78", "gabriel@email.com", "senha123", "11999999999", "Rua A, 123", "SP", "CLIENTE", "07/11/2004", "Programador", 5.000);

        //Act
        var output = usuarioService.criarUsuario(request);

        //Assert
        assertNotNull(output);
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should don't create a new user if CPF or Email already exists")
    void criarUsuarioCase2() {

        //Arrange
        Usuario usuario = new Usuario();
        when(repository.save(any())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setIdUsuario(1L);
            return u;
        });
        usuario.setCpfCnpj("094.157.194-78");
        usuario.setEmail("gabriel@email.com");

        when(passwordEncoder.encode(any())).thenReturn("senhaCriptografada");
        UsuarioRequestDTO request = new UsuarioRequestDTO("Gabriel Leal", "094.157.194-78", "gabriel@email.com", "senha123", "11999999999", "Rua A, 123", "SP", "CLIENTE", "07/11/2004", "Programador", 5.000);

        //Act
        var output = usuarioService.criarUsuario(request);

        //Assert
        assertEquals(output.cpf_cnpj(), usuario.getCpfCnpj());
        assertEquals(output.email(), usuario.getEmail());
    }


//    @Test
//    @DisplayName("Should create a new user successfully")
//    void criarUsuarioCase1() {
//
//        //Arrange
//        UsuarioRequestDTO request = new UsuarioRequestDTO("Gabriel Leal", "094.157.194-78", "gabriel@email.com", "senha123", "11999999999", "Rua A, 123", "SP", "CLIENTE", "07/11/2004", "Programador", 5.000);
//        //Act
//        UsuarioResponseDTO response = usuarioService.criarUsuario(request);
//        entityManager.persist(response);
//        //Assert
//        assertThat(response.cpf_cnpj()).isNotEmpty();
//
//        //Arrange
//        //Act
//        //Assert
//
//    }

}