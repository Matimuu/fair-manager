package es.mpoea.fairmanager.catalog_service.api.utils;

public final class Util {
    public static String normalizeName(String name) {
        return name == null ? null : name.trim();
    }
    public static void requireValidName(String name, String message) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException(message);
    }

    private Util(){}
}
