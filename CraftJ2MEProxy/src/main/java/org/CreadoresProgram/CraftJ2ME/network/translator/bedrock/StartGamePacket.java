package org.CreadoresProgram.CraftJ2ME.network.translator.bedrock;
import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;

import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestChunkRadiusPacket;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
public class StartGamePacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.StartGamePacket packet = (org.cloudburstmc.protocol.bedrock.packet.StartGamePacket) pk;
        player.setRuntimeEntityId(packet.getRuntimeEntityId());
        player.setOldPosition(packet.getPlayerPosition());
        player.setVector3f(packet.getPlayerPosition());
        player.setLastServerPosition(packet.getPlayerPosition());
        player.setLastServerRotation(packet.getRotation());
        player.setStartGamePacketCache(packet);
        //player.setGameMode(packet.getPlayerGameType());
        SimpleDefinitionRegistry<ItemDefinition> itemDefinitions = SimpleDefinitionRegistry.<ItemDefinition>builder()
            .addAll(packet.getItemDefinitions())
            .add(new SimpleItemDefinition("minecraft:empty", 0, false))
            .build();
        player.getBedrockClientSession().getPeer().getCodecHelper().setItemDefinitions(itemDefinitions);
        if (!packet.isBlockNetworkIdsHashed()) {
            player.getBedrockClientSession().getPeer().getCodecHelper().setBlockDefinitions(Server.getInstance().getBlockDefinitions());
        }
        if(player.getRenderMCJ2ME() != null){
            player.getRenderMCJ2ME().setDimention(packet.getDimensionId());
        }
        player.setDimention(packet.getDimensionId());
        RequestChunkRadiusPacket subpk = new RequestChunkRadiusPacket();
        subpk.setRadius(5);
        subpk.setMaxRadius(7);
        player.getBedrockClientSession().sendPacketImmediately(subpk);
    }
    @Override
    public boolean immediate() {
        return true;
    }
}