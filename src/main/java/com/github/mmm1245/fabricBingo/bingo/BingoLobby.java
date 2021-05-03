package com.github.mmm1245.fabricBingo.bingo;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.plasmid.game.*;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateChunkGenerator;

import java.util.Random;

public class BingoLobby {
    public static GameOpenProcedure open(GameOpenContext<BingoConfig> context) {
        // get our config that got loaded by Plasmid
        BingoConfig config = context.getConfig();

        // create a very simple map with a stone block at (0; 64; 0)
        MapTemplate template = MapTemplate.createEmpty();
        template.setBlockState(new BlockPos(0, 64, 0), Blocks.STONE.getDefaultState());

        // create a chunk generator that will generate from this template that we just created
        //TemplateChunkGenerator generator = new TemplateChunkGenerator(context.getServer(), template);

        NoiseChunkGenerator generator = GeneratorOptions.createOverworldGenerator(context.getServer().getRegistryManager().get(Registry.BIOME_KEY), context.getServer().getRegistryManager().get(Registry.NOISE_SETTINGS_WORLDGEN), Util.random.nextLong());

        // set up how the world that this minigame will take place in should be constructed
        BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                .setGenerator(generator)
                .setDefaultGameMode(GameMode.SPECTATOR);
                //.setSpawnAt(new Vec3d(0.0, 65.0, 0.0));

        return context.createOpenProcedure(worldConfig, logic -> {
            logic.setRule(GameRule.PVP, RuleResult.DENY);
            logic.setRule(GameRule.INTERACTION, RuleResult.DENY);
            logic.setRule(GameRule.BREAK_BLOCKS, RuleResult.DENY);
            logic.setRule(GameRule.HUNGER, RuleResult.DENY);
            logic.setRule(GameRule.PLACE_BLOCKS, RuleResult.DENY);
            logic.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
            logic.setRule(GameRule.PORTALS, RuleResult.DENY);


            GameSpace gameSpace = logic.getSpace();
            //gameSpace.getWorld().getWorldBorder().setSize(200);
            logic.on(PlayerAddListener.EVENT, player -> {
                Text message = new LiteralText(config.greeting);
                gameSpace.getPlayers().sendMessage(message);
                player.teleport(0, Util.highestPoint(gameSpace.getWorld(), 0, 0)+10, 0);
                //player.networkHandler.connection.send(new WorldBorderS2CPacket(gameSpace.getWorld().getWorldBorder(), WorldBorderS2CPacket.Type.SET_SIZE));
            });
            logic.on(RequestStartListener.EVENT, () -> {
                BingoGame.open(gameSpace, config);
                return StartResult.OK;
            });
            logic.on(PlayerDeathListener.EVENT, (player, source) -> {
                player.inventory.dropAll();
                player.sendMessage(new LiteralText("Bingo>death location x: " + player.getX() + " y: " + player.getY()), false);
                int x = (int) (player.getX() + Util.random.nextInt(200)-100);
                int z = (int) (player.getZ() + Util.random.nextInt(200)-100);

                player.teleport(x, Util.highestPoint(gameSpace.getWorld(), x, z), z);
                Util.resetPlayer(player, GameMode.SURVIVAL);
                return ActionResult.PASS;
            });
        });
    }


}
