package nexum.com.nexumbank.service;

import nexum.com.nexumbank.dto.usuario.UsuarioRequestDTO;
import nexum.com.nexumbank.dto.usuario.UsuarioResponseDTO;
import nexum.com.nexumbank.exception.CpfCnpjJaCadastrado;
import nexum.com.nexumbank.exception.EmailJaCadastrado;
import nexum.com.nexumbank.exception.UsuarioNaoEncontrado;
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

import java.util.List;
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

    @Captor
    private ArgumentCaptor<Long> userIdArgumentCapturer;

    @Captor
    private ArgumentCaptor<String> userStringArgumentCapturer;

    @BeforeEach
    void init() {
        usuarioRequestDTO = new UsuarioRequestDTO("Gabriel Leal",
                "123.456.789-00",
                "gabriel@email.com",
                "11999999999",
                "senha123",
                "Rua A, 123",
                "SP",
                "CLIENTE",
                "07/11/2004",
                "Programador",
                5.000);
    }

    @Nested
    class listUsersTest {
        @Test
        @DisplayName("Should return all list users successfully")
        void shouldListAllUsersSuccessfully() {

            //Arrange
            Usuario usuario = new Usuario(usuarioRequestDTO);
            usuario.setIdUsuario(1L);

            //Act
            doReturn(List.of(usuario)).when(repository).findAll();

            //Assert
            var output = usuarioService.listarUsuarios();

            assertNotNull(output);
            assertEquals(1, output.size());

        }

        @Test
        @DisplayName("Should return empty list when no users found")
        void shouldReturnEmptyListWhenNoUsersFound() {

            //Act
            doReturn(List.of()).when(repository).findAll();

            //Assert
            var output = usuarioService.listarUsuarios();

            assertNotNull(output);
            assertTrue(output.isEmpty());
        }
    }

    @Nested
    class findUserByIdTests {
        @Test
        @DisplayName("Should find user by ID successfully")
        void shouldFindUserByIdSuccessfully() {

            //Arrange
            Usuario usuario = new Usuario(usuarioRequestDTO);
            usuario.setIdUsuario(1L);

            when(repository.findById(usuario.getIdUsuario())).thenReturn(Optional.of(usuario));

            //Act
            var output = usuarioService.buscarUsuario(usuario.getIdUsuario());

            //Assert
            assertNotNull(output);
            assertEquals(1L, usuario.getIdUsuario());

        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {

            //Arrange

            Long userId = 999L;
            when(repository.findById(userId)).thenReturn(Optional.empty());

            //Act
            UsuarioNaoEncontrado exception = assertThrows(UsuarioNaoEncontrado.class,
                    () -> usuarioService.buscarUsuario(userId));

            //Assert
            assertEquals("Usuário não encontrado. Id: " + userId, exception.getMessage());

        }
    }

    @Nested
    class createUserTests {
        @Test
        @DisplayName("Should create a new user successfully")
        void shouldCreateNewUserSuccessfully() {

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
        @DisplayName("Should throw exception if CPF already exists")
        void shouldThrowExceptionIfCpfAlreadyExists() {

            Usuario usuario = new Usuario(usuarioRequestDTO);

            doReturn(true).when(repository).existsByCpfCnpj(userStringArgumentCapturer.capture());

            CpfCnpjJaCadastrado exception = assertThrows(CpfCnpjJaCadastrado.class,
                    () -> usuarioService.criarUsuario(usuarioRequestDTO));

            var userStringCaptured = userStringArgumentCapturer.getValue();

            assertEquals("CPF/CNPJ já cadastrado no sistema", exception.getMessage());
            assertEquals(usuario.getCpfCnpj(), userStringCaptured);

            verify(repository, never()).save(any());
            verify(repository).existsByCpfCnpj(userStringCaptured);

        }

        @Test
        @DisplayName("Should throw exception if email already exists")
        void shouldThrowExceptionIfEmailAlreadyExists() {

            //Arrange
            Usuario usuario = new Usuario(usuarioRequestDTO);

            doReturn(true).when(repository).existsByEmail(userStringArgumentCapturer.capture());

            //Act
            EmailJaCadastrado exception = assertThrows(EmailJaCadastrado.class,
                    () -> usuarioService.criarUsuario(usuarioRequestDTO));

            var userStringCaptured = userStringArgumentCapturer.getValue();

            //Assert
            assertEquals("E-mail já cadastrado no sistema", exception.getMessage());
            assertEquals(usuario.getEmail(), userStringCaptured);

            verify(repository, never()).save(any());
            verify(repository).existsByEmail(userStringCaptured);

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

            UsuarioRequestDTO usuarioEditado = new UsuarioRequestDTO(
                    "João Lucas",
                    "123.456.789-00",
                    "gabriel@email.com",
                    "123123123",
                    "senha123",
                    "Novo Endereço",
                    "SP",
                    "CLIENTE",
                    "07/11/2004",
                    "Programador",
                    5.000);

            when(repository.findById(1L)).thenReturn(Optional.of(usuarioExistente));

            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            UsuarioResponseDTO response = usuarioService.editarUsuario(1L, usuarioEditado);

            assertNotNull(response);
            assertEquals("João Lucas", response.nome());
            assertEquals("123123123", response.telefone());
            assertEquals("Novo Endereço", response.endereco());

//            ArgumentCaptor<Usuario> argumentCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(repository).save(userArgumentCapturer.capture());

            Usuario usuarioSalvo = userArgumentCapturer.getValue();

            assertEquals("João Lucas", usuarioSalvo.getNome());
            assertEquals("123123123", usuarioSalvo.getTelefone());
            assertEquals("Novo Endereço", usuarioSalvo.getEndereco());
        }

        @Test
        @DisplayName("Should throw user not founded exception if ID aren't find")
        void shouldThrowUserNotFoundedExceptionIfIdArentFind() {
            Usuario usuario = new Usuario(usuarioRequestDTO);
            usuario.setIdUsuario(1L);
            var userNotFound = 999L;

            doReturn(Optional.of(usuario)).when(repository.findById(userNotFound));

            UsuarioNaoEncontrado exception = assertThrows(UsuarioNaoEncontrado.class, () -> usuarioService.buscarUsuario(userNotFound));

            assertEquals("Usuário não encontrado. Id: " + 999L, exception.getMessage());

        }
    }

    @Nested
    class deleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {

            var userId = 1L;

            doNothing().when(repository).deleteById(userIdArgumentCapturer.capture());

            var result = usuarioService.deletarUsuario(userId);

            var idCaputred = userIdArgumentCapturer.getValue();

            assertTrue(true, result.toString());
            assertEquals(userId, idCaputred);

        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {

            var userId = 999L;

            doNothing().when(repository).deleteById(userIdArgumentCapturer.capture());

            var result = usuarioService.deletarUsuario(userId);

            var idCaputred = userIdArgumentCapturer.getValue();

            assertTrue(result);
            assertEquals(userId, idCaputred);
        }
    }

    @Nested
    class validatePassword {

        @Test
        @DisplayName("Should validate password if passwords where the same")
        void shouldValidatePasswordIfPasswordsWhereTheSame() {

            //Arrange
            String rawPassword = "senha123";

            String encodedPassword = "HashBanco";

            Usuario usuarioRequest = new Usuario(usuarioRequestDTO);
            usuarioRequest.setIdUsuario(1L);
            usuarioRequest.setSenha(rawPassword);

            Usuario usuarioBanco = new Usuario(usuarioRequestDTO);
            usuarioBanco.setIdUsuario(1L);
            usuarioBanco.setSenha(encodedPassword);

            doReturn(Optional.of(usuarioBanco)).when(repository).findById(1L);

            ArgumentCaptor<String> encodedUserStringArgumentCapturer = ArgumentCaptor.forClass(String.class);

            when(passwordEncoder.matches(encodedUserStringArgumentCapturer.capture(), userStringArgumentCapturer.capture())).thenReturn(true);

            //Act
            var output = usuarioService.validarSenha(usuarioRequest);

            //Assert
            assertNotNull(output);
            assertTrue(output);

            verify(passwordEncoder).matches(any(), any());


            assertEquals(rawPassword, userStringArgumentCapturer.getValue());
            assertEquals(encodedPassword, encodedUserStringArgumentCapturer.getValue());

        }
    }


}