package com.github.mmm1245.fabricBingo.bingo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BingoConfig {
    public final String greeting;

    public static final Codec<BingoConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.STRING.fieldOf("greeting").forGetter(config -> config.greeting)
        ).apply(instance, BingoConfig::new);
    });

    public BingoConfig(String greeting) {
        this.greeting = greeting;
    }
}
