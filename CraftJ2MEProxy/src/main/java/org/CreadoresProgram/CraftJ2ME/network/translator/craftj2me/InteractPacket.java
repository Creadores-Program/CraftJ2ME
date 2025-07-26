package org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me;
import com.alibaba.fastjson2.JSONObject;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;
import org.CreadoresProgram.CraftJ2ME.server.Server;

import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
public class InteractPacket implements CraftJ2MEPacketTranslator{
    @Override
    public void translate(JSONObject pk, Player player){
        Vector3f playerpos = player.getVector3f();
        Vector2f playervista = player.getRotation();
        float x = playerpos.getX();
        float y = playerpos.getY();
        float z = playerpos.getZ();
        float yaw = playervista.getY();
        if(yaw >= 45 && yaw < 135){
            x += 1;
        }else if(yaw >= 135 && yaw < 225){
            z += 1;
        }else if(yaw >= 225 && yaw < 315){
            x -= 1;
        }else{
            z -= 1;
        }
        if(player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.CLIENT){
            org.cloudburstmc.protocol.bedrock.packet.InteractPacket subpk = new org.cloudburstmc.protocol.bedrock.packet.InteractPacket();
            subpk.setRuntimeEntityId(player.getRuntimeEntityId());
            subpk.setAction(org.cloudburstmc.protocol.bedrock.packet.InteractPacket.Action.INTERACT);
            subpk.setMousePosition(Vector3f.from(x, y, z));
            player.getBedrockClientSession().sendPacket(subpk);
        }else{
            //proximamente
        }
    }
}