package nexum.com.nexumbank.model.enums;

import lombok.Getter;

@Getter
public enum Estado {
    AC("000-1"), AL("000-2"), AP("000-3"), AM("000-4"), BA("000-5"), CE("000-6"), DF("000-7"), ES("000-8"), GO("000-9"), MA("000-10"),
    MT("000-11"), MS("000-12"), MG("000-13"), PA("000-14"), PB("000-15"), PR("000-16"), PE("000-17"), PI("000-18"), RJ("000-19"),
    RN("000-20"), RS("000-21"), RO("000-22"), RR("000-23"), SC("000-24"), SP("000-25"), SE("000-26");

    private final String numeroEstado;

    Estado(String numeroEstado) {
        this.numeroEstado = numeroEstado;
    }

}
