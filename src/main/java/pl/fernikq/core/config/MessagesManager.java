package pl.fernikq.core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.fernikq.core.CorePlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class MessagesManager {

    private final CorePlugin plugin;
    private final File file;

    public static String errorMessage;
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
    public static String playerChatPointsFormat;
    public static String playerChatFormat;
    public static String playerChatAdminFormat;
    public static String playerPrivateMessageFormat;
    public static String helpopFormat;
    public static String playerJoinMessage;
    public static String playerQuitMessage;
    public static String shopBuyItem;
    public static String levelShopBuyItem;
    public static String shopSellItem;
    public static String depositeApplesMessage;
    public static String depositeEnchantedApplesMessage;
    public static String depositePearlsMessage;
    public static String broadcastMessage;
    public static String titleBroadcastPrefix;
    public static String dropLevelupMessage;
    public static String coinsDropFromStoneMessage;
    public static String pointsBelowNameSuffix;
    public static String guildCreateMessage;
    public static String guildDeleteMessage;
    public static String guildInviteMessage;
    public static String guildJoinMessage;
    public static String guildQuitMessage;
    public static String guildKickMessage;
    public static String guildAllianceCreateMessage;
    public static String guildAllianceBreakMessage;
    public static String guildLeaderMessage;
    public static String guildJoinCuboidMessage;
    public static String guildQuitCuboidMessage;
    public static String guildIntruderMessage;
    public static String guildDestroyMessage;
    public static String guildAttackMessage;
    public static String guildTNTMessage;
    public static String playerFightMessage;
    public static String playerFightPlusAssistMessage;
    public static String playerFightAntylogoutMessage;
    public static String playerFightFinishMessage;
    public static String playerFightLogoutMessage;
    public static List<String> guildMainCommandHelp;

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
        errorMessage = "&4&lBlad &8>> &fWystapil blad, zglos sie do administratora!";
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
        playerChatPointsFormat = "&8[&c{POINTS}&8] ";
        playerChatFormat = "&8[&7{LVL}&8] {GUILD}{POINTS}{RANK}&f{NAME}&8: &f{MESSAGE}";
        playerChatAdminFormat = "{RANK}&f{NAME}&8: &f{MESSAGE}";
        playerPrivateMessageFormat = "&3{SENDER} &8>> &3{RECEIVER}&8: &f";
        helpopFormat = "&8[ &4&lHELPOP&8 ] &c{NICK}&8: &f";
        playerJoinMessage = "&8>> &8[&a+&8] &f{PLAYER}";
        playerQuitMessage = "&8>> &8[&c-&8] &f{PLAYER}";
        shopBuyItem = "&8>> &aPomyslnie {n}kupiles przedmiot za &a{AMOUNT} {n}monet&8!";
        levelShopBuyItem = "&8>> &aPomyslnie {n}kupiles przedmiot za poziom gornictwa&8!";
        shopSellItem = "&8>> &cPomyslnie {n}sprzedales przedmiot za &c{AMOUNT} {n}monet&8!";
        depositeApplesMessage = "&8>> {n}Posiadales przy sobie za duzo {c}refili&8, {n}nadmiar &8[{c}{AMOUNT}&8] {n}zostal przeniesiony do {c}/schowek";
        depositeEnchantedApplesMessage = "&8>> {n}Posiadales przy sobie za duzo {c}koxow&8, {n}nadmiar &8[{c}{AMOUNT}&8] {n}zostal przeniesiony do {c}/schowek";
        depositePearlsMessage = "&8>> {n}Posiadales przy sobie za duzo {c}perel&8, {n}nadmiar &8[{c}{AMOUNT}&8] {n}zostal przeniesiony do {c}/schowek";
        broadcastMessage = "&8[&e&lOgloszenie&8]: &f{MESSAGE}";
        titleBroadcastPrefix = "&c&lAlert";
        dropLevelupMessage = "&8>> &eGratulacje, osiagnales &6{LVL} &epoziom gornictwa!";
        coinsDropFromStoneMessage = "&8>> {c}Gratulacje, {n}trafiles na monety w ilosci&8: {c}{AMOUNT}";
        pointsBelowNameSuffix = "&cpkt";
        guildMainCommandHelp = Arrays.asList("&8&m--------&8[ {c}&lGildie &8]&m--------",
                " ",
                "&8>> {c}/g zaloz <tag> <nazwa> &8- {n}Zakladanie gildii",
                "&8>> {c}/g usun &8- {n}Usuwanie gildii",
                "&8>> {c}/g baza &8- {n}Teleportacja na baze gildii",
                "&8>> {c}/g info <tag> &8- {n}Informacje o podanej gildii",
                "&8>> {c}/g sojusz <tag> &8- {n}Prosba o sojusz z podana gildia",
                "&8>> {c}/g rozwiaz <tag> &8- {n}Zerwanie sojuszu z dana gildia",
                "&8>> {c}/g pvp &8- {n}Wlaczenie/Wylaczenie walki w gildii",
                "&8>> {c}/g setbaza &8- {n}Ustawienie bazy gildii",
                "&8>> {c}/g panel &8- {n}Zarzadzanie gildia",
                "&8>> {c}/g zapros <nick> &8- {n}Zapraszanie gracza do gildii",
                "&8>> {c}/g wyrzuc <nick> &8- {n}Wyrzucanie gracza z gildii",
                "&8>> {c}/g skarbiec &8- {n}Skarbiec gildii",
                "&8>> {c}/g itemy &8- {n}Przedmioty potrzebne do zalozenia gildii",
                "&8>> {c}/g opusc &8- {n}Opuszczenie gildii",
                "&8>> {c}/g dolacz <tag> &8- {n}Dolaczanie do gildii",
                "&8>> {c}/g lider <nick> &8- {n}Przekazanie lidera gildii",
                " ",
                "&8&m--------&8[ {c}&lGildie &8]&m--------");
        guildCreateMessage = "&8[{c}&lGILDIE&8] {n}Gildia &8[{c}{TAG}&8] {c}{NAME} {n}zostala zalozona przez {c}{OWNER}";
        guildDeleteMessage = "&8[{c}&lGILDIE&8] {n}Gildia &8[{c}{TAG}&8] {n}zostala usunieta przez {c}{OWNER}";
        guildInviteMessage = "&8[{c}&lGILDIE&8] {n}Gildia &8[{c}{TAG}&8] {n}wyslala ci zaproszenie, aby zaakceptowac wpisz {c}/g dolacz {TAG}";
        guildJoinMessage = "&8[{c}&lGILDIE&8] {n}Gracz {c}{PLAYER} {n}dolaczyl do gildii &8[{c}{TAG}&8]";
        guildQuitMessage = "&8[{c}&lGILDIE&8] {n}Gracz {c}{PLAYER} {n}opuscil gildie &8[{c}{TAG}&8]";
        guildKickMessage = "&8[{c}&lGILDIE&8] {n}Gracz {c}{PLAYER} {n}zostal wyrzucony z gildii &8[{c}{TAG}&8]";
        guildDestroyMessage = "&8[{c}&lGILDIE&8] {n}Gildia &8[{c}{TAG1}&8] {n}zostal podbita przez gildie &8[{c}{TAG2}&8]";
        guildAttackMessage = "&8[{c}&lGILDIE&8] {n}Gildia &8[{c}{TAG1}&8] {n}zabrala zycie gildii &8[{c}{TAG2}&8]";
        guildAllianceCreateMessage = "&8[{c}&lGILDIE&8] {n}Gildia &8[{c}{TAG1}&8] {n}zawarla sojusz z gildia &8[{c}{TAG2}&8]";
        guildAllianceBreakMessage = "&8[{c}&lGILDIE&8] {n}Gildia &8[{c}{TAG1}&8] {n}zerwala sojusz z gildia &8[{c}{TAG2}&8]";
        guildLeaderMessage = "&8[{c}&lGILDIE&8] {n}Gracz {c}{PLAYER} {n}zostal nowym liderem gildii &8[{c}{TAG}&8]";
        guildQuitCuboidMessage = "&8>> &cOpusciles {n}teren gildii &8[&c{TAG}&8]";
        guildJoinCuboidMessage = "&8>> &aWkroczyles {n}na teren gildii &8[&a{TAG}&8]";
        guildIntruderMessage = "&8[&c&lINTRUZ&8] {n}Gracz &c{PLAYER} {n}wszedl na teren twojej gildii!";
        guildTNTMessage = "&8[&c&lTNT&8] {n}Na terenie twojej gildii wybuchlo &cTNT {n}budowanie zostalo zablokowane na &c{TIME}!";
        playerFightMessage = "&8>> {n}Gracz {VICTIM-GUILD}{c}{VICTIM} &8[&f-&c{VICTIM-POINTS}&8] {n}zostal zabity przez {KILLER-GUILD}&a{KILLER} &8[&f+&a{KILLER-POINTS}&8]";
        playerFightPlusAssistMessage = "&8>> {n}Przy zabojstwie asystowal {ASSIST-GUILD}&9{ASSIST} &8[&f+&a{ASSIST-POINTS}&8]";
        playerFightAntylogoutMessage = "{n}Jestes podczas walki jeszcze przez &c&l{TIME} {n}sek";
        playerFightFinishMessage = "&a&lSkonczyles walke, mozesz sie wylogowac!";
        playerFightLogoutMessage = "&8>> {n}Gracz &c{PLAYER} {n}wylogowal sie podczas walki&8!";
    }
}
