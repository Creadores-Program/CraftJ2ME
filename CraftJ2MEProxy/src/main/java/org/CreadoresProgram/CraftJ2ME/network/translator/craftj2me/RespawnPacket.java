package org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me;
import com.alibaba.fastjson2.JSONObject;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;

public class RespawnPacket implements CraftJ2MEPacketTranslator{
    @Override
    public void translate(JSONObject pk, Player player){
        org.cloudburstmc.protocol.bedrock.packet.RespawnPacket subpk = new org.cloudburstmc.protocol.bedrock.packet.RespawnPacket();
        subpk.setRuntimeEntityId(player.getRuntimeEntityId());
        subpk.setPosition(player.getLastServerPosition());
        subpk.setState(org.cloudburstmc.protocol.bedrock.packet.RespawnPacket.State.CLIENT_READY);
        player.setVector3f(player.getLastServerPosition());
        player.setRotation(player.getLastServerRotation());
        //actualizar vista
        player.getBedrockClientSession().sendPacket(subpk);
    }
}