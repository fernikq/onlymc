package pl.fernikq.core.listener.custom;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BlockDigEvent extends Event implements Cancellable {

    private final Player player;
    private final Block block;
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    public BlockDigEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return this.handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
