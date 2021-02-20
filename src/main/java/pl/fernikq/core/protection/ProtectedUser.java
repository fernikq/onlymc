package pl.fernikq.core.protection;

import java.util.UUID;

public class ProtectedUser {

    private UUID uuid;
    private int seconds;

    public ProtectedUser(UUID uuid, int seconds) {
        this.uuid = uuid;
        this.seconds = seconds;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
