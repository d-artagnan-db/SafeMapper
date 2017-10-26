package pt.uminho.haslab.safemapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Qualifier class.
 * Holds all the relevant information associated to a specific Qualifier.
 */
public class Qualifier {

    private String qualifierName;
    private DatabaseSchema.CryptoType cryptoType;
    private int formatSize;
    private Boolean padding;
    private Map<String, String> properties;

    public Qualifier() {
        this.qualifierName = "";
        this.cryptoType = DatabaseSchema.CryptoType.PLT;
        this.formatSize = 0;
        this.padding = null;
        this.properties = new HashMap<String, String>();
    }

    public Qualifier(String name, DatabaseSchema.CryptoType cType, int format, Boolean padding, Map<String, String> properties) {
        this.qualifierName = name;
        this.cryptoType = cType;
        this.formatSize = format;
        this.padding = padding;
        this.properties = properties;
    }

    public String getName() {
        return this.qualifierName;
    }

    public DatabaseSchema.CryptoType getCryptoType() {
        return this.cryptoType;
    }

    public void setCryptoType(DatabaseSchema.CryptoType cryptoType) {
        this.cryptoType = cryptoType;
    }

    public int getFormatSize() {
        return this.formatSize;
    }

    public void setFormatSize(int format) {
        this.formatSize = format;
    }

    public Boolean getPadding() {
        return this.padding;
    }

    public void setPadding(Boolean padding) {
        this.padding = padding;
    }

    public void setQualifierName(String name) {
        this.qualifierName = name;
    }

    public Map<String, String> getProperties() {
        Map<String, String> propertiesTemp = new HashMap<String, String>();
        for (String s : this.properties.keySet()) {
            propertiesTemp.put(s, this.properties.get(s));
        }
        return propertiesTemp;
    }

    public void setProperties(Map<String, String> prop) {
        this.properties = new HashMap<String, String>();
        for (String s : prop.keySet()) {
            this.properties.put(s, prop.get(s));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Qualifier Name: ").append(qualifierName).append("\n");
        sb.append("Qualifier CryptoType: ").append(cryptoType).append("\n");
        sb.append("Qualifier Format Size: ").append(formatSize).append("\n");
        sb.append("Qualifier Padding: ").append(padding).append("\n");
        sb.append("Qualifier Properties: \n");
        for (String s : this.properties.keySet()) {
            sb.append(s).append(": ").append(this.properties.get(s)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Qualifier)) return false;

        Qualifier qualifier = (Qualifier) o;

        if (formatSize != qualifier.formatSize) return false;
        if (qualifierName != null ? !qualifierName.equals(qualifier.qualifierName) : qualifier.qualifierName != null)
            return false;
        if (cryptoType != qualifier.cryptoType) return false;
        if (padding != null ? !padding.equals(qualifier.padding) : qualifier.padding != null) return false;
        return properties != null ? properties.equals(qualifier.properties) : qualifier.properties == null;
    }

}
