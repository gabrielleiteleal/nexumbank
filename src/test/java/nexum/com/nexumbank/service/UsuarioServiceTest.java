package nexum.com.nexumbank.service;

import jakarta.persistence.EntityManager;
import nexum.com.nexumbank.dto.usuario.UsuarioRequestDTO;
import nexum.com.nexumbank.dto.usuario.UsuarioResponseDTO;
import nexum.com.nexumbank.repository.IUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private IUsuario repository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Autowired
    @InjectMocks
    private UsuarioService usuarioService;

    @Autowired
    private EntityManager entityManager;

//    @BeforeEach
//    void setup(){
//        MockitoAnnotations.initMocks(this);
//    }

    @Test
    @DisplayName("Should create a new user successfully")
    void criarUsuarioCase1() {

        //Arrange
        UsuarioRequestDTO request = new UsuarioRequestDTO("Gabriel Leal", "123.456.789-00", "gabriel@email.com", "senha123", "11999999999", "Rua A, 123", "SP", "CLIENTE", "07/11/2004", "Programador", 5.000);
        //Act
        UsuarioResponseDTO response = usuarioService.criarUsuario(request);
        //Assert
        assertNotNull(response);
        assertNotNull(response.id_usuario());
        assertEquals("Gabriel Leal", response.nome());
        assertEquals("123.456.789-00", response.cpf_cnpj());
        
    }

    @Test
    @DisplayName("Should throw Exception when a user has not created")
    void criarUsuarioCase2() {
    }
}