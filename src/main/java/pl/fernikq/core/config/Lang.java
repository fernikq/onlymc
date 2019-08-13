package pl.fernikq.core.config;

public class Lang {

    public static String userNotExists;

    static {
        userNotExists = MessagesManager.error("Gracz nie istnieje w bazie danych!");
    }
}
