package pl.fernikq.core.command.player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

import java.util.HashSet;
import java.util.Set;

public class TeleportRequestCommand extends CustomCommand {

    private final CorePlugin plugin;

    public TeleportRequestCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/tpa <nick>"));
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null){
            return ChatUtil.sendMessage(sender, Lang.playerOffline);
        }
        if(target.equals(player)){
            return ChatUtil.sendMessage(player, MessagesManager.error("Nie mozesz wyslac prosby do siebie!"));
        }
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            User targetUser = this.plugin.getUserManager().getUser(target.getUniqueId()).getOrNull();
            if(targetUser == null){
                ChatUtil.sendMessage(player, Lang.userNotExists);
                return;
            }
            Set<User> blockedTpa = new HashSet<>(targetUser.getUserChat().getBlockedTpa());
            if(blockedTpa.contains(user) && !user.canByGroup(UserGroup.HELPER)){
                ChatUtil.sendMessage(sender, MessagesManager.error("Podany gracz ignoruje twoje prosby o teleportacja!"));
                return;
            }
            if(targetUser.getTpaRequests().asMap().containsKey(user)){
                ChatUtil.sendMessage(player, MessagesManager.error("Wyslales juz prosbe do tego gracza!"));
                return;
            }
            targetUser.getTpaRequests().asMap().putIfAbsent(user, 1000L);
            ChatUtil.sendMessage(player, "&8>> {n}Wyslales prosbe o teleportacje do gracza {c}"+target.getName());
            ChatUtil.sendMessage(target, "&8>> {n}Otrzymales prosbe o teleportacje od gracza {c}"+player.getName());
            TextComponent textComponent = new TextComponent("");
            TextComponent acceptCompontent = new TextComponent(ChatUtil.fixColor("{c}&lKliknij aby zaakceptowac"));
            acceptCompontent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatUtil.fixColor("{n}Kliknij aby zaakceptowac&8!"))));
            acceptCompontent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept "+player.getName()));
            textComponent.addExtra(ChatUtil.fixColor("&8>> "));
            textComponent.addExtra(acceptCompontent);
            textComponent.addExtra(ChatUtil.fixColor("&8, {n}lub wpisz {c}/tpaccept "+player.getName()));
            target.spigot().sendMessage(textComponent);
        });
        return true;
    }
}
