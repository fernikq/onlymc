package pl.fernikq.core.magiccase;

import io.vavr.collection.HashSet;

public enum MagicCaseType {

    NORMAL("&bStandardowa"),
    PREMIUM("&5Wyjatkowa");

    private final String name;

    private MagicCaseType(String name){
        this.name = name;
    }

    public static MagicCaseType getMagicCaseTypeByName(String name){
        return HashSet.of(values()).find(magicCaseType -> magicCaseType.name().equalsIgnoreCase(name)).getOrNull();
    }

    public String getName() {
        return name;
    }
}
