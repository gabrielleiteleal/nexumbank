package nexum.com.nexumbank.service;

import nexum.com.nexumbank.dto.usuario.UsuarioRequestDTO;
import nexum.com.nexumbank.dto.usuario.UsuarioResponseDTO;
import nexum.com.nexumbank.exception.CpfCnpjJaCadastrado;
import nexum.com.nexumbank.model.Usuario;
import nexum.com.nexumbank.repository.IUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private IUsuario repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO usuarioRequestDTO;

    @Captor
    private ArgumentCaptor<Usuario> userArgumentCapturer;

    @BeforeEach
    void init() {
        usuarioRequestDTO = new UsuarioRequestDTO("Gabriel Leal", "123.456.789-00", "gabriel@email.com", "11999999999", "senha123", "Rua A, 123", "SP", "CLIENTE", "07/11/2004", "Programador", 5.000);
    }

    @Nested
    class createUserTests {
        @Test
        @DisplayName("Should create a new user successfully")
        void shouldCreateNewUser1() {
            //Arrange
            Usuario usuario = new Usuario(usuarioRequestDTO);

            when(passwordEncoder.encode(any())).thenReturn("senhaCriptografada");
            when(repository.save(any())).thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                u.setIdUsuario(1L);
                return u;
            });

            //Act
            var output = usuarioService.criarUsuario(usuarioRequestDTO);

            //Assert
            verify(repository).save(userArgumentCapturer.capture());
            Usuario userCaptured = userArgumentCapturer.getValue();

            assertNotNull(output);
            assertEquals("senhaCriptografada", userCaptured.getSenha());
            assertEquals(usuario.getCpfCnpj(), userCaptured.getCpfCnpj());
        }

        @Test
        @DisplayName("Should throw UserExcpetion if CPF or Email already exists")
        void shouldThrowUserException() {
            //Arrange
//            Usuario usuario = new Usuario(usuarioRequestDTO);

            doReturn(true).when(usuarioService.criarUsuario(usuarioRequestDTO));

            when(repository.existsByCpfCnpj(any())).thenReturn(true);

            var exception = assertThrows(CpfCnpjJaCadastrado.class, () -> {
                usuarioService.criarUsuario(usuarioRequestDTO);
            });


            assertTrue(exception.getMessage().contains("CPF/CNPJ já cadastrado no sistema"));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should create a new user successfully 2")
        void shouldCreateNewUser2() {

            when(passwordEncoder.encode(any())).thenReturn("SenhaCriptografada");

            when(repository.save(any())).thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                u.setIdUsuario(1L);
                return u;
            });

            var output = usuarioService.criarUsuario(usuarioRequestDTO);

            assertNotNull(output);
            verify(repository).save(any());

        }


    }

    @Nested
    class editUserTests {
        @Test
        @DisplayName("Should edit user successfully")
        void shouldEditUserSuccessfully() {
            Usuario usuarioExistente = new Usuario(usuarioRequestDTO);
            usuarioExistente.setIdUsuario(1L);
            usuarioExistente.setNome("Gabriel Leal");
            usuarioExistente.setTelefone("1111111111");
            usuarioExistente.setEndereco("Antigo endereço");

            UsuarioRequestDTO usuarioEditado = new UsuarioRequestDTO("João Lucas", "123.456.789-00", "gabriel@email.com", "123123123", "senha123", "Novo Endereço", "SP", "CLIENTE", "07/11/2004", "Programador", 5.000);

            when(repository.findById(1L)).thenReturn(Optional.of(usuarioExistente));

            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            UsuarioResponseDTO response = usuarioService.editarUsuario(1L, usuarioEditado);

            assertNotNull(response);
            assertEquals("João Lucas", response.nome());
            assertEquals("123123123", response.telefone());
            assertEquals("Novo Endereço", response.endereco());

            ArgumentCaptor<Usuario> argumentCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(repository).save(argumentCaptor.capture());

            Usuario usuarioSalvo = argumentCaptor.getValue();

            assertEquals("João Lucas", usuarioSalvo.getNome());
            assertEquals("123123123", usuarioSalvo.getTelefone());
            assertEquals("Novo Endereço", usuarioSalvo.getEndereco());

//        verify(repository).save(any());
        }
    }


}