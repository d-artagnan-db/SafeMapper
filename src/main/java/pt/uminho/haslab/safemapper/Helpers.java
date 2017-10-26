package pt.uminho.haslab.safemapper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Helpers {
    public static DatabaseSchema.FFX whichFpeInstance(String instance) {
        if (instance.equals("FF1")) {
            return DatabaseSchema.FFX.FF1;
        } else if (instance.equals("FF3")) {
            return DatabaseSchema.FFX.FF3;
        }
        return null;
    }

    public static byte[] getTweakBytes(String instance, String tweak) throws UnsupportedEncodingException {
        byte[] temp = new byte[8];
        if (instance.equals("FF1")) {

            temp = tweak.getBytes(Charset.forName("UTF-8").name());
        } else if (instance.equals("FF3")) {
            byte[] temp_tweak = tweak.getBytes(Charset.forName("UTF-8").name());
            if (temp_tweak.length == 8) {
                temp = temp_tweak;
            } else if (temp_tweak.length > 8) {
                System.arraycopy(temp_tweak, 0, temp, 0, 8);
            } else {
                throw new IllegalArgumentException("For an FF3 instance, the tweak must have a 64bit length");
            }
        }
        return temp;
    }
}
