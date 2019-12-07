package pl.fernikq.core.guild.member;

import io.vavr.collection.Stream;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.user.User;

import java.util.ArrayList;
import java.util.List;

public class GuildMember {

    private User user;
    private Guild guild;
    private List<GuildPermission> guildPermissions;

    public GuildMember(User user, Guild guild, GuildPermission... guildPermissions){
        this.user = user;
        this.guild = guild;
        this.guildPermissions = new ArrayList<>();
        Stream.of(guildPermissions).forEach(guildPermission -> this.guildPermissions.add(guildPermission));
        this.guild.addMember(this);
        user.setGuild(guild);
    }

    public GuildMember(User user, Guild guild, List<GuildPermission> guildPermissions){
        this.user = user;
        this.guild = guild;
        this.guildPermissions = new ArrayList<>();
        guildPermissions.forEach(guildPermission -> this.guildPermissions.add(guildPermission));
        user.setGuild(guild);
        this.guild.addMember(this);
    }

    public boolean hasPermission(GuildPermission guildPermission){
        if(this.guild.getOwner().equals(this.user)){
            return true;
        }
        return this.guildPermissions.contains(guildPermission);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public void addPermission(GuildPermission guildPermission){
        if(!this.guildPermissions.contains(guildPermission)) this.guildPermissions.add(guildPermission);
    }

    public void removePermission(GuildPermission guildPermission){
        if(this.guildPermissions.contains(guildPermission)) this.guildPermissions.remove(guildPermission);
    }

    public void changePermission(GuildPermission guildPermission){
        if(hasPermission(guildPermission)){
            this.guildPermissions.remove(guildPermission);
            return;
        }
        this.guildPermissions.add(guildPermission);
        return;
    }

    public List<GuildPermission> getGuildPermissions() {
        return new ArrayList<>(this.guildPermissions);
    }

    public void setGuildPermissions(List<GuildPermission> guildPermissions) {
        this.guildPermissions = guildPermissions;
    }
}
