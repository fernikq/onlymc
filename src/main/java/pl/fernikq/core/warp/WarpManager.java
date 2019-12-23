package pl.fernikq.core.warp;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.user.User;

import java.util.HashMap;
import java.util.Map;

public class WarpManager {

    private final CorePlugin plugin;
    private Map<String, Warp> warps;
    private WarpData warpData;

    public WarpManager(CorePlugin plugin){
        this.plugin = plugin;
        this.warps = new HashMap<>();
    }

    public void addWarp(Warp warp){
        this.warps.putIfAbsent(warp.getName().toLowerCase(), warp);
        this.plugin.runAsync(() -> this.warpData.insertWarp(warp));
    }

    public void removeWarp(Warp warp){
        this.warps.remove(warp.getName().toLowerCase());
        this.plugin.runAsync(() -> this.warpData.deleteWarp(warp));
    }

    public void registerWarp(Warp warp){
        this.warps.putIfAbsent(warp.getName().toLowerCase(), warp);
    }

    public Option<Warp> getWarp(String name){
        return Option.of(this.warps.get(name.toLowerCase()));
    }

    public boolean canTeleport(User user, Warp warp){
        return user.canByGroup(warp.getRequiredGroup());
    }

    public void init(){
        this.warpData = new WarpData(this.plugin);
    }

    public String getWarpsToString(){
        StringBuilder builder = new StringBuilder();
        for(Warp warp : getWarps()){
            builder.append("&8, {c}").append(warp.getName());
        }
        return builder.toString().replaceFirst("&8, ", "");
    }

    public Set<Warp> getWarps(){
        return HashSet.ofAll(new HashMap<String, Warp>(this.warps).values());
    }

    public WarpData getWarpData() {
        return warpData;
    }
}
