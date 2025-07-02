package org.CreadoresProgram.CraftJ2ME.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.common.DefinitionRegistry;

import java.util.List;

public class NbtBlockDefinitionRegistry implements DefinitionRegistry<BlockDefinition> {

    private final Int2ObjectMap<NbtBlockDefinition> definitions = new Int2ObjectOpenHashMap<>();

    public NbtBlockDefinitionRegistry(List<NbtMap> definitions) {
        int counter = 0;
        for (NbtMap definition : definitions) {
            int runtimeId = counter++;
            this.definitions.put(runtimeId, new NbtBlockDefinition(runtimeId, definition));
        }
    }

    @Override
    public BlockDefinition getDefinition(int runtimeId) {
        return definitions.get(runtimeId);
    }

    @Override
    public boolean isRegistered(BlockDefinition definition) {
        return definitions.get(definition.getRuntimeId()) == definition;
    }

    private static class NbtBlockDefinition implements BlockDefinition {
        @Getter
        private final int runtimeId;
        @Getter
        private final NbtMap definition;

        public NbtBlockDefinition(int runtimeId, NbtMap definition) {
            this.runtimeId = runtimeId;
            this.definition = definition;
        }
    }
}