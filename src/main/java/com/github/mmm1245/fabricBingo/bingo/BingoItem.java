package com.github.mmm1245.fabricBingo.bingo;

import net.minecraft.item.Item;

public class BingoItem {
    Item item;
    String texture;
    public BingoItem(Item item, String texture) {
        this.item = item;
        this.texture = texture;
    }
    public Item getItem() {
        return item;
    }

    public String getTexture() {
        return texture;
    }
}
