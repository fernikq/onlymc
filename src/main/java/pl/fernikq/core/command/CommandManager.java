package pl.fernikq.core.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import pl.fernikq.core.util.Reflection;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private CommandMap commandMap;
    private Reflection.FieldAccessor<SimpleCommandMap> fieldAccessor;
    public Map<String, CustomCommand> commands;


    public CommandManager(){
        commands = new HashMap<String, CustomCommand>();
        fieldAccessor = Reflection.getField(SimplePluginManager.class, "commandMap", SimpleCommandMap.class);
        commandMap = fieldAccessor.get(Bukkit.getServer().getPluginManager());
    }

    public void register(CustomCommand command) {
        if(commandMap == null) {
            commandMap = fieldAccessor.get(Bukkit.getServer().getPluginManager());
        }
        if(commands.containsKey(command.getName())){
            return;
        }
        commandMap.register(command.getName(), command.getCommand());
        commands.put(command.getName(), command);
    }
}
