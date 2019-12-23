package pl.fernikq.core.top.comparator.guild;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.top.comparator.Sortable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuildDeathsComparator implements Sortable<Guild> {

    private final CorePlugin plugin;
    private List<Guild> guildList;
    private TopType topType;
    private TopKind topKind;
    private Comparator<Guild> guildComparator;

    public GuildDeathsComparator(CorePlugin plugin, TopType topType, TopKind topKind){
        this.plugin = plugin;
        this.topType = topType;
        this.topKind = topKind;
        this.guildList = new ArrayList<>();
        this.guildComparator = new Comparator<Guild>() {
            @Override
            public int compare(Guild o1, Guild o2) {
                int i = Integer.compare(o2.getDeaths(), o1.getDeaths());
                if(i == 0){
                    if(o1.getName() == null){
                        return -1;
                    }
                    if(o2.getName() == null){
                        return 1;
                    }
                    i = o1.getName().compareTo(o2.getName());
                }
                return 0;
            }
        };
    }

    @Override
    public void sort() {
        this.guildList.sort(this.guildComparator);
    }

    @Override
    public Guild getObjectByPosition(int position){
        return this.guildList.size() > position ? this.guildList.get(position) : null;
    }

    @Override
    public int getPositionByObject(Guild user){
        return this.guildList.indexOf(user);
    }

    @Override
    public void addObject(Guild user){
        if(this.guildList.contains(user)){
            return;
        }
        this.guildList.add(user);
    }

    @Override
    public void removeObject(Guild user){
        if(!this.guildList.contains(user)){
            return;
        }
        this.guildList.add(user);
    }

    @Override
    public TopType getTopType() {
        return topType;
    }

    @Override
    public TopKind getTopKind() {
        return topKind;
    }
}
