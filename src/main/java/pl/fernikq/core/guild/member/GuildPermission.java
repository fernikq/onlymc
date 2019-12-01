package pl.fernikq.core.guild.member;

import io.vavr.collection.Stream;
import io.vavr.control.Option;
import pl.fernikq.core.guild.Guild;

import java.util.ArrayList;
import java.util.List;


public enum GuildPermission {

    PVP,
    INVITE,
    KICK,
    ALLIES,
    PLACE,
    BREAK,
    TREASURE_OPEN,
    SET_BASE,
    BASE_TELEPORT,
    TIME_RENEW,
    CUBOID_ENLARGE,
    ALLIANCES_ENLARGE,
    MEMBERS_ENLARGE,
    MEMBERS_PERMISSIONS,
    TREASURE_ENLARGE,
    ENLARGE_REGION,
    ENLARGE_MEMBERS;

    public static Option<GuildPermission> getPermissionByName(String name){
        return Stream.of(values()).find(guildPermission -> guildPermission.name().equalsIgnoreCase(name));
    }

    public static String getMemberPermissionsToString(GuildMember member){
        StringBuilder stringBuilder = new StringBuilder();
        if(member.getGuildPermissions() == null || member.getGuildPermissions().isEmpty()){
            return "";
        }
        int i = 0;
        for(GuildPermission guildPermission : member.getGuildPermissions()){
            if(i == 0){
                stringBuilder.append(guildPermission.name());
            }else{
                stringBuilder.append(";"+guildPermission.name());
            }
            i++;
        }
        return stringBuilder.toString();
    }

    public static List<GuildPermission> getMemberPermissionsFromString(String string){
        List<GuildPermission> guildPermissions = new ArrayList<>();
        if(string == null || string.isEmpty() || string.equalsIgnoreCase("")){
            return guildPermissions;
        }
        String[] permissionsInfo = string.split(";");
        for(String permission : permissionsInfo){
            if(getPermissionByName(permission).isDefined()){
                guildPermissions.add(getPermissionByName(permission).get());
            }
        }
        return guildPermissions;
    }
}
