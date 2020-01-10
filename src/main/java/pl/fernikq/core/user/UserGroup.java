package pl.fernikq.core.user;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum UserGroup {

    PLAYER(0, 1, "", "", new ArrayList<>()),
    VIP(1, 2, "&8[&eVIP&8] ", "&e[VIP]", new ArrayList<>()),
    SV(2, 3, "&8[&6SV&8] ", "&6[SV]", new ArrayList<>()),
    MVP(3, 4, "&8[&3MVP&8] ", "&3[MVP]", new ArrayList<>()),
    YT(4, 4, "&8[&4Y&fT&8] ", "&4Y&fT", new ArrayList<>()),
    HELPER(5, 5, "&8[&bHelper&8] ", "&b[Helper]", new ArrayList<>()),
    MOD(6, 10, "&8[&aMod&8] ", "&a[Mod]", new ArrayList<>()),
    ADMIN(7, 100, "&8[&cAdmin&8] ", "&c[Admin]", new ArrayList<>()),
    ROOT(8, 100, "&8[&4ROOT&8] ", "&4[ROOT]", new ArrayList<>()),
    DEV(9, 100, "&8[&9DEV&8] ", "&9[DEV]", new ArrayList<>());

    private int level;
    private int homes;
    private String prefix;
    private String tag;
    private List<String> permissions;

    private UserGroup(int level, int homes, String prefix, String tag, List<String> permissions){
        this.level = level;
        this.homes = homes;
        this.prefix = prefix;
        this.tag = tag;
        this.permissions = permissions;
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
