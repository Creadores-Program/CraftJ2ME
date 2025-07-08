package org.CreadoresProgram.CraftJ2ME.network.translator.bedrock;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import org.cloudburstmc.protocol.bedrock.packet.TickSyncPacket;

public class PlayStatusPacket implements BedrockPacketTranslator {

    @Override
    public boolean immediate() {
        return true;
    }

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket packet = (org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket) pk;

        if (packet.getStatus() == org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket.Status.PLAYER_SPAWN) {
            TickSyncPacket tickSyncPacket = new TickSyncPacket();
            tickSyncPacket.setRequestTimestamp(0);
            tickSyncPacket.setResponseTimestamp(0);
            player.getBedrockClientSession().sendPacketImmediately(tickSyncPacket);

            if (player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.SERVER) {
                player.startSendingPlayerInput();
            }

            SetLocalPlayerAsInitializedPacket setLocalPlayerAsInitializedPacket = new SetLocalPlayerAsInitializedPacket();
            setLocalPlayerAsInitializedPacket.setRuntimeEntityId(player.getRuntimeEntityId());
            player.getBedrockClientSession().sendPacket(setLocalPlayerAsInitializedPacket);

            Vector3f pos = player.getLastServerPosition();
            Vector2f rotation = player.getLastServerRotation();
            player.setVector3f(pos);
            player.setRotation(rotation);
            player.startMoveLoop();
            //generar Vista Render
        }
    }
}