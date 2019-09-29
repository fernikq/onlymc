package pl.fernikq.core.config;

public class Lang {

    public static String userNotExists;
    public static String mustBePlayer;
    public static String playerOffline;
    public static String badIntegerFormat;

    static {
        userNotExists = MessagesManager.error("Gracz nie istnieje w bazie danych!");
        playerOffline = MessagesManager.error("Podany gracz jest offline!");
        mustBePlayer = "Musisz byc graczem aby uzyc tej komendy";
        badIntegerFormat = MessagesManager.error("Format liczby jest niepoprawny!");
    }
}
