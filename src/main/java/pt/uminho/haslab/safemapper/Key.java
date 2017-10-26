package pt.uminho.haslab.safemapper;

/**
 * Key class.
 * Holds all the relevant information associated to the Row-Key
 */
public class Key {

    private DatabaseSchema.CryptoType cryptoType;
    private int formatSize;
    private Boolean keyPadding;

    public Key() {
        this.cryptoType = DatabaseSchema.CryptoType.PLT;
        this.formatSize = 0;
        this.keyPadding = null;
    }

    public Key(DatabaseSchema.CryptoType cType, int formatSize, Boolean padding) {
        this.cryptoType = cType;
        this.formatSize = formatSize;
        this.keyPadding = padding;
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

    public Boolean getKeyPadding() {
        return this.keyPadding;
    }

    public void setKeyPadding(Boolean padding) {
        this.keyPadding = padding;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Key [").append(this.cryptoType).append(", ")
                .append(this.formatSize).append(", ")
                .append(this.keyPadding).append("]\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;

        Key key = (Key) o;

        if (formatSize != key.formatSize) return false;
        if (cryptoType != key.cryptoType) return false;
        return keyPadding != null ? keyPadding.equals(key.keyPadding) : key.keyPadding == null;
    }

}
