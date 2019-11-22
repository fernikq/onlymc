package pl.fernikq.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.fernikq.core.automessage.AutoMessageManager;
import pl.fernikq.core.command.CommandManager;
import pl.fernikq.core.command.admin.*;
import pl.fernikq.core.command.player.*;
import pl.fernikq.core.command.premium.RepairCommand;
import pl.fernikq.core.command.simpleCommand.SimpleCommandManager;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.crafting.GeneratorManager;
import pl.fernikq.core.crafting.stoneGenerator.StoneGeneratorManager;
import pl.fernikq.core.drop.DropManager;
import pl.fernikq.core.dummy.DummyManager;
import pl.fernikq.core.guild.GuildManager;
import pl.fernikq.core.guild.alliances.AllianceManager;
import pl.fernikq.core.inventory.user.UserInventory;
import pl.fernikq.core.kit.KitManager;
import pl.fernikq.core.listener.block.*;
import pl.fernikq.core.listener.entity.*;
import pl.fernikq.core.listener.inventory.InventoryClickListener;
import pl.fernikq.core.listener.player.*;
import pl.fernikq.core.mysql.MySQL;
import pl.fernikq.core.region.RegionManager;
import pl.fernikq.core.shop.ShopManager;
import pl.fernikq.core.tag.TagManager;
import pl.fernikq.core.task.DepositeTask;
import pl.fernikq.core.task.RemoveItemsTask;
import pl.fernikq.core.task.SimpleTask;
import pl.fernikq.core.task.StoneGeneratorTask;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.user.UserManager;
import pl.fernikq.core.user.UserPermissionsManager;
import pl.fernikq.core.user.home.HomeManager;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.RankingUtil;
import pl.fernikq.core.util.TeleportManager;
import pl.fernikq.core.vanish.VanishManager;
import pl.fernikq.core.warp.WarpManager;

import java.util.Arrays;
import java.util.List;

public class CorePlugin extends JavaPlugin {

    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private MySQL mySQL;
    private CommandManager commandManager;
    private UserManager userManager;
    private TagManager tagManager;
    private HomeManager homeManager;
    private TeleportManager teleportManager;
    private AutoMessageManager autoMessageManager;
    private WarpManager warpManager;
    private UserInventory userInventory;
    private KitManager kitManager;
    private VanishManager vanishManager;
    private SimpleCommandManager simpleCommandManager;
    private RegionManager regionManager;
    private GeneratorManager generatorManager;
    private StoneGeneratorManager stoneGeneratorManager;
    private List<SimpleTask> simpleTasks;
    private ShopManager shopManager;
    private DropManager dropManager;
    private UserPermissionsManager userPermissionsManager;
    private DummyManager dummyManager;
    private GuildManager guildManager;
    private AllianceManager allianceManager;

    @Override
    public void onEnable() {
        initConfigurations();
        initDatabase();
        initManagers();
        initData();
        registerCommands();
        registerListeners();
        initPacketReceiving();
        this.simpleTasks = Arrays.asList(new StoneGeneratorTask(this), new DepositeTask(this), new RemoveItemsTask(this));
        simpleTasks.forEach(SimpleTask::start);
    }

    @Override
    public void onDisable(){
        this.mySQL.closeConnection();
        this.mySQL.openWithoutTask();
        Bukkit.getOnlinePlayers().forEach(player -> {
            this.userManager.getUser(player.getUniqueId()).peek(user -> this.userManager.updateUser(user));
            player.kickPlayer(ChatUtil.fixColor("&c&lRestart serwera!"));
        });
        Bukkit.getWorlds().forEach(world -> world.save());
        simpleTasks.forEach(SimpleTask::stop);
        this.mySQL.closeConnection();
    }

    private void initPacketReceiving(){
        //new BlockDigListener(this);

        //TODO
    }

    private void initManagers(){
        this.commandManager = new CommandManager();
        this.userManager = new UserManager(this);
        this.tagManager = new TagManager(this);
        this.homeManager = new HomeManager(this);
        this.teleportManager = new TeleportManager(this);
        this.autoMessageManager = new AutoMessageManager(this);
        this.warpManager = new WarpManager(this);
        this.userInventory = new UserInventory(this);
        this.kitManager = new KitManager(this);
        this.vanishManager = new VanishManager(this);
        this.simpleCommandManager = new SimpleCommandManager(this);
        this.regionManager = new RegionManager(this);
        this.generatorManager = new GeneratorManager(this);
        this.stoneGeneratorManager = new StoneGeneratorManager(this);
        this.shopManager = new ShopManager(this);
        this.dropManager = new DropManager(this);
        this.userPermissionsManager = new UserPermissionsManager(this);
        this.dummyManager = new DummyManager(this);
        this.guildManager = new GuildManager(this);
        this.allianceManager = new AllianceManager(this);
    }

