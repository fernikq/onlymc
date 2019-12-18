package pl.fernikq.core.top;

import org.bukkit.Material;

public enum TopType {

    USER_COINS("TOPKA MONET", Material.DOUBLE_PLANT);

    private String name;
    private Material material;

    private TopType(String name, Material material){
        this.name = name;
        this.material = material;
    }
}
