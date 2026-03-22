package br.com.nexus.change.commons;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String BRAZILIAN_DATE = "dd/MM/yyyy HH:mm:ss";
    public static final String ERROR_EXCEPTION_RESOURCE = "Error ao processar resource: ";
    public static final String CURRENT_USER = "root@localhost";
    public static final String BEARER_TOKEN_ATTRIBUTE = "BEARER_TOKEN";
}
