package pl.fernikq.core.guild.member;

import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.bukkit.Material;
import pl.fernikq.core.guild.Guild;

import java.util.ArrayList;
import java.util.List;


public enum GuildPermission {

    PVP("{c}&lZarzadzanie PVP", Material.DIAMOND_SWORD),
    INVITE("{c}&lZapraszanie graczy", Material.WATER_BUCKET),
    KICK("{c}&lWyrzucanie graczy", Material.LAVA_BUCKET),
    ALLIES("{c}&lZarzadzanie sojuszami", Material.BOOK),
    PLACE("{c}&lStawianie blokow", Material.OBSIDIAN),
    BREAK("{c}&lNiszczenie blokow", Material.STONE),
    TREASURE_OPEN("{c}&lDostep do skarbca", Material.CHEST),
    SET_BASE("{c}&lUstawianie bazy", Material.ARMOR_STAND),
    BASE_TELEPORT("{c}&lTeleporacja na baze", Material.BED),
    TIME_RENEW("{c}&lOdnawianie waznosci", Material.WATCH),
    CUBOID_ENLARGE("{c}&lPowiekszanie terenu", Material.GRASS),
    ALLIANCES_ENLARGE("{c}&lPowiekszanie ilosci sojuszy", Material.PAPER),
    MEMBERS_ENLARGE("{c}&lPowiekszanie ilosci czlonkow", Material.BOOK_AND_QUILL),
    TREASURE_ENLARGE("{c}&lPowiekszanie skarbca", Material.ENDER_CHEST);

    private String name;
    private Material material;

    private GuildPermission(String name, Material material){
        this.name = name;
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

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
