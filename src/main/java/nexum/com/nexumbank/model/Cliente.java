package nexum.com.nexumbank.model;

import jakarta.persistence.*;
import lombok.*;


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

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id_cliente")
    private Usuario usuario;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Conta conta;

    @Column(name = "profissao", length = 100)
    private String profissao;

    @Column(name = "renda_mensal")
    private Double rendaMensal;
}
