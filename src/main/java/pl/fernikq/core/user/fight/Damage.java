package pl.fernikq.core.user.fight;

import pl.fernikq.core.user.User;

public class Damage {

    private User user;
    private double damage;

    public Damage(User user, double damage){
        this.user = user;
        this.damage = damage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
