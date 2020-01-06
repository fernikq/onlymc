package pl.fernikq.core.user.backup;

import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.user.User;

public class BackupBuilder {

    private User user;
    private ItemStack[] items;
    private ItemStack[] armor;
    private ItemStack[] enderchest;
    private int points;
    private int deaths;
    private long deathTime;
    private int ping;
    private String reason;

    public static BackupBuilder builder(){
        return new BackupBuilder();
    }

    public Backup build(){
        Backup backup = new Backup();
        backup.setUser(this.user);
        backup.setItems(this.items);
        backup.setArmor(this.armor);
        backup.setEnderchest(this.enderchest);
        backup.setPoints(this.points);
        backup.setDeaths(this.deaths);
        backup.setDeathTime(this.deathTime);
        backup.setPing(this.ping);
        backup.setReason(this.reason);
        this.user.getBackups().add(backup);
        return backup;
    }

    public BackupBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public BackupBuilder setItems(ItemStack[] items) {
        this.items = items;
        return this;
    }

    public BackupBuilder setArmor(ItemStack[] armor) {
        this.armor = armor;
        return this;
    }

    public BackupBuilder setEnderchest(ItemStack[] enderchest) {
        this.enderchest = enderchest;
        return this;
    }

    public BackupBuilder setPoints(int points) {
        this.points = points;
        return this;
    }

    public BackupBuilder setDeaths(int deaths) {
        this.deaths = deaths;
        return this;
    }

    public BackupBuilder setDeathTime(long deathTime) {
        this.deathTime = deathTime;
        return this;
    }

    public BackupBuilder setPing(int ping) {
        this.ping = ping;
        return this;
    }

    public BackupBuilder setReason(String reason) {
        this.reason = reason;
        return this;
    }
}
