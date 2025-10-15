package nexum.com.nexumbank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nexum.com.nexumbank.model.enums.StatusConta;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta", nullable = false)
    private Long idConta;

    @NotNull
    @Column(name = "numero_conta", nullable = false, unique = true)
    private String numeroConta;

    @NotNull
    @Column(name = "agencia", nullable = false)
    private String agencia;

    @Column(name = "saldo", nullable = false)
    private Double saldo = 0.0;

    @OneToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "status_conta", nullable = false)
    private StatusConta statusConta;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

}
