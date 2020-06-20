package pl.fernikq.core.top.comparator.guild;

import org.bukkit.Material;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.user.User;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;

import java.util.*;

public class GuildPointsComparator implements Sortable<Guild> {

    private final CorePlugin plugin;
    private List<Guild> sortedList;
    private Set<Guild> guilds;
    private TopType topType;
    private TopKind topKind;
    private boolean isSorted;
    private Comparator<Guild> guildComparator;

    public GuildPointsComparator(CorePlugin plugin, TopType topType, TopKind topKind){
        this.plugin = plugin;
        this.topType = topType;
        this.topKind = topKind;
        this.sortedList = new ArrayList<>();
        this.guilds = new HashSet<>();
        this.guildComparator = new Comparator<Guild>() {
            @Override
            public int compare(Guild o1, Guild o2) {
                int i = Integer.compare(o2.getPoints(), o1.getPoints());
                if(i == 0){
                    if(o1.getName() == null){
                        return -1;
                    }
                    if(o2.getName() == null){
                        return 1;
                    }
                    i = o1.getName().compareTo(o2.getName());
                }
                return i;
            }
        };
    }

    @Override
    public InventoryGUI getInventory(Guild object) {
        InventoryGUI inventoryGUI = new InventoryGUI("&8[ {c}&l"+this.topType.getGuiTitle()+" &8]", 6, true);
        for(int i = 0; i < 45; i++){
            Guild topGuild = this.getObjectByPosition(i);
            if(topGuild == null){
                break;
            }
            inventoryGUI.addItem(new ItemBuilder(Material.ENDER_PORTAL_FRAME).setName(ChatUtil.fixColor("{n}Pozycja {c}"+(i + 1))).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Gildia&8: {c}"+topGuild.getTag(), "&8>> {n}Punktow&8: {c}"+topGuild.getPoints()))).toItemStack());
        }
        inventoryGUI.setItem(49, new ItemBuilder(Material.ENDER_PORTAL_FRAME).setName(ChatUtil.fixColor("{n}Pozycja twojej gildii&8: {c}"+(object == null ? "Brak" : (getPositionByObject(object)+1))))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Punktow&8: {c}"+((object == null ? "Brak" : object.getPoints()))))).toItemStack());
        return inventoryGUI;
    }

    @Override
    public void sort() {
        this.sortedList = new ArrayList<>(this.guilds);
        if(this.guilds.isEmpty()){
            return;
        }
        this.sortedList.sort(this.guildComparator);
    }

    @Override
    public Guild getObjectByPosition(int position){
        List<Guild> sortedList = new ArrayList<>(this.sortedList);
        return sortedList.size() > position ? sortedList.get(position) : null;
    }

    @Override
    public int getPositionByObject(Guild guild){
        return new ArrayList<>(this.sortedList).indexOf(guild);
    }

    @Override
    public boolean isSorted() {
        return this.isSorted;
    }

    public void setSorted(boolean sorted) {
        this.isSorted = sorted;
    }

    @Override
    public void addObject(Guild guild){
        this.guilds.add(guild);
    }

    @Override
    public void removeObject(Guild guild){
        this.guilds.remove(guild);
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
