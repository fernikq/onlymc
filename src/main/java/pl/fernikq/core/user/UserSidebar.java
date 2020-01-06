package pl.fernikq.core.user;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.PlayerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSidebar {

    private User user;
    private Map<Integer, String> sidebarLines;
    private boolean enabled;
    private String sidebarName;

    public UserSidebar(User user) {
        this.user = user;
        this.sidebarLines = new HashMap<>();
        this.enabled = true;
    }

    public void create(){
        if(!this.enabled){
            return;
        }
        if(this.user.asPlayer() == null){
            return;
        }
        if(this.user.getScoreboard() == null){
            return;
        }
        Scoreboard scoreboard = this.user.getScoreboard();
        if(hasSidebar()){
            scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            this.sidebarLines.clear();
            scoreboard.getObjective("CORE-SIDEBAR").unregister();
        }
        int id = 0;
        scoreboard.registerNewObjective("CORE-SIDEBAR", "dummy").setDisplaySlot(DisplaySlot.SIDEBAR);
        String sidebarName = ChatUtil.fixColor(MessagesManager.sidebarName);
        this.sidebarName = sidebarName;
        scoreboard.getObjective("CORE-SIDEBAR").setDisplayName(sidebarName);
        for(String string : MessagesManager.playerSidebarLines){
            addLine(id, ChatUtil.fixColor(replaceLine(string)));
            id--;
        }
    }

    public void update(){
        if(!this.enabled){
            return;
        }
        if(this.user.asPlayer() == null){
            return;
        }
        if(this.user.getScoreboard() == null){
            return;
        }
        if(!hasSidebar()){
            create();
        }
        Scoreboard scoreboard = this.user.getScoreboard();
        if(!scoreboard.getObjective("CORE-SIDEBAR").getDisplayName().equals(this.sidebarName)){
            String sidebarName = ChatUtil.fixColor(MessagesManager.sidebarName);
            this.sidebarName = sidebarName;
            scoreboard.getObjective("CORE-SIDEBAR").setDisplayName(sidebarName);
        }
        int id = 0;
        for(String string : MessagesManager.playerSidebarLines){
            updateLine(id, ChatUtil.fixColor(replaceLine(string)));
            id--;
        }
    }

    public void remove(){
        if(this.user.asPlayer() == null){
            return;
        }
        if(this.user.getScoreboard() == null){
            return;
        }
        Scoreboard scoreboard = this.user.getScoreboard();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        this.sidebarLines.clear();
        scoreboard.getObjective("CORE-SIDEBAR").unregister();
    }

    private String replaceLine(String line){
        line = line.replace("{GUILD}", this.user.hasGuild() ? this.user.getGuild().getTag() : "Brak");
        line = line.replace("{NICK}", this.user.getName());
        line = line.replace("{POINTS}", Integer.toString(this.user.getUserStat().getPoints()));
        line = line.replace("{KILLS}", Integer.toString(this.user.getUserStat().getKills()));
        line = line.replace("{DEATHS}", Integer.toString(this.user.getUserStat().getDeaths()));
        line = line.replace("{LOGOUTS}", Integer.toString(this.user.getUserStat().getLogouts()));
        line = line.replace("{ASSISTS}", Integer.toString(this.user.getUserStat().getAssists()));
        line = line.replace("{TO-NEXT-LEVEL}", Integer.toString(this.user.getUserStat().getStoneToNextLevel()));
        line = line.replace("{COINS}", Integer.toString(this.user.getUserStat().getCoins()));
        line = line.replace("{PING}", Integer.toString(PlayerUtil.getPing(this.user.asPlayer())));
        return line;
    }

    public void removeLine(int id){
        if(!hasLine(id)){
            return;
        }
        Scoreboard scoreboard = this.user.getScoreboard();
        scoreboard.resetScores(this.sidebarLines.get(id));
        this.sidebarLines.remove(id);
        this.sidebarLines.remove(id);
    }

    public void addLine(int id, String string){
        if(hasLine(id)){
            return;
        }
        Scoreboard scoreboard = this.user.getScoreboard();
        scoreboard.getObjective("CORE-SIDEBAR").getScore(string).setScore(id);
        this.sidebarLines.put(id, string);
    }

    public void updateLine(int id, String line){
        if(!hasLine(id)){
            addLine(id, line);
            return;
        }
        if(getLine(id).equals(line)){
            return;
        }
        removeLine(id);
        addLine(id, line);
    }

    public boolean hasLine(int id){
        return this.sidebarLines.get(id) != null;
    }

    public String getLine(int id){
        return this.sidebarLines.get(id);
    }

    public boolean hasSidebar(){
        return this.user.getScoreboard().getObjective("CORE-SIDEBAR") != null;
    }

    public String getSidebarName() {
        return sidebarName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<Integer, String> getSidebarLines() {
        return sidebarLines;
    }

    public void setSidebarLines(Map<Integer, String> sidebarLines) {
        this.sidebarLines = sidebarLines;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
