package pt.uminho.haslab.safemapper;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static pt.uminho.haslab.safemapper.Helpers.getTweakBytes;
import static pt.uminho.haslab.safemapper.Helpers.whichFpeInstance;

/**
 * Created by rgmacedo on 5/4/17.
 */
public class QualifierFPE extends Qualifier {
    private String qualifierName;
    private DatabaseSchema.CryptoType cryptoType;
    private int formatSize;
    private Boolean padding;
    private Map<String, String> properties;
    private String instance;
    private DatabaseSchema.FFX fpe_instance;
    private int radix;
    private String tweak;

    public QualifierFPE() {
        this.qualifierName = "";
        this.cryptoType = DatabaseSchema.CryptoType.FPE;
        this.formatSize = 0;
        this.padding = null;
        this.properties = new HashMap<String, String>();
        this.instance = "FF1";
        this.fpe_instance = DatabaseSchema.FFX.FF1;
        this.radix = 10;
        this.tweak = "";
    }

    public QualifierFPE(String qualifierName, DatabaseSchema.CryptoType cType, int formatSize, Boolean padding, Map<String, String> prop, String instance, int radix, String tweak) {
        super(qualifierName, cType, formatSize, padding, prop);
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

    public DatabaseSchema.FFX getFpeInstance() {
        return this.fpe_instance;
    }

    public void setFpeInstance(DatabaseSchema.FFX instance) {
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
        sb.append("Qualifier instance: ").append(this.instance).append("\n");
        sb.append("Qualifier radix: ").append(this.radix).append("\n");
        sb.append("Qualifier tweak: ").append(this.tweak).append("\n");
        return super.toString() + sb.toString();
    }

}
