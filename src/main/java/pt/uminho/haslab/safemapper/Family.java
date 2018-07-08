package pt.uminho.haslab.safemapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pt.uminho.haslab.safemapper.Helpers.whichFpeInstance;

/**
 * Family class.
 * Holds all the relevant information associated to a specific Family.
 */
public class Family {

    private String familyName;
    private DatabaseSchema.CryptoType cryptoType;
    private int formatSize;
    private List<Qualifier> qualifiers;
    private Boolean columnPadding;

    public Family() {
        this.familyName = "";
        this.cryptoType = DatabaseSchema.CryptoType.PLT;
        this.formatSize = 0;
        this.qualifiers = new ArrayList<Qualifier>();
        this.columnPadding = null;
    }

    public Family(String familyName, DatabaseSchema.CryptoType cType, int formatSize, Boolean columnPadding) {
        this.familyName = familyName;
        this.cryptoType = cType;
        this.formatSize = formatSize;
        this.qualifiers = new ArrayList<Qualifier>();
        this.columnPadding = columnPadding;
    }

    public Family(String familyName, DatabaseSchema.CryptoType cType, int formatSize, Boolean columnPadding, List<Qualifier> quals) {
        this.familyName = familyName;
        this.cryptoType = cType;
        this.formatSize = formatSize;
        this.columnPadding = columnPadding;
        this.qualifiers = quals;
    }


    public String getFamilyName() {
        return this.familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public DatabaseSchema.CryptoType getCryptoType() {
        return this.cryptoType;
    }

    public void setCryptoType(DatabaseSchema.CryptoType cType) {
        this.cryptoType = cType;
    }

    public int getFormatSize() {
        return this.formatSize;
    }

    public void setFormatSize(int formatSize) {
        this.formatSize = formatSize;
    }

    public Boolean getColumnPadding() {
        return this.columnPadding;
    }

    public void setColumnPadding(Boolean columnPadding) {
        this.columnPadding = columnPadding;
    }

    public List<Qualifier> getQualifiers() {
        List<Qualifier> qualifiersTemp = new ArrayList<Qualifier>();
        for (Qualifier q : this.qualifiers)
            qualifiersTemp.add(q);
        return qualifiersTemp;
    }

    public Qualifier getQualifier(String qualifierName){
        for(Qualifier q: this.qualifiers){
            if(q.getName().equals(qualifierName)){
                return q;
            }
        }
        return null;
    }


    /**
     * addQualifier(qualifierName : String, cryptoType : CryptoType, formatSize : int, properties : Map<String,String>) method :
     * add a new qualifier to the Qualifiers list. If both format size and CryptoType are undefined, the qualifier assume
     * the default properties (inherited from the column family)
     *
     * @param qualifierName column qualifier
     * @param cryptoType    CryptoBox type
     * @param formatSize    size of qualifier
     */
    public void addQualifier(String qualifierName, DatabaseSchema.CryptoType cryptoType, int formatSize, Boolean columnPadding, Map<String, String> properties) {
        DatabaseSchema.CryptoType cType;
        int fSize = 0;
        Boolean padding = false;

        if (cryptoType != null)
            cType = cryptoType;
        else
            cType = this.cryptoType;

        if (formatSize > 0) {
            fSize = formatSize;
        } else {
            fSize = this.formatSize;
        }

        if (columnPadding != null) {
            padding = columnPadding;
        } else {
            padding = this.columnPadding;
        }

        this.qualifiers.add(new Qualifier(qualifierName, cType, fSize, padding, properties));
    }

    /**
     * addQualifier(qualifier : Qualifier) method : add a new qualifier to the Qualifiers list. If both format size and
     * CryptoType are undefined, the qualifier assume the default properties (inherited from the column family)
     *
     * @param qualifier Qualifier object
     */
    public void addQualifier(Qualifier qualifier) {
        if (qualifier instanceof QualifierFPE) {
            QualifierFPE q = new QualifierFPE();
            QualifierFPE qTemp = (QualifierFPE) qualifier;

            if (qualifier.getName() != null)
                q.setQualifierName(qualifier.getName());

            if (qualifier.getCryptoType() == null)
                q.setCryptoType(this.cryptoType);
            else
                q.setCryptoType(qualifier.getCryptoType());

            if (qualifier.getFormatSize() == 0)
                q.setFormatSize(this.formatSize);
            else
                q.setFormatSize(qualifier.getFormatSize());

            if (qualifier.getPadding() == null)
                q.setPadding(this.columnPadding);
            else
                q.setPadding(qualifier.getPadding());

            if (qTemp.getInstance() != null) {
                q.setInstance(qTemp.getInstance());
                q.setFpeInstance(whichFpeInstance(qTemp.getInstance()));
            }

            if (qTemp.getRadix() > 0)
                q.setRadix(qTemp.getRadix());

            if (qTemp.getTweak() != null)
                q.setTweak(qTemp.getTweak());

            this.qualifiers.add(q);
        } else {
            if (qualifier.getCryptoType() == null)
                qualifier.setCryptoType(this.cryptoType);

            if (qualifier.getFormatSize() == 0)
                qualifier.setFormatSize(this.formatSize);

            if (qualifier.getPadding() == null) {
                qualifier.setPadding(this.columnPadding);
            }

            this.qualifiers.add(qualifier);
        }
    }


    /**
     * containsQualifier(qualifier :String) method : verify if qualifier List contains a given qualifier
     *
     * @param qualifier column qualifier
     * @return true if qualifier exist. Otherwise false.
     */
    public boolean containsQualifier(String qualifier) {
        boolean contains = false;
        for (Qualifier q : this.qualifiers) {
            if (q.getName().equals(qualifier)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Family Name: ").append(familyName).append("\n");
        sb.append("Family CryptoType: ").append(cryptoType).append("\n");
        sb.append("Family FormatSize: ").append(formatSize).append("\n");
        sb.append("Family Padding: ").append(columnPadding).append("\n");
        sb.append("Column Qualifiers: \n");
        for (Qualifier q : this.qualifiers) {
            sb.append(q.toString());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Family)) return false;

        Family family = (Family) o;

        if (formatSize != family.formatSize) return false;
        if (familyName != null ? !familyName.equals(family.familyName) : family.familyName != null) return false;
        if (cryptoType != family.cryptoType) return false;
        if (qualifiers != null ? !qualifiers.equals(family.qualifiers) : family.qualifiers != null) return false;
        return columnPadding != null ? columnPadding.equals(family.columnPadding) : family.columnPadding == null;
    }

}
