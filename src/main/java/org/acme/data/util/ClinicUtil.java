package org.acme.data.util;

import jakarta.ws.rs.BadRequestException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ClinicUtil {

    public static final ZoneId BUCURESTI = ZoneId.of("Europe/Bucharest");
    public static final DateTimeFormatter IO_FMT = DateTimeFormatter.ofPattern("MM.dd.yyyy HH:mm", Locale.ROOT);


    public static String formatBucharest(OffsetDateTime odt) {
        if (odt == null) return null;
        return odt.atZoneSameInstant(BUCURESTI).toLocalDateTime().format(IO_FMT);
    }

    public static OffsetDateTime parseBucharest(String s) {
        if (isBlank(s)) throw new BadRequestException("startAt must be provided");
        LocalDateTime ldt = LocalDateTime.parse(s.trim(), ClinicUtil.IO_FMT);
        return ldt.atZone(ClinicUtil.BUCURESTI).toOffsetDateTime();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

}
