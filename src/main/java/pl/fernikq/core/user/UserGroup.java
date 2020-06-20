package pl.fernikq.core.user;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum UserGroup {

    PLAYER(0, 1, "", "", "&f", "&f", new ArrayList<>()),
    VIP(1, 2, "&8[&e&LVIP&8] ", "&eVIP", "&7", "&e", new ArrayList<>()),
    SV(2, 3, "&8[&6&LS&E&LV&8] ", "&6S&EV", "&7", "&6", new ArrayList<>()),
    MVP(3, 4, "&8[&3&LM&e&LVP&8] ", "&3M&EVP", "&7", "&3", new ArrayList<>()),
    UVIP(4, 10, "&8[&d&lU&5&lVIP&8] ", "&dU&5VIP", "&f", "&5", new ArrayList<>()),
    YT(5, 4, "&8[&4&lY&f&lT&8] ", "&4Y&fT", "&7", "&f", new ArrayList<>()),
    TEST_HELPER(6, 5, "&8[&3&lTHelper&8] ", "&3THelper", "&f&l", "&3", new ArrayList<>()),
    HELPER(6, 5, "&8[&b&lHelper&8] ", "&bHelper", "&f&l", "&b", new ArrayList<>()),
    MOD(7, 10, "&8[&a&lMod&8] ", "&aMod", "&f&l", "&a&l", new ArrayList<>()),
    ADMIN(8, 100, "&8[&c&lAdmin&8] ", "&cAdmin", "&f&l", "&c&l", new ArrayList<>()),
    ROOT(9, 100, "&8[&4&lROOT&8] ", "&4ROOT", "&f&l", "&4&l", new ArrayList<>()),
    DEV(10, 100, "&8[&9&lDEV&8] ", "&9DEV", "&f&l", "&3&l", new ArrayList<>());

    private int level;
    private int homes;
    private String prefix;
    private String tag;
    private String nickColor;
    private String messageColor;
    private List<String> permissions;

    private UserGroup(int level, int homes, String prefix, String tag, String nickColor, String messageColor, List<String> permissions){
        this.level = level;
        this.homes = homes;
        this.prefix = prefix;
        this.tag = tag;
        this.messageColor = messageColor;
        this.nickColor = nickColor;
        this.permissions = permissions;
    }

    public String getNickColor() {
        return nickColor;
    }

    public String getMessageColor() {
        return messageColor;
    }

    public static Set<UserGroup> getGroups(){
        return HashSet.of(values());
    }

    public static UserGroup getByName(final String name){
        return getGroups().find(group -> group.name().equalsIgnoreCase(name)).getOrNull();
    }

    public static Set<String> getPrefixes(){
        List<String> prefixes = new ArrayList<>();
        getGroups().forEach(group -> prefixes.add(group.name()));
        return HashSet.ofAll(prefixes);
    }

    public static String getPrefixesToString(){
        StringBuilder sb = new StringBuilder();
        getPrefixes().forEach(prefix -> sb.append("&8, {c}").append(prefix));
        return sb.toString().replaceFirst("&8, ", "");
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public int getLevel() {
        return level;
    }

    public int getHomes() {
        return homes;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTag() {
        return tag;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
