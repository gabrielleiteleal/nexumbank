package nexum.com.nexumbank.repository;

import jakarta.persistence.EntityManager;
import nexum.com.nexumbank.dto.usuario.UsuarioRequestDTO;
import nexum.com.nexumbank.model.Cliente;
import nexum.com.nexumbank.model.Conta;
import nexum.com.nexumbank.model.Usuario;
import nexum.com.nexumbank.model.enums.StatusConta;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
class IContaTest {

    @Autowired
    private IConta iConta;

    @Autowired
    private EntityManager entityManager;

    UsuarioRequestDTO usuarioRequestDTO;
    Usuario usuario;
    Cliente cliente;


    @BeforeAll
    static void setup() {
        System.out.println("Executing method before all tests in IContaTest class");
    }

    @BeforeEach
    void init() {
        usuarioRequestDTO = new UsuarioRequestDTO("Gabriel Leal", "123.456.789-00", "gabriel@email.com", "senha123", "11999999999", "Rua A, 123", "SP", "CLIENTE", "07/11/2004", "Programador", 5.000);
        usuario = createUsuario(usuarioRequestDTO);
        cliente = createCliente(usuario);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Executing method after each test in IContaTest class");
    }

    @AfterAll
    static void cleanup() {
        System.out.println("Executing method after all tests in IContaTest class");
    }

    @Test
    @DisplayName("Should return true when account number exists")
    void existsByNumeroContaCase1() {

        //Arrange
        Conta conta = createConta(cliente);

        //Act
        Boolean foundedAccount = this.iConta.existsByNumeroConta(conta.getNumeroConta());

        //Assert
        assertThat(foundedAccount).isTrue();
    }

    @Test
    @DisplayName("Should return false when account number not exists")
    void existsByNumeroContaCase2() {

        String inexistentNumber = "654321";

        Boolean foundedAccount = this.iConta.existsByNumeroConta(inexistentNumber);

        assertThat(foundedAccount).isFalse();

    }

    //TODO ajustar teste para verificar exceção de número duplicado
    @Test
    @DisplayName("Should throw exception when trying to create account with duplicate number")
    void dontCreateContaWithDuplicateNumber(){

        //Arrange
        Conta conta = createConta(cliente);
        Conta contaDuplicate = new Conta();
        contaDuplicate.setNumeroConta(conta.getNumeroConta());

        //Act
        Conta foundedAccount = this.iConta.findById(conta.getIdConta()).orElseThrow();

        //Assert
        assertThat(foundedAccount.getNumeroConta()).isNotEqualTo(contaDuplicate.getNumeroConta());
    }

    private Usuario createUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = new Usuario(usuarioRequestDTO);
        this.entityManager.persist(usuario);
        return usuario;
    }

    private Cliente createCliente(Usuario usuario) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        this.entityManager.persist(cliente);
        return cliente;
    }

    private Conta createConta(Cliente cliente) {
        Conta conta = new Conta();
        conta.setNumeroConta("123456");
        conta.setAgencia(cliente.getUsuario().getEstado().getNumeroEstado());
        conta.setCliente(cliente);
        conta.setStatusConta(StatusConta.Ativo);
        this.entityManager.persist(conta);
        return conta;
    }
}