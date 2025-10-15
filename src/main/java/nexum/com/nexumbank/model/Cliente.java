package nexum.com.nexumbank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "cliente")
public class Cliente {

    @Id
    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_cliente")
    private Usuario usuario;

    @Column(name = "profissao", length = 100)
    private String profissao;

    @Column(name = "renda_mensal")
    private Double rendaMensal;
}
