package com.github.mmm1245.fabricBingo.bingo;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.plasmid.game.GameCloseReason;

public class BingoPlayer {
    ServerPlayerEntity player;
    boolean[] checked;
    BingoItem[] items;
    public BingoPlayer(ServerPlayerEntity player, BingoItem[] items){
        if(items.length != 9)  throw new IllegalArgumentException("items length must be 9");
        this.player = player;
        this.checked = new boolean[9];
        for(int i = 0;i < 9;i++){
            checked[i] = false;
        }
        this.items = items;
    }
    public ItemStack getMap(){
        return MapBuilder.create(player.getServerWorld(), MapBuilder.createGrid(items, checked));
    }
    public void check(int index){
        if(index < 0 || index >= 9) throw new IllegalArgumentException("index cant not be <0 and >9");
        checked[index] = true;
        ItemStack map = getMap();
        player.inventory.offHand.set(0, map);
    }
    public boolean collect(BingoGame bg, ItemStack is){
        for(int i = 0;i < 9;i++){
            if(is.getItem().equals(items[i].getItem()) && (!checked[i])){
                check(i);
                if(checkWin()){
                    bg.space.getPlayers().sendTitle(new LiteralText(this.player.getName().asString() + " won."));
                    bg.end = true;
                    bg.space.close(GameCloseReason.FINISHED);
                }
                return true;
            }
        }
        return false;
    }
    public boolean checkWin(){
        if(checked[0] && checked[4] && checked[8])
            return true;
        if(checked[2] && checked[4] && checked[6])
            return true;
        boolean ok = false;
        for(int i = 0;i < 3;i++){
            if((checked[i] && checked[i+3] && checked[i+6]))
                ok=true;
        }
        for(int i = 0;i < 3;i++){
            if((checked[i*3] && checked[i*3+1] && checked[i*3+2]))
                ok=true;
        }
        /*for(int i = 0;i < 9;i++){
            if(!checked[i]) return false;
        }*/
        return ok;
    }
}
