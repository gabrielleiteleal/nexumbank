package nexum.com.nexumbank.dto.usuario;

import nexum.com.nexumbank.model.enums.Estado;

public record AddressRequestDTO(String endereco, Estado estado) {
}
