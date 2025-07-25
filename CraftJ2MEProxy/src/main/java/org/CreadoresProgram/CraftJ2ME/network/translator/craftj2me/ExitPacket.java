package org.CreadoresProgram.CraftJ2ME.network.translator.craftj2me;
import com.alibaba.fastjson2.JSONObject;

import org.CreadoresProgram.CraftJ2ME.network.translator.interfaces.CraftJ2MEPacketTranslator;
import org.CreadoresProgram.CraftJ2ME.player.Player;

public class ExitPacket implements CraftJ2MEPacketTranslator{
    @Override
    public boolean immediate(){
        return true;
    }
    @Override
    public void translate(JSONObject pk, Player player){
        player.disconnect("logged out");
    }
}