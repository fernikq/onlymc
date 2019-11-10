package pl.fernikq.core.user;

public class UserChat {

    private boolean dropMessages;
    private boolean premiumCaseMessages;

    public UserChat(User user){
        this.dropMessages = true;
        this.premiumCaseMessages = true;
        user.setUserChat(this);
    }

    public boolean isDropMessages() {
        return dropMessages;
    }

    public void setDropMessages(boolean dropMessages) {
        this.dropMessages = dropMessages;
    }

    public boolean isPremiumCaseMessages() {
        return premiumCaseMessages;
    }

    public void setPremiumCaseMessages(boolean premiumCaseMessages) {
        this.premiumCaseMessages = premiumCaseMessages;
    }
}
