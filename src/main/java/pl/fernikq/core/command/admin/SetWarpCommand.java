package pl.fernikq.core.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.warp.Warp;

public class SetWarpCommand extends CustomCommand {

    private final CorePlugin plugin;

    public SetWarpCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/setwarp <nazwa> || /setwarp <nazwa> <ranga>"));
        }
        Player player = (Player)sender;
        String warpName = args[0];
        if(warpName.length() > 32){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Nazwa warpu nie mozesz byc dluzsza niz 32 znaki!"));
        }
        if(args.length == 1){
            this.plugin.getWarpManager().getWarp(warpName).onEmpty(() -> {
                Warp warp = new Warp(warpName, player.getLocation(), UserGroup.PLAYER);
                this.plugin.getWarpManager().addWarp(warp);
                ChatUtil.sendMessage(sender, "&8>> {n}Ustawiles warp o nazwie {c}"+warpName);
                return;
            }).peek(warp -> {
                warp.setLocation(player.getLocation());
                this.plugin.runAsync(() -> this.plugin.getWarpManager().getWarpData().updateWarp(warp));
                ChatUtil.sendMessage(sender, "&8>> {n}Zmieniles lokalizacje warpu {c}"+warp.getName());
                return;
            });
            return true;
        }
        String groupName = args[1];
        if(UserGroup.getByName(groupName) == null){
            ChatUtil.sendMessage(sender, MessagesManager.error("Podana ranga nie istnieje!"));
            return ChatUtil.sendMessage(sender, "&8>> {n}Dostepne rangi&8: "+UserGroup.getPrefixesToString());
        }
        this.plugin.getWarpManager().getWarp(warpName).onEmpty(() -> {
            Warp warp = new Warp(warpName, player.getLocation(), UserGroup.getByName(groupName));
            this.plugin.getWarpManager().addWarp(warp);
            ChatUtil.sendMessage(sender, "&8>> {n}Ustawiles warp o nazwie {c}"+warpName);
            return;
        }).peek(warp -> {
            warp.setLocation(player.getLocation());
            warp.setRequiredGroup(UserGroup.getByName(groupName));
            this.plugin.runAsync(() -> this.plugin.getWarpManager().getWarpData().updateWarp(warp));
            ChatUtil.sendMessage(sender, "&8>> {n}Zmieniles lokalizacje i wymagana range warpu {c}"+warp.getName());
            return;
        });
        return true;
    }
}
