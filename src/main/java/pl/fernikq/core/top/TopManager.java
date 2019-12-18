package pl.fernikq.core.top;

import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.top.comparator.CoinsComparator;
import pl.fernikq.core.top.comparator.Sortable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TopManager {

    private final CorePlugin plugin;
    private List<Sortable> tops;

    public TopManager(CorePlugin plugin){
        this.plugin = plugin;
        this.tops = Arrays.asList(new CoinsComparator(this.plugin, TopType.USER_COINS, TopKind.USER));
    }

    public Sortable getTopByType(TopType topType){
        return this.tops.stream().filter(sortable -> sortable.getTopType().equals(topType)).findAny().get();
    }

    public List<Sortable> getTopsByKind(TopKind topKind){
        return this.tops.stream().filter(sortable -> sortable.getTopKind().equals(topKind)).collect(Collectors.toList());
    }
}
