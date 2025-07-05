package org.CreadoresProgram.CraftJ2ME.network.translator.interfaces;

import org.CreadoresProgram.CraftJ2ME.player.Player;
import com.alibaba.fastjson2.JSONObject;

public interface CraftJ2MEPacketTranslator {

    default boolean immediate() {
        return false;
    }

    void translate(JSONObject pk, Player player);
}