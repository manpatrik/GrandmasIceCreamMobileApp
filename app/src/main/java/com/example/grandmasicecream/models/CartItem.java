package com.example.grandmasicecream.models;

import java.util.List;

public class CartItem {
    IceCream iceCream;
    List<Long> extraItemIds;

    public CartItem(IceCream iceCream, List<Long> extraItemIds) {
        this.iceCream = iceCream;
        this.extraItemIds = extraItemIds;
    }

    public void addExtraItemIds(Long itemId){
        if (!extraItemIds.contains(itemId)) {
            extraItemIds.add(itemId);
        }
    }

    public void removeExtraItemId(Long extraItemId){
        extraItemIds.remove(extraItemId);
    }

    public IceCream getIceCream() {
        return iceCream;
    }

    public List<Long> getExtraItemIds() {
        return extraItemIds;
    }

    public void addOrRemoveExtraId(Long id, boolean isChecked) {
        if ( isChecked ) {
            addExtraItemIds(id);
        } else {
            removeExtraItemId(id);
        }
    }
}
