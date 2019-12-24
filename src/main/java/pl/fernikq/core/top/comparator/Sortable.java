package pl.fernikq.core.top.comparator;

import pl.fernikq.core.inventory.InventoryGUI;
import pl.fernikq.core.top.TopKind;
import pl.fernikq.core.top.TopType;

public interface Sortable<T> {

    void sort();
    void addObject(T object);
    void removeObject(T object);
    int getPositionByObject(T object);
    T getObjectByPosition(int position);
    TopType getTopType();
    TopKind getTopKind();
    InventoryGUI getInventory(T object);
}
