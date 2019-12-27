package pl.fernikq.core;

public class CoreAPI {

    private static CorePlugin plugin;

    public static CorePlugin getPlugin() {
        return plugin;
    }

    public static void setPlugin(CorePlugin plugin) {
        CoreAPI.plugin = plugin;
    }
}
