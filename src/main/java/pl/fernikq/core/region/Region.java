package pl.fernikq.core.region;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import pl.fernikq.core.user.UserGroup;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private String regionName;
    private Location lowerCorner;
    private Location upperCorner;
    private int priority;
    private List<String> blockedCommands;
    private boolean canPlayerBuild;
    private boolean canPlayerDestroy;
    private boolean canPlayerThrowPearls;
    private boolean canPlayerUserBuckets;
    private boolean canPlayerHurtOther;
    private boolean canPlayerBeHurt;
    private boolean canPlayerSpawnVehicles;
    private boolean canPlayerIgniteBlocks;
    private boolean canPlayerIgniteTNT;
    private boolean canPlayerChangePaintings;
    private boolean canPlayerChangeFrames;
    private boolean canPlayerDestroyFarmlands;
    private boolean canPlayerBeHungry;
    private boolean canPlayerJoinDuringPVP;
    private boolean canEntityChangePaintings;
    private boolean canEntityChangeFrames;
    private boolean canEntityIgniteTNT;
    private boolean canEntityExplode;
    private boolean canEntityIgniteBlocks;
    private boolean isStoneGeneratorRegion;
    private boolean allowFireSpread;
    private boolean allowMobSpawning;
    private boolean allowLeavesDecay;

    public boolean isIn(Location location){
        if(!StringUtils.equals(location.getWorld().getName(), this.lowerCorner.getWorld().getName())){
            return false;
        }
        return location.toVector().isInAABB(this.lowerCorner.toVector(), this.upperCorner.toVector());
    }

    public String getRegionName() {
        return regionName;
    }

    public Location getLowerCorner() {
        return lowerCorner;
    }

    public Location getUpperCorner() {
        return upperCorner;
    }

    public int getPriority() {
        return priority;
    }

    public List<String> getBlockedCommands() {
        return new ArrayList<>(this.blockedCommands);
    }

    public Region setRegionName(String regionName) {
        this.regionName = regionName;
        return this;
    }

    public Region setLowerCorner(Location lowerCorner) {
        this.lowerCorner = lowerCorner;
        return this;
    }

    public Region setUpperCorner(Location upperCorner) {
        this.upperCorner = upperCorner;
        return this;
    }

    public Region setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public Region setBlockedCommands(List<String> blockedCommands) {
        this.blockedCommands = blockedCommands;
        return this;
    }

    public boolean isCanEntityIgniteBlocks() {
        return canEntityIgniteBlocks;
    }

    public boolean isCanPlayerBuild() {
        return canPlayerBuild;
    }

    public boolean isCanPlayerDestroy() {
        return canPlayerDestroy;
    }

    public boolean isCanPlayerThrowPearls() {
        return canPlayerThrowPearls;
    }

    public boolean isCanPlayerUserBuckets() {
        return canPlayerUserBuckets;
    }

    public boolean isCanPlayerHurtOther() {
        return canPlayerHurtOther;
    }

    public boolean isCanPlayerBeHurt() {
        return canPlayerBeHurt;
    }

    public boolean isCanPlayerSpawnVehicles() {
        return canPlayerSpawnVehicles;
    }

    public boolean isCanPlayerIgniteBlocks() {
        return canPlayerIgniteBlocks;
    }

    public boolean isCanPlayerIgniteTNT() {
        return canPlayerIgniteTNT;
    }

    public boolean isCanPlayerChangePaintings() {
        return canPlayerChangePaintings;
    }

    public boolean isCanPlayerChangeFrames() {
        return canPlayerChangeFrames;
    }

    public boolean isCanPlayerDestroyFarmlands() {
        return canPlayerDestroyFarmlands;
    }

    public boolean isCanPlayerBeHungry() {
        return canPlayerBeHungry;
    }

    public boolean isCanPlayerJoinDuringPVP() {
        return canPlayerJoinDuringPVP;
    }

    public boolean isCanEntityChangePaintings() {
        return canEntityChangePaintings;
    }

    public boolean isCanEntityChangeFrames() {
        return canEntityChangeFrames;
    }

    public boolean isCanEntityIgniteTNT() {
        return canEntityIgniteTNT;
    }

    public boolean isCanEntityExplode() {
        return canEntityExplode;
    }

    public boolean isStoneGeneratorRegion() {
        return isStoneGeneratorRegion;
    }

    public boolean isAllowFireSpread() {
        return allowFireSpread;
    }

    public boolean isAllowMobSpawning() {
        return allowMobSpawning;
    }

    public boolean isAllowLeavesDecay() {
        return allowLeavesDecay;
    }

    public Region setCanPlayerBuild(boolean canPlayerBuild) {
        this.canPlayerBuild = canPlayerBuild;
        return this;
    }

    public Region setCanPlayerDestroy(boolean canPlayerDestroy) {
        this.canPlayerDestroy = canPlayerDestroy;
        return this;
    }

    public Region setCanEntityIgniteBlocks(boolean canEntityIgniteBlocks) {
        this.canEntityIgniteBlocks = canEntityIgniteBlocks;
        return this;
    }

    public Region setCanPlayerThrowPearls(boolean canPlayerThrowPearls) {
        this.canPlayerThrowPearls = canPlayerThrowPearls;
        return this;
    }

    public Region setCanPlayerUserBuckets(boolean canPlayerUserBuckets) {
        this.canPlayerUserBuckets = canPlayerUserBuckets;
        return this;
    }

    public Region setCanPlayerHurtOther(boolean canPlayerHurtOther) {
        this.canPlayerHurtOther = canPlayerHurtOther;
        return this;
    }

    public Region setCanPlayerBeHurt(boolean canPlayerBeHurt) {
        this.canPlayerBeHurt = canPlayerBeHurt;
        return this;
    }

    public Region setCanPlayerSpawnVehicles(boolean canPlayerSpawnVehicles) {
        this.canPlayerSpawnVehicles = canPlayerSpawnVehicles;
        return this;
    }

    public Region setCanPlayerIgniteBlocks(boolean canPlayerIgniteBlocks) {
        this.canPlayerIgniteBlocks = canPlayerIgniteBlocks;
        return this;
    }

    public Region setCanPlayerIgniteTNT(boolean canPlayerIgniteTNT) {
        this.canPlayerIgniteTNT = canPlayerIgniteTNT;
        return this;
    }

    public Region setCanPlayerChangePaintings(boolean canPlayerChangePaintings) {
        this.canPlayerChangePaintings = canPlayerChangePaintings;
        return this;
    }

    public Region setCanPlayerChangeFrames(boolean canPlayerChangeFrames) {
        this.canPlayerChangeFrames = canPlayerChangeFrames;
        return this;
    }

    public Region setCanPlayerDestroyFarmlands(boolean canPlayerDestroyFarmlands) {
        this.canPlayerDestroyFarmlands = canPlayerDestroyFarmlands;
        return this;
    }

    public Region setCanPlayerBeHungry(boolean canPlayerBeHungry) {
        this.canPlayerBeHungry = canPlayerBeHungry;
        return this;
    }

    public Region setCanPlayerJoinDuringPVP(boolean canPlayerJoinDuringPVP) {
        this.canPlayerJoinDuringPVP = canPlayerJoinDuringPVP;
        return this;
    }

    public Region setCanEntityChangePaintings(boolean canEntityChangePaintings) {
        this.canEntityChangePaintings = canEntityChangePaintings;
        return this;
    }

    public Region setCanEntityChangeFrames(boolean canEntityChangeFrames) {
        this.canEntityChangeFrames = canEntityChangeFrames;
        return this;
    }

    public Region setCanEntityIgniteTNT(boolean canEntityIgniteTNT) {
        this.canEntityIgniteTNT = canEntityIgniteTNT;
        return this;
    }

    public Region setCanEntityExplode(boolean canEntityExplode) {
        this.canEntityExplode = canEntityExplode;
        return this;
    }

    public Region setStoneGeneratorRegion(boolean stoneGeneratorRegion) {
        isStoneGeneratorRegion = stoneGeneratorRegion;
        return this;
    }

    public Region setAllowFireSpread(boolean allowFireSpread) {
        this.allowFireSpread = allowFireSpread;
        return this;
    }

    public Region setAllowMobSpawning(boolean allowMobSpawning) {
        this.allowMobSpawning = allowMobSpawning;
        return this;
    }

    public Region setAllowLeavesDecay(boolean allowLeavesDecay) {
        this.allowLeavesDecay = allowLeavesDecay;
        return this;
    }
}
