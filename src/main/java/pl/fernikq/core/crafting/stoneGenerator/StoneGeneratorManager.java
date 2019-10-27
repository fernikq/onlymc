package pl.fernikq.core.crafting.stoneGenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.crafting.Generator;
import pl.fernikq.core.util.LocationUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StoneGeneratorManager {

    private ConcurrentMap<Location, StoneGenerator> generators;
    private Set<StoneGenerator> generatorsToRegen;
    private final CorePlugin plugin;
    private File stoneGeneratorFile;

    public StoneGeneratorManager(CorePlugin plugin){
        this.plugin = plugin;
        this.generatorsToRegen = new HashSet<>();
        this.generators = new ConcurrentHashMap<>();
        checkFile();
        loadGenerators();
        restoreGenerators();
    }

    public StoneGenerator getStoneGenerator(Location location){
        return getGenerators().get(location);
    }

    public void checkFile(){
        if(!this.plugin.getDataFolder().exists()){
            this.plugin.getDataFolder().mkdir();
        }
        stoneGeneratorFile = new File(this.plugin.getDataFolder(), "stoneGenerators");
        if(!stoneGeneratorFile.exists()){
            stoneGeneratorFile.mkdir();
        }
    }

    public void registerGenerator(StoneGenerator generator){
        this.generators.putIfAbsent(generator.getLocation(), generator);
    }

    public void saveGenerator(StoneGenerator generator){
        File file = new File(stoneGeneratorFile, LocationUtil.locationToString(generator.getLocation())+".yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set("location", LocationUtil.locationToString(generator.getLocation()));
        try {
            configuration.save(file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteGenerator(StoneGenerator generator){
        File file = new File(stoneGeneratorFile, LocationUtil.locationToString(generator.getLocation())+".yml");
        if(file.exists()){
            file.delete();
        }
        this.generators.remove(generator.getLocation());
        this.generatorsToRegen.remove(generator);
    }

    public void loadGenerators(){
        for(File file : stoneGeneratorFile.listFiles()){
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            StoneGenerator generator = new StoneGenerator(LocationUtil.locationFromString(configuration.getString("location")));
            registerGenerator(generator);
        }
    }

    public void regenGenerator(StoneGenerator generator){
        generator.setRegenerationTime(System.currentTimeMillis() + ConfigManager.stoneGeneratorRegenerationTime);
        this.generatorsToRegen.add(generator);
    }

    public void successfulRegen(StoneGenerator generator){
        generator.setRegenerationTime(0);
        this.generatorsToRegen.remove(generator);
    }

    public void restoreGenerators(){
        getGeneratorsSet().forEach(generator -> {
            generator.getLocation().getBlock().setType(Material.STONE);
        });
    }

    public ConcurrentMap<Location, StoneGenerator> getGenerators() {
        return new ConcurrentHashMap<>(this.generators);
    }

    public io.vavr.collection.Set<StoneGenerator> getGeneratorsSet(){
        return io.vavr.collection.HashSet.ofAll(getGenerators().values());
    }

    public Set<StoneGenerator> getGeneratorsToRegen() {
        return new HashSet<>(this.generatorsToRegen);
    }
}
