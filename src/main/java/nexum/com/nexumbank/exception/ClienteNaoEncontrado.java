package nexum.com.nexumbank.exception;

public class ClienteNaoEncontrado extends RuntimeException {
    public ClienteNaoEncontrado(String message) {
        super(message);
    }
}
