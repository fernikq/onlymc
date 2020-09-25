package pl.fernikq.core.magiccase.draw;

import org.bukkit.entity.Player;
import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.util.RandomUtil;

public class MagicCaseDraw {

    private Player player;
    private int repetitions;
    private InventoryGUI inventoryGUI;

    public MagicCaseDraw(Player player, InventoryGUI inventoryGUI) {
        this.player = player;
        this.repetitions = RandomUtil.getRandInt(8, 16);
        this.inventoryGUI = inventoryGUI;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public InventoryGUI getInventoryGUI() {
        return inventoryGUI;
    }
}
