package pt.uminho.haslab.safemapper;

import pt.uminho.haslab.cryptoenv.CryptoTechnique;

import java.io.UnsupportedEncodingException;

import static pt.uminho.haslab.safemapper.Helpers.getTweakBytes;
import static pt.uminho.haslab.safemapper.Helpers.whichFpeInstance;


/**
 * Created by rgmacedo on 5/3/17.
 */
public class KeyFPE extends Key {

    private CryptoTechnique.CryptoType cryptoType;
    private int formatSize;
    private String instance;
    private CryptoTechnique.FFX fpe_instance;
    private int radix;
    private String tweak;
    private Boolean keyPadding;

    public KeyFPE() {
        this.cryptoType = CryptoTechnique.CryptoType.FPE;
        this.formatSize = 10;
        this.instance = "FF1";
        this.fpe_instance = CryptoTechnique.FFX.FF1;
        this.radix = 10;
        this.tweak = "";
        this.keyPadding = null;
    }

    public KeyFPE(CryptoTechnique.CryptoType cType, int formatSize, Boolean padding, String instance, int radix, String tweak) {
        super(cType, formatSize, padding);
        this.instance = instance;
        this.fpe_instance = whichFpeInstance(instance);
        this.radix = radix;
        this.tweak = tweak;
    }


    public String getInstance() {
        return this.instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public CryptoTechnique.FFX getFpeInstance() {
        return this.fpe_instance;
    }

    public void setFpeInstance(CryptoTechnique.FFX instance) {
        this.fpe_instance = instance;
    }

    public int getRadix() {
        return this.radix;
    }

    public void setRadix(int radix) {
        this.radix = radix;
    }

    public String getTweak() {
        return this.tweak;
    }

    public void setTweak(String tweak) {
        this.tweak = tweak;
    }


    public byte[] getSecurityParameters(byte[] key) throws UnsupportedEncodingException {
        byte[] temp_tweak = getTweakBytes(this.instance, this.tweak);
        byte[] security_parameters = new byte[key.length + temp_tweak.length];
        System.arraycopy(key, 0, security_parameters, 0, key.length);
        System.arraycopy(temp_tweak, 0, security_parameters, key.length, temp_tweak.length);
        return security_parameters;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(this.instance).append(",");
        sb.append(this.fpe_instance).append(",");
        sb.append(this.radix).append(",");
        sb.append(this.tweak).append("]\n");
        return super.toString() + sb.toString();
    }

}
