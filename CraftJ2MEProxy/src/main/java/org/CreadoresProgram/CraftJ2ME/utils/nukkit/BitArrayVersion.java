package org.CreadoresProgram.CraftJ2ME.utils.nukkit;

import org.CreadoresProgram.CraftJ2ME.utils.geyser.SingletonBitArray;

public enum BitArrayVersion {
    V16(16, 2, null),
    V8(8, 4, V16),
    V6(6, 5, V8), // 2 bit padding
    V5(5, 6, V6), // 2 bit padding
    V4(4, 8, V5),
    V3(3, 10, V4), // 2 bit padding
    V2(2, 16, V3),
    V1(1, 32, V2),
    V0(0, 0, V1);

    public final byte bits; //TODO: probably make this private again just because
    public final byte entriesPerWord; //TODO: probably make this private again just because
    public final int maxEntryValue; //TODO: probably make this private again just because
    final BitArrayVersion next;

    BitArrayVersion(int bits, int entriesPerWord, BitArrayVersion next) {
        this.bits = (byte) bits;
        this.entriesPerWord = (byte) entriesPerWord;
        this.maxEntryValue = (1 << this.bits) - 1;
        this.next = next;
    }

    public static BitArrayVersion get(int version, boolean read) {
        for (BitArrayVersion ver : values()) {
            if ((!read && ver.entriesPerWord <= version) || (read && ver.bits == version)) {
                return ver;
            }
        }
        throw new IllegalArgumentException("Invalid palette version: " + version);
    }

    public BitArray createPalette(int size) {
        return this.createPalette(size, new int[this.getWordsForSize(size)]);
    }

    public byte getId() {
        return bits;
    }

    public int getWordsForSize(int size) {
        return (size / entriesPerWord) + (size % entriesPerWord == 0 ? 0 : 1);
    }

    public int getMaxEntryValue() {
        return maxEntryValue;
    }

    public BitArrayVersion next() {
        return next;
    }

    public BitArray createPalette(int size, int[] words) {
        if (this == V3 || this == V5 || this == V6) {
            // Padded palettes aren't able to use bitwise operations due to their padding.
            return new PaddedBitArray(this, size, words);
        } else if (this == V0) {
            return new SingletonBitArray();
        } else {
            return new Pow2BitArray(this, size, words);
        }
    }
}