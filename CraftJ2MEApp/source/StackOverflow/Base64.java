package StackOverflow;

// http://stackoverflow.com/questions/9240434/how-to-convert-byte-to-base64-and-back-in-j2me
public class Base64 {
    static byte[] encodeData;
    static String charSet = 
  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    static {
        encodeData = new byte[64];
    for (int i = 0; i<64; i++) {
        byte c = (byte) charSet.charAt(i);
        encodeData[i] = c;
    }
    }

    private Base64() {}

    /**
     * base-64 encode a string
     * @param s     The ascii string to encode
     * @returns     The base64 encoded result
     */

    public static String
    encode(String s) {
        return encode(s.getBytes());
    }

    /**
     * base-64 encode a byte array
     * @param src   The byte array to encode
     * @returns     The base64 encoded result
     */

    public static String
    encode(byte[] src) {
    return encode(src, 0, src.length);
    }

    /**
     * base-64 encode a byte array
     * @param src   The byte array to encode
     * @param start The starting index
     * @param len   The number of bytes
     * @returns     The base64 encoded result
     */

    public static String
    encode(byte[] src, int start, int length) {
        byte[] dst = new byte[(length+2)/3 * 4 + length/72];
        int x = 0;
        int dstIndex = 0;
        int state = 0;  // which char in pattern
        int old = 0;    // previous byte
        int len = 0;    // length decoded so far
    int max = length + start;
        for (int srcIndex = start; srcIndex<max; srcIndex++) {
        x = src[srcIndex];
        switch (++state) {
        case 1:
            dst[dstIndex++] = encodeData[(x>>2) & 0x3f];
        break;
        case 2:
            dst[dstIndex++] = encodeData[((old<<4)&0x30) 
                | ((x>>4)&0xf)];
        break;
        case 3:
            dst[dstIndex++] = encodeData[((old<<2)&0x3C) 
                | ((x>>6)&0x3)];
        dst[dstIndex++] = encodeData[x&0x3F];
        state = 0;
        break;
        }
        old = x;
        if (++len >= 72) {
            dst[dstIndex++] = (byte) '\n';
            len = 0;
        }
    }

    /*
     * now clean up the end bytes
     */

    switch (state) {
    case 1: dst[dstIndex++] = encodeData[(old<<4) & 0x30];
       dst[dstIndex++] = (byte) '=';
       dst[dstIndex++] = (byte) '=';
       break;
    case 2: dst[dstIndex++] = encodeData[(old<<2) & 0x3c];
       dst[dstIndex++] = (byte) '=';
       break;
    }
    return new String(dst);
    }

    /**
     * A Base64 decoder.  This implementation is slow, and 
     * doesn't handle wrapped lines.
     * The output is undefined if there are errors in the input.
     * @param s     a Base64 encoded string
     * @returns     The byte array eith the decoded result
     */

    public static byte[]
    decode(String s) {
      int end = 0;  // end state
      if (s.endsWith("=")) {
      end++;
      }
      if (s.endsWith("==")) {
      end++;
      }
      int len = (s.length() + 3)/4 * 3 - end;
      byte[] result = new byte[len];
      int dst = 0;
      try {
      for(int src = 0; src< s.length(); src++) {
          int code =  charSet.indexOf(s.charAt(src));
          if (code == -1) {
              break;
          }
          switch (src%4) {
          case 0:
              result[dst] = (byte) (code<<2);
              break;
          case 1: 
              result[dst++] |= (byte) ((code>>4) & 0x3);
              result[dst] = (byte) (code<<4);
              break;
          case 2:
              result[dst++] |= (byte) ((code>>2) & 0xf);
              result[dst] = (byte) (code<<6);
              break;
          case 3:
              result[dst++] |= (byte) (code & 0x3f);
              break;
          }
      }
      } catch (ArrayIndexOutOfBoundsException e) {}
      return result;
    }

    /**
     * Test the decoder and encoder.
     * Call as <code>Base64 [string]</code>.
     */

    public static void
    main(String[] args) {
        System.out.println("encode: " + args[0]  + " -> (" 
            + encode(args[0]) + ")");
        System.out.println("decode: " + args[0]  + " -> (" 
            + new String(decode(args[0])) + ")");
    }
}