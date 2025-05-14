package cace.processos_api.util;

public class CpfCnpjUtil {
    public static String limpar(String valor) {
        return valor == null ? null : valor.replaceAll("[^\\d]", "");
    }
}