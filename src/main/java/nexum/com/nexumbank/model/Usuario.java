package nexum.com.nexumbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import nexum.com.nexumbank.dto.usuario.UsuarioRequestDTO;
import nexum.com.nexumbank.model.enums.Estado;
import nexum.com.nexumbank.model.enums.TipoUsuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @NotNull
    @Size(max = 100)
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotNull
    @Size(min = 8, max = 18)
    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 18)
    private String cpfCnpj;

    @NotNull
    @Email
    @Size(max = 120)
    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @NotNull
    @Size(min = 8)
    @Column(name = "senha_hash", nullable = false)
    private String senha;

    @Size(max = 20)
    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "estado", length = 2)
    private Estado estado;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private TipoUsuario tipoUsuario;

    @NotNull
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @NotNull
    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private Cliente cliente;

    public Usuario(UsuarioRequestDTO usuarioRequestDTO) {
        this.nome = usuarioRequestDTO.nome();
        this.cpfCnpj = usuarioRequestDTO.cpf_cnpj();
        this.email = usuarioRequestDTO.email();
        this.senha = usuarioRequestDTO.senha();
        this.telefone = usuarioRequestDTO.telefone();
        this.endereco = usuarioRequestDTO.endereco();
        this.estado = usuarioRequestDTO.estado() != null ? Estado.valueOf(usuarioRequestDTO.estado().toUpperCase()) : null;
        this.tipoUsuario = TipoUsuario.valueOf(usuarioRequestDTO.tipo_usuario().toUpperCase());
        this.dataNascimento = LocalDate.parse(usuarioRequestDTO.data_nascimento(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
