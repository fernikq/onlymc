package pl.fernikq.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.fernikq.core.command.CommandManager;
import pl.fernikq.core.command.admin.GroupCommand;
import pl.fernikq.core.command.player.SethomeCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.listener.inventory.InventoryClickListener;
import pl.fernikq.core.listener.player.PlayerJoinListener;
import pl.fernikq.core.listener.player.PlayerQuitListener;
import pl.fernikq.core.mysql.MySQL;
import pl.fernikq.core.tag.TagManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.UserManager;
import pl.fernikq.core.user.home.HomeManager;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TeleportManager;

public class CorePlugin extends JavaPlugin {

    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private MySQL mySQL;
    private CommandManager commandManager;
    private UserManager userManager;
    private TagManager tagManager;
    private HomeManager homeManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        initConfigurations();
        initDatabase();
        initManagers();
        initData();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            this.userManager.getUser(player.getUniqueId()).peek(user -> this.userManager.getUserData().updateUser(user));
            player.kickPlayer(ChatUtil.fixColor("&c&lRestart serwera!"));
        });
        Bukkit.getWorlds().forEach(world -> world.save());
    }

    private void initManagers(){
        this.commandManager = new CommandManager();
        this.userManager = new UserManager(this);
        this.tagManager = new TagManager(this);
        this.homeManager = new HomeManager(this);
        this.teleportManager = new TeleportManager(this);
    }

    private void initData(){
        this.userManager.init();
        this.homeManager.init();
    }

    private void initDatabase(){
        this.mySQL = new MySQL(this);
    }

    private void initConfigurations(){
        this.configManager = new ConfigManager(this);
        this.messagesManager = new MessagesManager(this);

        this.configManager.reload();
        this.messagesManager.reload();
    }

    private void registerCommands(){

        //ADMIN
        new GroupCommand("group", new String[]{"pex"}, UserGroup.ROOT, this).register();

        //PLAYER
        new SethomeCommand("sethome", new String[0], UserGroup.PLAYER, this).register();
    }

    private void registerListeners(){
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new InventoryClickListener(this);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public TagManager getTagManager() {
        return tagManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }
}
