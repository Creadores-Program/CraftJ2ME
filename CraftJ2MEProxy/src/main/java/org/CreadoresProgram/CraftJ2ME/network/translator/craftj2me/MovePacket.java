package org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me;
import com.alibaba.fastjson2.JSONObject;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
public class MovePacket implements CraftJ2MEPacketTranslator{
    @Override
    public boolean immediate(){
        return true;
    }
    @Override
    public void translate(JSONObject pk, Player player){
        if(player.getMoveLoop() == null){
            return;
        }
        Vector2f velocityFace = Vector2f.from(pk.getIntValue("pitch"), pk.getIntValue("yaw"));
        Vector3f velocity = Vector3f.from(pk.getIntValue("x"), pk.getIntValue("y"), pk.getIntValue("z"));
        player.getMoveLoop().setVelocityCraftJ2ME(velocity);
        player.getMoveLoop().setVelocityFaceCraftJ2ME(velocityFace);
    }
}