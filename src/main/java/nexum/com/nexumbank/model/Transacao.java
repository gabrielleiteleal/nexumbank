package nexum.com.nexumbank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nexum.com.nexumbank.model.enums.TipoTransacao;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transacao", nullable = false)
    private Long idTransacao;

    @Column(name = "tipo_transacao", nullable = false)
    private TipoTransacao tipoTransacao;

    @NotNull
    @Column(name = "valor", nullable = false)
    private Double valor = 0.0;

    @ManyToOne
    @JoinColumn(name = "conta_origem", nullable = false)
    private Conta contaOrigem;

    @ManyToOne
    @JoinColumn(name = "conta_destino", nullable = false)
    private Conta contaDestino;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();
}
