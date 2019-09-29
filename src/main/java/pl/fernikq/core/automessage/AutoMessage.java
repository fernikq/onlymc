package pl.fernikq.core.automessage;

import java.util.List;

public class AutoMessage {

    private List<String> lines;

    public AutoMessage(List<String> lines){
        this.lines = lines;
    }

    public List<String> getLines() {
        return lines;
    }
}
