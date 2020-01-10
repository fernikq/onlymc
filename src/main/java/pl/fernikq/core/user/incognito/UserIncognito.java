package pl.fernikq.core.user.incognito;

import com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import pl.fernikq.core.user.User;

public class UserIncognito {

    private User user;
    private IncognitoType showGuildTag;
    private IncognitoType showNickName;
    private IncognitoType showRank;
    private IncognitoType showPoints;
    private boolean hideOriginalSkin;
    private String originalSkin;

    public UserIncognito(User user){
        this.user = user;
        this.showGuildTag = IncognitoType.ALL;
        this.showNickName = IncognitoType.ALL;
        this.showRank = IncognitoType.ALL;
        this.showPoints = IncognitoType.ALL;
        this.hideOriginalSkin = false;
        this.originalSkin = null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public IncognitoType getShowGuildTag() {
        return showGuildTag;
    }

    public void setShowGuildTag(IncognitoType hideGuildTag) {
        this.showGuildTag = hideGuildTag;
    }

    public IncognitoType getShowNickName() {
        return showNickName;
    }

    public void setShowNickName(IncognitoType hideNickName) {
        this.showNickName = hideNickName;
    }

    public boolean isHideOriginalSkin() {
        return hideOriginalSkin;
    }

    public void setHideOriginalSkin(boolean hideOriginalSkin) {
        this.hideOriginalSkin = hideOriginalSkin;
    }

    public boolean hasSkin(){
        GameProfile gameProfile = ((CraftPlayer)this.user.asPlayer()).getProfile();
        return !gameProfile.getProperties().get("textures").isEmpty();
    }

    public String getOriginalSkin() {
        return originalSkin;
    }

    public void setOriginalSkin(String originalSkin) {
        this.originalSkin = originalSkin;
    }

    public IncognitoType getShowRank() {
        return showRank;
    }

    public void setShowRank(IncognitoType showRank) {
        this.showRank = showRank;
    }

    public IncognitoType getShowPoints() {
        return showPoints;
    }

    public void setShowPoints(IncognitoType showPoints) {
        this.showPoints = showPoints;
    }
}
