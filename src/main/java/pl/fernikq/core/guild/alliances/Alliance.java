package pl.fernikq.core.guild.alliances;

import pl.fernikq.core.guild.Guild;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Alliance {

    private Guild guild1;
    private Guild guild2;

    public Alliance(Guild guild1, Guild guild2){
        this.guild1 = guild1;
        this.guild2 = guild2;
    }

    public Guild getGuild1() {
        return guild1;
    }

    public void setGuild1(Guild guild1) {
        this.guild1 = guild1;
    }

    public Guild getGuild2() {
        return guild2;
    }

    public void setGuild2(Guild guild2) {
        this.guild2 = guild2;
    }
}
