package pl.fernikq.core.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class WhoisCommand extends CustomCommand {

    private final CorePlugin plugin;

    public WhoisCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin) {
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/whois <nick>"));
        }
        this.plugin.getUserManager().getUser(args[0]).peek(user -> {
            ChatUtil.sendMessage(sender, "&8&m--------&8[ {c}&lWHOIS &8]&m--------", " ");
            ChatUtil.sendMessage(sender, "&8>> {n}Nick&8: {c}"+user.getName());
            ChatUtil.sendMessage(sender, "&8>> {n}Ranga&8: {c}"+user.getGroup().name());
            ChatUtil.sendMessage(sender, "&8>> {n}Online&8: {c}"+(user.asPlayer() == null ? "nie" : "tak"));
            ChatUtil.sendMessage(sender, "&8>> {n}UUID&8: {c}"+user.getUuid().toString());
            ChatUtil.sendMessage(sender, "&8>> {n}Gamemode&8: {c}"+(user.asPlayer() == null ? "offline" : user.asPlayer().getGameMode().name()));
            ChatUtil.sendMessage(sender, "&8>> {n}God&8: {c}"+(user.isGodMode() ? "tak" : "nie"));
            if(user.asPlayer() == null){
                ChatUtil.sendMessage(sender, "&8>> {n}Efekty&8: {c}brak");
            }else{
                if(user.asPlayer().getActivePotionEffects().isEmpty()){
                    ChatUtil.sendMessage(sender, "&8>> {n}Efekty&8: {c}brak");
                }else{
                    StringBuilder stringBuilder = new StringBuilder();
                    user.asPlayer().getActivePotionEffects().forEach(potionEffect -> {
                        stringBuilder.append("&8, {n}"+potionEffect.getType().getName().toLowerCase()+"&8:{c}"+potionEffect.getAmplifier());
                    });
                    ChatUtil.sendMessage(sender, "&8>> {n}Efekty&8: {c}"+stringBuilder.toString().replaceFirst("&8, ", ""));
                }
            }
            ChatUtil.sendMessage(sender, " ", "&8&m--------&8[ {c}&lWHOIS &8]&m--------");
        }).onEmpty(() -> ChatUtil.sendMessage(sender, Lang.userNotExists));
        return true;
    }
}
