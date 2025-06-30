package org.CreadoresProgram.CraftJ2ME.utils;
import java.util.Vector;
public class StringUtils{
    public static String join(String delimiter, String[] arry){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < arry.length; i++){
            sb.append(arry[i]);
            if(i < arry.length - 1){
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
    public static String[] split(String str, String delimiter) {
        if (str == null || delimiter == null || delimiter.length() == 0) {
            return new String[] { str };
        }
        Vector parts = new Vector();
        int start = 0;
        int end;
        while ((end = str.indexOf(delimiter, start)) != -1) {
            parts.addElement(str.substring(start, end));
            start = end + delimiter.length();
        }
        parts.addElement(str.substring(start));
        String[] result = new String[parts.size()];
        parts.copyInto(result);
        return result;
    }
}