package org.CreadoresProgram.CraftJ2ME.utils.geyser;

import org.CreadoresProgram.CraftJ2ME.utils.nukkit.BitArray;
import org.CreadoresProgram.CraftJ2ME.utils.nukkit.BitArrayVersion;

public class SingletonBitArray implements BitArray {

    private static final int[] EMPTY_ARRAY = new int[0];

    public SingletonBitArray() {
    }

    @Override
    public void set(int index, int value) {
    }

    @Override
    public int get(int index) {
        return 0;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int[] getWords() {
        return EMPTY_ARRAY;
    }

    @Override
    public BitArrayVersion getVersion() {
        return BitArrayVersion.V0;
    }

    @Override
    public SingletonBitArray copy() {
        return new SingletonBitArray();
    }
}