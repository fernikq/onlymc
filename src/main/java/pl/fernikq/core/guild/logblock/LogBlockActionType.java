package pl.fernikq.core.guild.logblock;

import io.vavr.collection.Stream;
import io.vavr.control.Option;
import pl.fernikq.core.guild.member.GuildPermission;

public enum LogBlockActionType {

    DESTROY_BLOCK,
    PLACE_BLOCK;

    public static Option<LogBlockActionType> getLogBlockActionTypeByName(String name){
        return Stream.of(values()).find(logBlockActionType -> logBlockActionType.name().equalsIgnoreCase(name));
    }
}
