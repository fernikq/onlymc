package pl.fernikq.core.command.simpleCommand;

import pl.fernikq.core.user.UserGroup;

import java.util.List;

public class SimpleCommand {

    private String name;
    private List<String> aliases;
    private UserGroup userGroup;
    private List<String> messages;

    public SimpleCommand(String name, List<String> aliases, UserGroup userGroup, List<String> messages) {
        this.name = name;
        this.aliases = aliases;
        this.userGroup = userGroup;
        this.messages = messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public List<String> getMessages() {
        return messages;
    }
}
