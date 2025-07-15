package org.CreadoresProgram.CraftJ2ME.network.translator.bedrock;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.utils.Utils;
import org.CreadoresProgram.CraftJ2ME.utils.Logger;
import org.CreadoresProgram.CraftJ2ME.server.Server;
import org.CreadoresProgram.CraftJ2ME.renderVista.RenderMCJ2ME;
import org.CreadoresProgram.CraftJ2ME.utils.nukkit.BitArray;
import org.CreadoresProgram.CraftJ2ME.utils.nukkit.BitArrayVersion;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.util.stream.NetworkDataInputStream;
import org.cloudburstmc.protocol.common.util.VarInts;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
public class LevelChunkPacket implements BedrockPacketTranslator{
    private static final byte[] FULL_LIGHT = new byte[2048];
    static {
        Arrays.fill(FULL_LIGHT, (byte) 0xff);
    }
    @Override
    public void translate(BedrockPacket pk, Player player){
        if(player.getRenderMCJ2ME() == null){
            player.setRenderMCJ2ME(new RenderMCJ2ME(player));
        }
        org.cloudburstmc.protocol.bedrock.packet.LevelChunkPacket packet = (org.cloudburstmc.protocol.bedrock.packet.LevelChunkPacket) pk;
        int subChunksLength = packet.getSubChunksLength();
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(packet.getData());
        ByteBuf chunkByteBuf = Unpooled.buffer();
        for (int sectionIndex = 0; sectionIndex < subChunksLength; sectionIndex++) {
            int chunkVersion = byteBuf.readByte();
            if (chunkVersion != 1 && chunkVersion != 8 && chunkVersion != 9) {
                continue;
            }
            if (chunkVersion == 1) {
                networkDecodeVersionOne(byteBuf);
                continue;
            }
            if (chunkVersion == 9) {
                networkDecodeVersionNine(byteBuf, sectionIndex);
                continue;
            }
            networkDecodeVersionEight(byteBuf, sectionIndex, byteBuf.readByte());
        }
        byteBuf.release();
        chunkByteBuf.release();
    }
    @Override
    public boolean immediate() {
        return true;
    }
    public void networkDecodeVersionNine(ByteBuf byteBuf, int sectionIndex) {
        byte storageSize = byteBuf.readByte();
        byteBuf.readByte(); // height
        networkDecodeVersionEight(byteBuf, sectionIndex, storageSize);
    }
    public void networkDecodeVersionOne(ByteBuf byteBuf) {
        networkDecodeVersionEight(byteBuf, 0, (byte) 1);
    }
    public void networkDecodeVersionEight(ByteBuf byteBuf, int sectionIndex, byte storageSize) {
        for (int storageReadIndex = 0; storageReadIndex < storageSize; storageReadIndex++) {
            if (storageReadIndex > 1) {
                return;
            }
            byte paletteHeader = byteBuf.readByte();
            boolean isRuntime = (paletteHeader & 1) == 1;
            int paletteVersion = (paletteHeader | 1) >> 1;

            BitArrayVersion bitArrayVersion = BitArrayVersion.get(paletteVersion, true);

            int maxBlocksInSection = 4096;
            BitArray bitArray = bitArrayVersion.createPalette(maxBlocksInSection);
            int wordsSize = bitArrayVersion.getWordsForSize(maxBlocksInSection);

            for (int wordIterationIndex = 0; wordIterationIndex < wordsSize; wordIterationIndex++) {
                int word = byteBuf.readIntLE();
                bitArray.getWords()[wordIterationIndex] = word;
            }

            int paletteSize = VarInts.readInt(byteBuf);
            int[] sectionPalette = new int[paletteSize];
            NBTInputStream nbtStream = isRuntime ? null : new NBTInputStream(new NetworkDataInputStream(new ByteBufInputStream(byteBuf)));
            for (int i = 0; i < paletteSize; i++) {
                if (isRuntime) {
                    sectionPalette[i] = VarInts.readInt(byteBuf);
                } else {
                    try {
                        NbtMapBuilder map = ((NbtMap) nbtStream.readTag()).toBuilder();
                        map.replace("name", "minecraft:" + map.get("name").toString());
                        Logger logger = Server.getInstance().getLogger();
                        logger.info(map.build().toString());
                        //sectionPalette[i] = BlockPaletteTranslator.getBedrockBlockId(BlockPaletteTranslator.bedrockStateFromNBTMap(map.build()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            int index = 0;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int paletteIndex = bitArray.get(index);
                        int mcbeBlockId = sectionPalette[paletteIndex];

                        if (storageReadIndex == 0) {
                            player.getRenderMCJ2ME().setBlock(x, y, z, mcbeBlockId);
                            //chunkSections[sectionIndex].setBlock(x, y, z, javaStateId);
                        } else {
                            player.getRenderMCJ2ME().setBlock(x, y, z, mcbeBlockId);
                            /*if (javaStateId == 34 || javaStateId == 35) { // water
                                int layer0 = chunkSections[sectionIndex].getBlock(x, y, z);
                                if (layer0 != 0) {
                                    int waterlogged = BlockConverter.javaBlockToWaterlogged(layer0);
                                    if (waterlogged != 1) {
                                        chunkSections[sectionIndex].setBlock(x, y, z, waterlogged);
                                    }
                                } else {
                                    chunkSections[sectionIndex].setBlock(x, y, z, javaStateId);
                                }
                            }*/
                        }

                        index++;
                    }
                }
            }
        }
    }
}
