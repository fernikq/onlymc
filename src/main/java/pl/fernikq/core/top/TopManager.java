package pl.fernikq.core.top;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.top.comparator.guild.*;
import pl.fernikq.core.top.comparator.user.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TopManager {

    private final CorePlugin plugin;
    private List<Sortable> tops;

    public TopManager(CorePlugin plugin){
        this.plugin = plugin;
        this.tops = Arrays.asList(new CoinsComparator(this.plugin, TopType.USER_COINS, TopKind.USER), new AssistsComparator(this.plugin, TopType.USER_ASSISTS, TopKind.USER),
                new CobblexComparator(this.plugin, TopType.USER_COBBLEX, TopKind.USER), new DeathsComparator(this.plugin, TopType.USER_DEATHS, TopKind.USER),
                new DistanceComparator(this.plugin, TopType.USER_DISTANCE, TopKind.USER), new KillsComparator(this.plugin, TopType.USER_KILLS, TopKind.USER),
                new LogoutsComparator(this.plugin, TopType.USER_LOGOUTS, TopKind.USER), new PointsComparator(this.plugin, TopType.USER_POINTS, TopKind.USER),
                new PremiumCaseComparator(this.plugin, TopType.USER_CASE, TopKind.USER), new StoneComparator(this.plugin, TopType.USER_STONE, TopKind.USER),
                new TimeComparator(this.plugin, TopType.USER_TIME, TopKind.USER), new GuildAssistsComparator(this.plugin, TopType.GUILD_ASSISTS, TopKind.GUILD),
                new GuildDeathsComparator(this.plugin, TopType.GUILD_DEATHS, TopKind.GUILD), new GuildKillsComparator(this.plugin, TopType.GUILD_KILLS, TopKind.GUILD),
                new GuildLogoutsComparator(this.plugin, TopType.GUILD_LOGOUTS, TopKind.GUILD), new GuildPointsComparator(this.plugin, TopType.GUILD_POINTS, TopKind.GUILD));
    }

    public Sortable getTopByType(TopType topType){
        return this.tops.stream().filter(sortable -> sortable.getTopType().equals(topType)).findAny().get();
    }

    public List<Sortable> getTopsByKind(TopKind topKind){
        return this.tops.stream().filter(sortable -> sortable.getTopKind().equals(topKind)).collect(Collectors.toList());
    }
}
