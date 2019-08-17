package pl.fernikq.core.config;

public class Lang {

    public static String userNotExists;
    public static String mustBePlayer;

    static {
        userNotExists = MessagesManager.error("Gracz nie istnieje w bazie danych!");
        mustBePlayer = "Musisz byc graczem aby uzyc tej komendy";
    }
}
