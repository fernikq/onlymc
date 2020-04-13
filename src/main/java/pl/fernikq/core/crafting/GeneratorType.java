package pl.fernikq.core.crafting;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public enum GeneratorType {

    STONE_GENERATOR("stoniarka"),
    OBSIDIAN_GENERATOR("boyfarmer"),
    SAND_GENERATOR("sandfarmer"),
    BLOCK_BREAKER("kopacz"),
    ENDERCHEST("enderchest"),
    RZUCANE("rzucane"),
    ANTITRAP("antynogi"),
    MAGIC_ROD("wedka");

    private final String name;

    private GeneratorType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static GeneratorType getGeneratorByName(String name){
        return getGenerators().find(generatorType -> generatorType.getName().equalsIgnoreCase(name)).getOrNull();
    }

    public static Set<GeneratorType> getGenerators(){
        return HashSet.of(values());
    }
}
