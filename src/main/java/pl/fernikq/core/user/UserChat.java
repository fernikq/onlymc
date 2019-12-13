package pl.fernikq.core.user;

import java.util.HashSet;
import java.util.Set;

public class UserChat {

    private boolean dropMessages;
    private boolean premiumCaseMessages;

    private Set<User> blockedTpa;
    private Set<User> blockedMessage;

    public UserChat(User user){
        this.dropMessages = true;
        this.premiumCaseMessages = true;
        this.blockedTpa = new HashSet<>();
        this.blockedMessage = new HashSet<>();
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

    public Set<User> getBlockedTpa() {
        return blockedTpa;
    }

    public Set<User> getBlockedMessage() {
        return blockedMessage;
    }
}