    private void initData(){
        this.userManager.init();
        this.homeManager.init();
        this.warpManager.init();
        this.guildManager.init();
        this.allianceManager.init();
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
        new GroupCommand("group", new String[0], UserGroup.ROOT, this).register();
        new GamemodeCommand("gamemode", new String[]{"gm"}, UserGroup.MOD, this).register();
        new GodCommand("god", new String[0], UserGroup.HELPER, this).register();
        new FlyCommand("fly", new String[0], UserGroup.HELPER, this).register();
        new ClearCommand("clear", new String[0], UserGroup.HELPER, this).register();
        new InvseeCommand("invsee", new String[0], UserGroup.HELPER, this).register();
        new HealCommand("heal", new String[0], UserGroup.HELPER, this).register();
        new GiveCommand("give", new String[0], UserGroup.MOD, this).register();
        new EnchantCommand("enchant", new String[0], UserGroup.MOD, this).register();
        new CoreCommand("core", new String[0], UserGroup.ROOT, this).register();
        new SetWarpCommand("setwarp", new String[0], UserGroup.MOD, this).register();
        new DelWarpCommand("delwarp", new String[0], UserGroup.MOD, this).register();
        new WarpGroupCommand("warpgroup", new String[0], UserGroup.MOD, this).register();
        new NameCommand("name", new String[0], UserGroup.HELPER, this).register();
        new LoreCommand("lore", new String[0], UserGroup.HELPER, this).register();
        new ChatCommand("chat", new String[0], UserGroup.HELPER, this).register();
        new TphereCommand("tphere", new String[]{"s"}, UserGroup.HELPER, this).register();
        new TeleportCommand("teleport", new String[]{"tp"}, UserGroup.HELPER, this).register();
        new VanishCommand("vanish", new String[]{"v"}, UserGroup.HELPER, this).register();
        new SetSpawnCommand("setspawn", new String[0], UserGroup.ADMIN, this).register();
        new TitleBroadcastCommand("tbc", new String[0], UserGroup.MOD, this).register();
        new BroadcastCommand("bc", new String[0], UserGroup.MOD, this).register();
        new CaseCommand("case", new String[0], UserGroup.MOD, this).register();
        new CaseAllCommand("caseall", new String[0], UserGroup.MOD, this).register();
        new TurboAllCommand("turboall", new String[0], UserGroup.MOD, this).register();
        new TurboCommand("turbo", new String[0], UserGroup.MOD, this).register();
        new HeadCommand("head", new String[0], UserGroup.HELPER, this).register();
        new WhoisCommand("whois", new String[0], UserGroup.HELPER, this).register();
        new ReloadPermissionsCommand("reloadpermissions", new String[0], UserGroup.ROOT, this).register();

        //PLAYER
        new SethomeCommand("sethome", new String[0], UserGroup.PLAYER, this).register();
        new DelhomeCommand("delhome", new String[0], UserGroup.PLAYER, this).register();
        new HomelistCommand("homelist", new String[0], UserGroup.PLAYER, this).register();
        new HomeCommand("home", new String[0], UserGroup.PLAYER, this).register();
        new MessageCommand("msg", new String[0], UserGroup.PLAYER, this).register();
        new ReplyCommand("reply", new String[]{"r"}, UserGroup.PLAYER, this).register();
        new RepairCommand("repair", new String[0], UserGroup.VIP, this).register();
        new HelpopCommand("helpop", new String[0], UserGroup.PLAYER, this).register();
        new WarpCommand("warp", new String[]{"warpy"}, UserGroup.PLAYER, this).register();
        new KitCommand("kit", new String[]{"kity"}, UserGroup.PLAYER, this).register();
        new ShopCommand("sklep", new String[0], UserGroup.PLAYER, this).register();
        new SpawnCommand("spawn", new String[0], UserGroup.PLAYER, this).register();
        new TeleportRequestCommand("tpa", new String[0], UserGroup.PLAYER, this).register();
        new TeleportAcceptCommand("tpaccept", new String[0], UserGroup.PLAYER, this).register();
        new CraftingsCommand("craftingi", new String[]{"crafting"}, UserGroup.PLAYER, this).register();
        new DepositeCommand("schowek", new String[]{"depozyt"}, UserGroup.PLAYER, this).register();
        new DropCommand("drop", new String[0], UserGroup.PLAYER, this).register();
        new CobblexCommand("cobblex", new String[]{"cx"}, UserGroup.PLAYER, this).register();
    }

    private void registerListeners(){
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new InventoryClickListener(this);
        new AsyncPlayerChatListener(this);
        new PlayerDamageListener(this);
        new PlayerLoginListener(this);
        new BlockPlaceListener(this);
        new BlockBreakListener(this);
        new EntityExplodeListener(this);
        new BucketListener(this);
        new TeleportListener(this);
        new PlayerInteractListener(this);
        new EntityDamageByEntityListener(this);
        new BlockBurnListener(this);
        new BlockIgniteListener(this);
        new BlockSpreadListener(this);
        new CreatureSpawnListener(this);
        new HangingBreakListener(this);
        new HangingPlaceListener(this);
        new EntityChangeBlockListener(this);
        new PlayerInteractEntityListener(this);
        new LeavesDecayListener(this);
        new PlayerCommandPreprocessListener(this);
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

    public AutoMessageManager getAutoMessageManager() {
        return autoMessageManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public UserInventory getUserInventory() {
        return userInventory;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public SimpleCommandManager getSimpleCommandManager() {
        return simpleCommandManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    public StoneGeneratorManager getStoneGeneratorManager() {
        return stoneGeneratorManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public DropManager getDropManager() {
        return dropManager;
    }

    public UserPermissionsManager getUserPermissionsManager() {
        return userPermissionsManager;
    }

    public DummyManager getDummyManager() {
        return dummyManager;
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public AllianceManager getAllianceManager() {
        return allianceManager;
    }
}
