package com.github.mmm1245.fabricBingo.bingo;

import net.minecraft.block.Material;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.util.BlockBounds;

import java.util.Random;

public class Util {
    public static Random random = new Random();
    public static void resetPlayer(ServerPlayerEntity player, GameMode gameMode){
        player.setGameMode(gameMode);
        player.setVelocity(Vec3d.ZERO);
        player.fallDistance = 0.0f;
        player.getHungerManager().add(20, 2.0f);
        player.setHealth(20.0f);
    }
    public static int highestPoint(ServerWorld world, int x, int z){
        for(int i = 255;i >= 0;i--){
            Material mat = world.getBlockState(new BlockPos(x, i, z)).getMaterial();
            if(mat.isSolid() || mat.isLiquid()){
                return i+1;
            }
        }
        return 0;
    }
}
