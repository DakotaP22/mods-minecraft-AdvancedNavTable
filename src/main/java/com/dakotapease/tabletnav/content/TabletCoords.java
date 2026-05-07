package com.dakotapease.tabletnav.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TabletCoords(int x, int z) {

    public static final Codec<TabletCoords> CODEC =
        RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("x").forGetter(TabletCoords::x),
            Codec.INT.fieldOf("z").forGetter(TabletCoords::z)
        ).apply(i, TabletCoords::new));

    public static final StreamCodec<ByteBuf, TabletCoords> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT, TabletCoords::x,
            ByteBufCodecs.INT, TabletCoords::z,
            TabletCoords::new);
}
