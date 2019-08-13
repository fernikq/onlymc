package pl.fernikq.core.user;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

import java.util.ArrayList;
import java.util.List;

public enum UserGroup {

    PLAYER(0, 1, "", "");

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
