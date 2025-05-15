package cace.processos_api.util;

public class NumeroProcessoUtil {
    // Remove tudo que não for número ou hífen (para formato curto: 0105512-02)
    public static String limparCurto(String numero) {
        if (numero == null) return null;
        return numero.replaceAll("[^\\d]", "");
    }

    // Remove tudo que não for número (para formato completo: 01055120234011234)
    public static String limparCompleto(String numero) {
        if (numero == null) return null;
        return numero.replaceAll("[^\\d]", "");
    }


    public static String formatarNumeroCompleto(String numero) {
        if (numero == null || numero.length() != 20) return numero;

        return numero.substring(0, 7) + "-" +
                numero.substring(7, 9) + "." +
                numero.substring(9, 13) + "." +
                numero.substring(13, 14) + "." +
                numero.substring(14, 16) + "." +
                numero.substring(16);
    }


    public static String formatarNumeroCurto(String numero) {
        if (numero == null || numero.length() != 9) return numero;

        return numero.substring(0, 7) + "-" + numero.substring(7);
    }


}

