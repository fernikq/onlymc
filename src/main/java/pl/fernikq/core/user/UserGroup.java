package pl.fernikq.core.user;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

import java.util.ArrayList;
import java.util.List;

public enum UserGroup {

    PLAYER(0, 1, "", ""),
    VIP(1, 2, "&8[&eVIP&8]", "&8[&eVIP&8]"),
    SV(2, 3, "&8[&6SV&8]", "&8[&6SV&8]"),
    MVP(3, 4, "&8[&3MVP&8]", "&8[&3MVP&8]"),
    YT(4, 4, "&8[&4Y&fT&8]", "&8[&4Y&fT&8]"),
    HELPER(5, 5, "&8[&bHelper&8]", "&8[&bHelper&8]"),
    MOD(6, 10, "&8[&aMod&8]", "&8[&aMod&8]"),
    ADMIN(7, 100, "&8[&cAdmin&8]", "&8[&cAdmin&8]"),
    ROOT(8, 100, "&8[&4ROOT&8]", "&8[&4ROOT&8]"),
    DEV(9, 100, "&8[&9DEV&8]", "&8[&9DEV&8]");

    private int level;
    private int homes;
    private String prefix;
    private String tag;

    private UserGroup(int level, int homes, String prefix, String tag){
        this.level = level;
        this.homes = homes;
        this.prefix = prefix;
        this.tag = tag;
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
}
