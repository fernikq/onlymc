package pl.fernikq.core.top.comparator.guild;

import org.bukkit.Material;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.guild.Guild;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;
import pl.fernikq.core.top.comparator.Sortable;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GuildLogoutsComparator implements Sortable<Guild> {

    private final CorePlugin plugin;
    private List<Guild> guildList;
    private TopType topType;
    private TopKind topKind;
    private Comparator<Guild> guildComparator;

    public GuildLogoutsComparator(CorePlugin plugin, TopType topType, TopKind topKind){
        this.plugin = plugin;
        this.topType = topType;
        this.topKind = topKind;
        this.guildList = new ArrayList<>();
        this.guildComparator = new Comparator<Guild>() {
            @Override
            public int compare(Guild o1, Guild o2) {
                int i = Integer.compare(o2.getLogouts(), o1.getLogouts());
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
            inventoryGUI.addItem(new ItemBuilder(Material.ENDER_PORTAL_FRAME).setName(ChatUtil.fixColor("{n}Pozycja {c}"+(i + 1))).setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Gildia&8: {c}"+topGuild.getTag(), "&8>> {n}Logoutow&8: {c}"+topGuild.getLogouts()))).toItemStack());
        }
        inventoryGUI.setItem(49, new ItemBuilder(Material.ENDER_PORTAL_FRAME).setName(ChatUtil.fixColor("{n}Pozycja twojej gildii&8: {c}"+(object == null ? "Brak" : (getPositionByObject(object)+1))))
                .setLore(ChatUtil.fixColor(Arrays.asList(" ", "&8>> {n}Logoutow&8: {c}"+((object == null ? "Brak" : object.getLogouts()))))).toItemStack());
        return inventoryGUI;
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
