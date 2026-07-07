package com.munevver.rabam.service.service;

import java.util.Set;

public final class ServiceCatalog {

    private static final Set<String> ALLOWED_TITLES = Set.of(
            "Bakım",
            "Muayene",
            "Araç Yıkama",
            "Lastik",
            "Akaryakıt",
            "Ekspertiz",
            "Çekici",
            "Sigorta"
    );

    private ServiceCatalog() {
    }

    public static boolean isAllowedTitle(String title) {
        return title != null && ALLOWED_TITLES.contains(title.trim());
    }

    public static String allowedTitlesText() {
        return String.join(", ", ALLOWED_TITLES);
    }
}
