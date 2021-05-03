package com.github.mmm1245.fabricBingo.bingo;

import com.google.common.net.HostAndPort;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.HandSwingListener;
import xyz.nucleoid.plasmid.game.event.PlayerRemoveListener;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class BingoGame {
    HashMap<ServerPlayerEntity,BingoPlayer> players;
    GameSpace space;
    boolean end;
    public BingoGame(HashMap<ServerPlayerEntity,BingoPlayer> players, GameSpace space){
        this.players = players;
        this.space = space;
        this.end = false;
    }
    public static void open(GameSpace gameSpace, BingoConfig config) {
        gameSpace.openGame(game -> {
            boolean[] checked = new boolean[9];
            for(int i = 0;i < checked.length;i++){
                checked[i] = i%2==0 ? false : true;
            }
            HashMap<ServerPlayerEntity,BingoPlayer> playersFill = new HashMap<>();
            ArrayList<BingoItem> items = new ArrayList<>();
            items.add(new BingoItem(Items.REDSTONE, "redstone"));
            items.add(new BingoItem(Items.ARMOR_STAND, "armor_stand"));
            items.add(new BingoItem(Items.BRICK, "brick"));
            items.add(new BingoItem(Items.CLOCK, "clock_00"));
            items.add(new BingoItem(Items.BONE_MEAL, "bone_meal"));
            items.add(new BingoItem(Items.GOLDEN_HOE, "golden_hoe"));
            items.add(new BingoItem(Items.COMPASS, "compass_16"));
            items.add(new BingoItem(Items.MINECART, "minecart"));
            items.add(new BingoItem(Items.CHAIN, "chain"));
            items.add(new BingoItem(Items.INK_SAC, "ink_sac"));
            items.add(new BingoItem(Items.WHEAT, "wheat"));
            items.add(new BingoItem(Items.ITEM_FRAME, "item_frame"));
            items.add(new BingoItem(Items.FLINT, "flint"));
            items.add(new BingoItem(Items.DIAMOND_BOOTS, "diamond_boots"));
            items.add(new BingoItem(Items.CROSSBOW, "crossbow_standby"));
            items.add(new BingoItem(Items.CAMPFIRE, "campfire"));
            items.add(new BingoItem(Items.SHEARS, "shears"));
            items.add(new BingoItem(Items.LANTERN, "lantern"));
            items.add(new BingoItem(Items.GOLDEN_APPLE, "golden_apple"));
            items.add(new BingoItem(Items.HOPPER, "hopper"));
            items.add(new BingoItem(Items.REPEATER, "repeater"));
            items.add(new BingoItem(Items.LAPIS_LAZULI, "lapis_lazuli"));

            Collections.shuffle(items);

            BingoItem[] itemsFinal = new BingoItem[9];
            for(int i = 0;i < 9;i++){
                itemsFinal[i] = items.get(i);
            }

            gameSpace.getPlayers().forEach(serverPlayerEntity -> {
                Util.resetPlayer(serverPlayerEntity, GameMode.SURVIVAL);
                BingoPlayer bingoPlayer = new BingoPlayer(serverPlayerEntity, itemsFinal);
                serverPlayerEntity.inventory.offHand.set(0, bingoPlayer.getMap());
                playersFill.put(serverPlayerEntity, bingoPlayer);
                serverPlayerEntity.teleport(serverPlayerEntity.getX(), Util.highestPoint(serverPlayerEntity.getServerWorld(), (int) serverPlayerEntity.getX(), (int) serverPlayerEntity.getZ()), serverPlayerEntity.getZ());
            });
            BingoGame bg = new BingoGame(playersFill, game.getSpace());

            gameSpace.getPlayers().sendMessage(new LiteralText("Game started"));

            game.setRule(GameRule.CRAFTING, RuleResult.ALLOW);
            game.setRule(GameRule.PORTALS, RuleResult.DENY);
            game.setRule(GameRule.PVP, RuleResult.DENY);
            game.setRule(GameRule.HUNGER, RuleResult.ALLOW);
            game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
            game.setRule(GameRule.INTERACTION, RuleResult.ALLOW);
            game.setRule(GameRule.BLOCK_DROPS, RuleResult.ALLOW);
            game.setRule(GameRule.THROW_ITEMS, RuleResult.ALLOW);
            game.setRule(GameRule.UNSTABLE_TNT, RuleResult.DENY);
            game.setRule(GameRule.PLACE_BLOCKS, RuleResult.ALLOW);
            game.setRule(GameRule.BREAK_BLOCKS, RuleResult.ALLOW);
            game.setRule(GameRule.BLOCK_DROPS, RuleResult.ALLOW);
 

            /*game.on(GameCloseListener.EVENT, active::onClose);

            game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
            game.on(PlayerAddListener.EVENT, active::addPlayer);
            game.on(PlayerRemoveListener.EVENT, active::removePlayer);

            game.on(GameTickListener.EVENT, active::tick);

            game.on(PlayerDamageListener.EVENT, active::onPlayerDamage);
            game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
            game.on(PlaceBlockListener.EVENT, active::onPlaceBlock);*/
            game.on(HandSwingListener.EVENT,(player, hand) -> {
                ItemStack is = hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack();
                if(bg.players.containsKey(player) && bg.players.get(player).collect(bg, is)){
                    gameSpace.getPlayers().sendMessage(new LiteralText("<Bingo>" + player.getName().asString() + " collected " + is.getItem().toString()));
                }
            });
            game.on(PlayerRemoveListener.EVENT, player -> {
                if(gameSpace.getPlayers().size() <= 1) {
                    if(!bg.end) {
                        gameSpace.getPlayers().sendTitle(new LiteralText(player.getName().asString() + " won, because he was last one standing."));
                    }
                    gameSpace.close(GameCloseReason.FINISHED);
                }
            });
        });
    }
}
