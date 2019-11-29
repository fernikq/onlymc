package pl.fernikq.core.guild.alliances;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AllianceManager {

    private final CorePlugin plugin;
    private AllianceData allianceData;
    private List<Alliance> alliances;

    public AllianceManager(CorePlugin plugin){
        this.plugin = plugin;
        this.alliances = new ArrayList<>();
    }

    public void init(){
        this.allianceData = new AllianceData(this.plugin);
    }

    public void registerAlliance(Guild guild1, Guild guild2){
        if(!hasAlliance(guild1, guild2)) {
            this.alliances.add(new Alliance(guild1, guild2));
        }
    }

    public void createAlliance(Guild guild1, Guild guild2){
        if(!hasAlliance(guild1, guild2)){
            Alliance alliance = new Alliance(guild1, guild2);
            this.alliances.add(alliance);
            this.allianceData.insertAlliance(alliance);
            guild1.getOnlineMembers().forEach(onlineMember -> {
                this.plugin.getTagManager().updateTag(onlineMember.getUser().asPlayer());
            });
            guild2.getOnlineMembers().forEach(onlineMember -> {
                this.plugin.getTagManager().updateTag(onlineMember.getUser().asPlayer());
            });
        }
    }

    public void removeAlliance(Guild guild1, Guild guild2){
        getAlliance(guild1, guild2).ifPresent(alliance -> {
            this.alliances.remove(alliance);
            this.allianceData.deleteAlliance(alliance);
            guild1.getOnlineMembers().forEach(onlineMember -> {
                this.plugin.getTagManager().updateTag(onlineMember.getUser().asPlayer());
            });
            guild2.getOnlineMembers().forEach(onlineMember -> {
                this.plugin.getTagManager().updateTag(onlineMember.getUser().asPlayer());
            });
        });
    }

    public RelationType getRelation(User user1, User user2){
        Guild guild1 = user1.getGuild();
        Guild guild2 = user2.getGuild();
        if(guild1 != null && guild2 != null){
            if(guild1.equals(guild2)){
                return RelationType.TEAM;
            }
            if(hasAlliance(guild1, guild2)){
                return RelationType.ALLY;
            }
        }
        return RelationType.ENEMY;
    }

    public List<Guild> getAllies(Guild guild){
        List<Guild> allies = new ArrayList<>();
        this.alliances.stream().filter(alliance -> alliance.getGuild1().getTag().equals(guild.getTag()))
                .forEach(alliance -> allies.add(alliance.getGuild2()));
        this.alliances.stream().filter(alliance -> alliance.getGuild2().getTag().equals(guild.getTag()))
                .forEach(alliance -> allies.add(alliance.getGuild1()));
        return allies;
    }

    public Optional<Alliance> getAlliance(Guild guild1, Guild guild2){
        return this.alliances.stream().filter(alliance -> alliance.getGuild1().equals(guild1) && alliance.getGuild2().equals(guild2) ||
                alliance.getGuild1().equals(guild2) && alliance.getGuild2().equals(guild1)).findAny();
    }

    public boolean hasAlliance(Guild guild1, Guild guild2){
        return this.alliances.stream().anyMatch(alliance -> alliance.getGuild1().equals(guild1) && alliance.getGuild2().equals(guild2) ||
                alliance.getGuild1().equals(guild2) && alliance.getGuild2().equals(guild1));
    }
}
