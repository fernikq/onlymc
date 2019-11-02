package pl.fernikq.core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.fernikq.core.CorePlugin;

import java.io.File;
import java.lang.reflect.Field;

public class MessagesManager {

    private final CorePlugin plugin;
    private final File file;

    public static String commandErrorMessage;
    public static String commandErrorPermission;
    public static String commandCorrectUsage;
    public static String errorFormat;
    public static String teleportStartMessage;
    public static String teleportCancelMessage;
    public static String teleportFinishPlayerMessage;
    public static String teleportFinishLocationMessage;

    //VISUAL
    public static String playerNametagGuildAllyFormat;
    public static String playerNametagGuildEnemyFormat;
    public static String playerNametagGuildOwnFormat;
    public static String playerChatGuildFormat;
    public static String playerChatFormat;
    public static String playerChatAdminFormat;
    public static String playerPrivateMessageFormat;
    public static String helpopFormat;
    public static String playerJoinMessage;
    public static String playerQuitMessage;
    public static String commandHelpMessage;
    public static String shopBuyItem;
    public static String shopSellItem;

    public MessagesManager(CorePlugin plugin){
        this.plugin = plugin;
        this.file = new File(this.plugin.getDataFolder(), "messages.yml");
        setDefaultValues();
    }

    public void reload(){
        load();
        save();
    }

    public void save(){
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            for(Field field : MessagesManager.class.getFields()) {
                fileConfiguration.set("Messages." + field.getName(), field.get(null));
            }
            fileConfiguration.save(file);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void load(){
        try{
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            for(Field field : MessagesManager.class.getFields()){
                if(fileConfiguration.isSet("Messages."+field.getName())){
                    field.set(null, fileConfiguration.get("Messages."+field.getName()));
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static String error(String message){
        return errorFormat.replace("{ERROR}", message);
    }

    public static String usage(String message){
        return commandCorrectUsage.replace("{USAGE}", message);
    }

    private void setDefaultValues(){
        commandErrorMessage = "&4&lBlad &8>> &fBlad podczas wykonywania komendy, zglos sie do administratora!";
        commandErrorPermission = "&4&lBlad &8>> &fNie posiadasz wystarczajacych uprawnien aby uzyc tej komendy!";
        errorFormat = "&4&lBlad &8>> &f{ERROR}";
        commandCorrectUsage = "&c&lPoprawne uzycie&8: &f{USAGE}";
        teleportCancelMessage = "&4&lBlad &8>> &fPoruszyles sie, teleportacja anulowana!";
        teleportStartMessage = "&8>> {n}Zostaniesz przeteleportowany za {c}{TIME} sek.";
        teleportFinishLocationMessage = "&8>> {n}Zostales przeteleportowany na {c}{LOCATION}";
        teleportFinishPlayerMessage = "&8>> {n}Zostales przeteleportowany do gracza {c}{PLAYER}";
        playerNametagGuildOwnFormat = "&8[&a{GUILD}&8] ";
        playerNametagGuildAllyFormat = "&8[&e{GUILD}&8] ";
        playerNametagGuildEnemyFormat = "&8[&c{GUILD}&8] ";
        playerChatGuildFormat = "&8[&c{GUILD}&8] ";
        playerChatFormat = "&8[&7{LVL}&8] {GUILD}{RANK} &f{NAME}&8: &f{MESSAGE}";
        playerChatAdminFormat = "{RANK} &f{NAME}&8: &f{MESSAGE}";
        playerPrivateMessageFormat = "&3{SENDER} &8>> &3{RECEIVER}&8: &f";
        helpopFormat = "&8[ &4&lHELPOP&8 ] &c{NICK}&8: &f";
        playerJoinMessage = "&8>> &8[&a+&8] &f{PLAYER}";
        playerQuitMessage = "&8>> &8[&c-&8] &f{PLAYER}";
        commandHelpMessage = "&8>> {n}Podana komenda nie istnieje&8! {n}Sprawdz dostepne komendy pod {c}/pomoc";
        shopBuyItem = "&8>> &aPomyslnie {n}kupiles przedmiot&8!";
        shopSellItem = "&8>> &cPomyslnie {n}sprzedales przedmiot&8!";
    }
}
