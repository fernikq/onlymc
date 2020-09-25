package pl.fernikq.core.magiccase;

public class MagicCase {

    private MagicCaseType type;

    public MagicCase(MagicCaseType type) {
        this.type = type;
    }

    public MagicCaseType getType() {
        return type;
    }

    public void setType(MagicCaseType type) {
        this.type = type;
    }
}
