package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.check.PlayerCheckUtil;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.LocationUtil;
import pl.fernikq.core.util.TimeUtil;
import pl.fernikq.core.util.TitleUtil;

public class ServiceCommand extends CustomCommand {

    private final CorePlugin plugin;

    public ServiceCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 2){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/service <itemy, case, sklep, kity, freeze> <czas>"));
        }
        long time = TimeUtil.getTime(args[1]);
        if(time == 0){
            return ChatUtil.sendMessage(sender, Lang.badTimeFormat);
        }
        time += System.currentTimeMillis();
        switch(args[0].toLowerCase()) {
            case "items":
            case "itemy": {
                ConfigManager.diamondItemsBlockTime = time;
                this.plugin.getConfigManager().save();
                ChatUtil.sendMessage(sender, "&8>> &fZablokowales diamentowe przedmioty do {c}"+TimeUtil.getDate(time));
                return true;
            }
            case "case": {
                ConfigManager.premiumCaseBlockTime = time;
                this.plugin.getConfigManager().save();
                ChatUtil.sendMessage(sender, "&8>> &fZablokowales premiumcase'y do {c}"+TimeUtil.getDate(time));
                return true;
            }
            case "shop":
            case "sklep":{
                ConfigManager.shopBlockTime = time;
                this.plugin.getConfigManager().save();
                ChatUtil.sendMessage(sender, "&8>> &fZablokowales sklep do {c}"+TimeUtil.getDate(time));
                return true;
            }
            case "kits":
            case "kity":{
                ConfigManager.kitsBlockTime = time;
                this.plugin.getConfigManager().save();
                ChatUtil.sendMessage(sender, "&8>> &fZablokowales kity premium do {c}"+TimeUtil.getDate(time));
                return true;
            }
            case "freeze":{
                ConfigManager.freezeTime = time;
                this.plugin.getConfigManager().save();
                ChatUtil.sendMessage(sender, "&8>> &fUstawiles zamrozenie do {c}"+TimeUtil.getDate(time));
                return true;
            }
            default:{
                return ChatUtil.sendMessage(sender, MessagesManager.usage("/service <itemy, case, sklep, kity> <czas>"));
            }
        }
    }
}
