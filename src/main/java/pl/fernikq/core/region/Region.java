package pl.fernikq.core.region;

import org.bukkit.Location;
import pl.fernikq.core.user.UserGroup;

public class Region {

    private String regionName;
    private Location lowerCorner;
    private Location upperCorner;
    private int priority;
    private boolean canBuild;
    private boolean canDestroy;
    private boolean canThrowPearls;
    private boolean canUseBuckets;
    private boolean canExplode;
    private boolean canHurt;
    private boolean canSpawnVehicles;
    private boolean isStoneGeneratorRegion;
    private boolean allowFireSpread;
    private boolean canSpreadFire;
    private boolean allowMobSpawning;
    private boolean canEnterDuringFight;
    private boolean canDestroyFarmland;
    private boolean canDestroyFrames;
    private boolean canRotateItemInFrame;
    private boolean canDestroyPaintings;


    public boolean isIn(Location location){
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

    public boolean isCanBuild() {
        return canBuild;
    }

    public boolean isCanDestroy() {
        return canDestroy;
    }

    public boolean isCanThrowPearls() {
        return canThrowPearls;
    }

    public boolean isCanUseBuckets() {
        return canUseBuckets;
    }

    public boolean isCanExplode() {
        return canExplode;
    }

    public boolean isCanHurt() {
        return canHurt;
    }

    public boolean isCanSpawnVehicles() {
        return canSpawnVehicles;
    }

    public boolean isStoneGeneratorRegion() {
        return isStoneGeneratorRegion;
    }

    public boolean isAllowFireSpread() {
        return allowFireSpread;
    }

    public boolean isCanSpreadFire() {
        return canSpreadFire;
    }

    public boolean isAllowMobSpawning() {
        return allowMobSpawning;
    }

    public boolean isCanEnterDuringFight() {
        return canEnterDuringFight;
    }

    public boolean isCanDestroyFarmland() {
        return canDestroyFarmland;
    }

    public boolean isCanDestroyFrames() {
        return canDestroyFrames;
    }

    public boolean isCanRotateItemInFrame() {
        return canRotateItemInFrame;
    }

    public boolean isCanDestroyPaintings() {
        return canDestroyPaintings;
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

    public Region setCanBuild(boolean canBuild) {
        this.canBuild = canBuild;
        return this;
    }

    public Region setCanDestroy(boolean canDestroy) {
        this.canDestroy = canDestroy;
        return this;
    }

    public Region setCanThrowPearls(boolean canThrowPearls) {
        this.canThrowPearls = canThrowPearls;
        return this;
    }

    public Region setCanUseBuckets(boolean canUseBuckets) {
        this.canUseBuckets = canUseBuckets;
        return this;
    }

    public Region setCanExplode(boolean canExplode) {
        this.canExplode = canExplode;
        return this;
    }

    public Region setCanHurt(boolean canHurt) {
        this.canHurt = canHurt;
        return this;
    }

    public Region setCanSpawnVehicles(boolean canSpawnVehicles) {
        this.canSpawnVehicles = canSpawnVehicles;
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

    public Region setCanSpreadFire(boolean canSpreadFire) {
        this.canSpreadFire = canSpreadFire;
        return this;
    }

    public Region setAllowMobSpawning(boolean allowMobSpawning) {
        this.allowMobSpawning = allowMobSpawning;
        return this;
    }

    public Region setCanEnterDuringFight(boolean canEnterDuringFight) {
        this.canEnterDuringFight = canEnterDuringFight;
        return this;
    }

    public Region setCanDestroyFarmland(boolean canDestroyFarmland) {
        this.canDestroyFarmland = canDestroyFarmland;
        return this;
    }

    public Region setCanDestroyFrames(boolean canDestroyFrames) {
        this.canDestroyFrames = canDestroyFrames;
        return this;
    }

    public Region setCanRotateItemInFrame(boolean canRotateItemInFrame) {
        this.canRotateItemInFrame = canRotateItemInFrame;
        return this;
    }

    public Region setCanDestroyPaintings(boolean canDestroyPaintings) {
        this.canDestroyPaintings = canDestroyPaintings;
        return this;
    }
}
