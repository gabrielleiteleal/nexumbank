package nexum.com.nexumbank.exception;

public class SaldoInvalido extends RuntimeException {
    public SaldoInvalido(String message) {
        super(message);
    }
}
