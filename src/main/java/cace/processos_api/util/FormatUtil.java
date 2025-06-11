package cace.processos_api.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtil {

    // Método para formatar data
    public static String formatarData(LocalDateTime data) {
        if (data == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return data.format(formatter);
    }




}
