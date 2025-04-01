package util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Classe utilitária para conversão e formatação de datas.
 */
public class DateUtil {
    // Formatador thread-safe para datas no padrão brasileiro
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Converte uma LocalDate para String no formato dd/MM/yyyy.
     * @param date Data a ser formatada
     * @return String formatada ou String vazia se a data for nula
     */
    public static String format(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * Converte uma String no formato dd/MM/yyyy para LocalDate.
     * @param dateString String contendo a data
     * @return LocalDate correspondente
     * @throws DateTimeParseException Se a string estiver em formato inválido
     */
    public static LocalDate parse(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    /**
     * Converte java.util.Date para LocalDate (considera o timezone padrão do sistema).
     * @param date Data a ser convertida
     * @return LocalDate correspondente
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Converte LocalDate para java.util.Date.
     * @param localDate Data a ser convertida
     * @return Date correspondente
     */
    public static Date toDate(LocalDate localDate) {
        return Date.from(
                localDate.atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    /**
     * Valida se uma string está no formato de data correto.
     * @param dateString String a ser validada
     * @return true se a data é válida, false caso contrário
     */
    public static boolean isValid(String dateString) {
        try {
            parse(dateString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Impede instanciação da classe utilitária
    private DateUtil() {}
}