package cace.processos_api.util;

public class NumeroProcessoUtil {
    // Remove tudo que não for número ou hífen (para formato curto: 0105512-02)
    public static String limparCurto(String numero) {
        if (numero == null) return null;
        return numero.replaceAll("[^\\d-]", "");
    }

    // Remove tudo que não for número (para formato completo: 01055120234011234)
    public static String limparCompleto(String numero) {
        if (numero == null) return null;
        return numero.replaceAll("[^\\d]", "");
    }
}

