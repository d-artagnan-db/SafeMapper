package pt.uminho.haslab.safemapper;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pt.uminho.haslab.safemapper.Helpers.whichFpeInstance;


/**
 * TableSchema class.
 * Mapper of the database schema provided by the user.
 */
public class TableSchema {
    static final Log LOG = LogFactory.getLog(TableSchema.class.getName());

    private String tablename;
    //	Default Row-Key CryptoBox
    private DatabaseSchema.CryptoType defaultKeyCryptoType;
    //	Default Qualifiers CryptoBox
    private DatabaseSchema.CryptoType defaultColumnsCryptoType;
    //	Default Row-Key format size
    private int defaultKeyFormatSize;
    //	Default values format size
    private int defaultColumnFormatSize;
    //	Default Row-Key padding
    private Boolean defaultKeyPadding;
    //	Default values padding
    private Boolean defaultColumnPadding;

    private Boolean defaultEncryptionMode;


    //	Key object. Contains CryptoBox, formatSize and other information about the Row-Key
    private Key key;
    //  Collection of the database column families (and qualifiers)
    private List<Family> columnFamilies;

    private Map<DatabaseSchema.CryptoType, Boolean> enabledCryptoTypes;

    public TableSchema() {
        this.tablename = "";
        this.defaultKeyCryptoType = DatabaseSchema.CryptoType.PLT;
        this.defaultColumnsCryptoType = DatabaseSchema.CryptoType.PLT;
        this.defaultKeyFormatSize = 0;
        this.defaultColumnFormatSize = 0;
        this.defaultKeyPadding = false;
        this.defaultColumnPadding = false;
        this.key = new Key();
        this.columnFamilies = new ArrayList<Family>();
        this.defaultEncryptionMode = false;

        this.enabledCryptoTypes = initializeEnabledCryptoTypes();
    }


    public String getTablename() {
        return this.tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public DatabaseSchema.CryptoType getDefaultKeyCryptoType() {
        return this.defaultKeyCryptoType;
    }

    public void setDefaultKeyCryptoType(DatabaseSchema.CryptoType cType) {
        defaultKeyCryptoType = cType;
        key.setCryptoType(cType);
        this.enableCryptoType(cType);
    }

    public DatabaseSchema.CryptoType getDefaultColumnsCryptoType() {
        return this.defaultColumnsCryptoType;
    }

    public void setDefaultColumnsCryptoType(DatabaseSchema.CryptoType cType) {
        defaultColumnsCryptoType = cType;
        enableCryptoType(cType);
    }

    public int getDefaultKeyFormatSize() {
        return this.defaultKeyFormatSize;

    }

    public void setDefaultKeyFormatSize(int formatSize) {
        defaultKeyFormatSize = formatSize;
        key.setFormatSize(formatSize);
    }

    public int getDefaultColumnFormatSize() {
        return this.defaultColumnFormatSize;
    }

    public void setDefaultColumnFormatSize(int formatSize) {
        this.defaultColumnFormatSize = formatSize;
    }

    public Boolean getDefaultKeyPadding() {
        return this.defaultKeyPadding;
    }

    public void setDefaultKeyPadding(Boolean padding) {
        defaultKeyPadding = padding;
        key.setKeyPadding(padding);
    }

    public Boolean getDefaultColumnPadding() {
        return this.defaultColumnPadding;
    }

    public void setDefaultColumnPadding(Boolean padding) {
        this.defaultColumnPadding = padding;
    }

    public Boolean getEncryptionMode() {
        return this.defaultEncryptionMode;
    }

    public void setEncryptionMode(Boolean mode) {
        this.defaultEncryptionMode = mode;
    }

    public Key getKey() {
        return this.key;
    }

    public void setKey(Key key) {
        if (key instanceof KeyFPE) {
            KeyFPE temp = new KeyFPE();
            if (key.getCryptoType() == null) {
                temp.setCryptoType(this.defaultKeyCryptoType);
            } else {
                temp.setCryptoType(key.getCryptoType());
                this.enableCryptoType(key.getCryptoType());
            }

            if (key.getFormatSize() <= 0) {
                temp.setFormatSize(this.defaultKeyFormatSize);
            } else {
                temp.setFormatSize(key.getFormatSize());
            }

            if (key.getKeyPadding() == null) {
                temp.setKeyPadding(this.defaultKeyPadding);
            } else {
                temp.setKeyPadding(key.getKeyPadding());
            }

            if (((KeyFPE) key).getInstance() != null) {
                temp.setInstance(((KeyFPE) key).getInstance());
                temp.setFpeInstance(whichFpeInstance(((KeyFPE) key).getInstance()));
            }

            if (((KeyFPE) key).getRadix() > 0) {
                temp.setRadix(((KeyFPE) key).getRadix());
            }

            if (((KeyFPE) key).getTweak() != null) {
                temp.setTweak(((KeyFPE) key).getTweak());
            }

            this.key = temp;
        } else {
            if (key.getCryptoType() == null) {
                this.key.setCryptoType(this.defaultKeyCryptoType);
            } else {
                this.key.setCryptoType(key.getCryptoType());
                this.enableCryptoType(key.getCryptoType());
            }

            if (key.getFormatSize() <= 0) {
                this.key.setFormatSize(this.defaultKeyFormatSize);
            } else {
                this.key.setFormatSize(key.getFormatSize());
            }

            if (key.getKeyPadding() == null) {
                this.key.setKeyPadding(this.defaultKeyPadding);
            } else {
                this.key.setKeyPadding(key.getKeyPadding());
            }
        }
    }

    public List<Family> getColumnFamilies() {
        List<Family> tempFamilies = new ArrayList<Family>();

        for (Family family : this.columnFamilies)
            tempFamilies.add(family);

        return tempFamilies;
    }

    public void setColumnFamilies(List<Family> families) {
        this.columnFamilies = new ArrayList<Family>();
        this.columnFamilies.addAll(families);
    }

    /**
     * addFamily(familyName : String, cType : CryptoType, formatSize : int, qualifiers : List<Qualifier>) method : add a new column family to the database mapper
     * Parametrized version.
     *
     * @param familyName column family name
     * @param cType      CryptoBox type
     * @param formatSize column family default size
     * @param qualifiers set of column qualifiers
     */
    public void addFamily(String familyName, DatabaseSchema.CryptoType cType, int formatSize, Boolean padding, List<Qualifier> qualifiers) {
        Family family = new Family();
        family.setFamilyName(familyName);

        if (cType == null) {
            family.setCryptoType(defaultColumnsCryptoType);
        } else {
            family.setCryptoType(cType);
            this.enableCryptoType(family.getCryptoType());
        }

        if (formatSize <= 0) {
            family.setFormatSize(defaultColumnFormatSize);
        } else {
            family.setFormatSize(formatSize);
        }

        if (padding == null) {
            family.setColumnPadding(defaultColumnPadding);
        } else {
            family.setColumnPadding(padding);
        }

        for (Qualifier q : qualifiers) {
            family.addQualifier(q);
        }

        this.columnFamilies.add(family);
    }

    /**
     * addFamily(fam : Family) method : add a new column family to the database mapper
     * Object version.
     *
     * @param fam Family object
     */
    public void addFamily(Family fam) {
        if (fam.getCryptoType() == null) {
            fam.setCryptoType(defaultColumnsCryptoType);
        }

        if (fam.getFormatSize() <= 0) {
            fam.setFormatSize(defaultColumnFormatSize);
        }

        if (fam.getColumnPadding() == null) {
            fam.setColumnPadding(defaultColumnPadding);
        }

        List<Qualifier> qualifiers = fam.getQualifiers();
        for (Qualifier qual : qualifiers) {
            enableCryptoType(qual.getCryptoType());
        }
        enableCryptoType(fam.getCryptoType());
        this.columnFamilies.add(fam);
    }

    public Family getFamily(String familyName) {
        Family wantedFamily = null;
        boolean hasFamily = false;
        Iterator<Family> family_iterator = this.columnFamilies.iterator();
        while (family_iterator.hasNext() && !hasFamily) {
            Family temp_family = family_iterator.next();
            if (temp_family.getFamilyName().equals(familyName)) {
                wantedFamily = temp_family;
                hasFamily = true;
            }
        }

        return wantedFamily;
    }

    public boolean containsFamily(String family) {
        boolean contains = false;
        for (Family f : this.columnFamilies) {
            if (f.getFamilyName().equals(family)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * addQualifier(familyName : String, qualifier : Qualifier) method : add a new column qualifier to the respective family collection
     *
     * @param familyName column family name
     * @param qualifier  Qualifier object.
     */
    public void addQualifier(String familyName, Qualifier qualifier) {
        int index = 0;

        for (Family f : this.columnFamilies) {
            if (f.getFamilyName().equals(familyName)) {
                index = this.columnFamilies.indexOf(f);
                break;
            }
        }

        if (index > -1) {
            Family f = this.columnFamilies.get(index);
            f.addQualifier(qualifier);
            this.columnFamilies.set(index, f);
            this.enableCryptoType(qualifier.getCryptoType());
            if (qualifier.getCryptoType().equals(DatabaseSchema.CryptoType.OPE)) {
                this.enableCryptoType(DatabaseSchema.CryptoType.STD);
            }
        }
    }

    public boolean containsQualifier(String family, String qualifier) {
        boolean contains = false;
        if (containsFamily(family)) {
            if (getFamily(family).containsQualifier(qualifier)) {
                contains = true;
            }
        }

        return contains;
    }

    /**
     * getCryptoTypeFromQualifier(family : String, qualifier : String)  method : get the CryptoType of a given family:qualifier
     *
     * @param family    column family
     * @param qualifier column qualifier
     * @return the respective CryptoType
     */
//	TODO: profile
    public DatabaseSchema.CryptoType getCryptoTypeFromQualifier(String family, String qualifier) {
        DatabaseSchema.CryptoType cType = null;
        for (Family f : this.columnFamilies) {
            if (f.getFamilyName().equals(family)) {
                for (Qualifier q : f.getQualifiers()) {
                    if (q.getName().equals(qualifier)) {
                        cType = q.getCryptoType();
                        break;
                    }
                }
                break;
            }
        }

        if (cType == null) {
//			LOG.info("Exception:TableSchema:getCryptoTypeFromQualifier:The specified qualifier ("+family+","+qualifier+") does not exists.");
            cType = defaultColumnsCryptoType;
        }

        return cType;

    }

    public void setColumnProperty(String family, String qualifier, String key, String value){
        for (Family f : this.getColumnFamilies()) {
            if (f.getFamilyName().equals(family)) {
                for (Qualifier q : f.getQualifiers()) {
                    if (q.getName().equals(qualifier)) {
                        q.getProperties().put(key, value);
                        break;
                    }
                }
                break;
            }
        }
    }

    public boolean isIntegerColumn(String family, String qualifier){
        String type = null;
        for (Family f : this.columnFamilies) {
            if (f.getFamilyName().equals(family)) {
                for (Qualifier q : f.getQualifiers()) {
                    if (q.getName().equals(qualifier)) {
                        type = q.getProperties().get("type");
                        break;
                    }

                }
                break;
            }
        }

        if(type != null){
            LOG.debug("Found column with type " + type);
            System.out.println("Found column with type " + type);
            return  type.equals("Integer");
        }
        LOG.debug("Column " + family+":"+qualifier +"  was not found");
        System.out.println("Found column with type " + type);
        return false;
    }



    /**
     * getGeneratorTypeFromQualifier(family : String, qualifier : String)  method : get the generator type of a given family:qualifier (e.g., String, Date, Integer)
     *
     * @param family    column family
     * @param qualifier column qualifier
     * @return the respective Generator in string format
     */
    public String getGeneratorTypeFromQualifier(String family, String qualifier) {
        String gen = null;
        for (Family f : this.columnFamilies) {
            if (f.getFamilyName().equals(family)) {
                for (Qualifier q : f.getQualifiers()) {
                    if (q.getName().equals(qualifier)) {
                        gen = q.getProperties().get("GENERATOR");
                        break;
                    }
                }
                break;
            }
        }
        return gen;
    }

    /**
     * getFormatSizeFromQualifier(family : String, qualifier : String)  method : get the FormatSize of a given family:qualifier
     *
     * @param family    column family
     * @param qualifier column qualifier
     * @return the respective format size in Integer format
     */
//	TODO: profile
    public Integer getFormatSizeFromQualifier(String family, String qualifier) {
        int formatSize = 0;
        for (Family f : this.columnFamilies) {
            if (f.getFamilyName().equals(family)) {
                for (Qualifier q : f.getQualifiers()) {
                    if (q.getName().equals(qualifier)) {
                        formatSize = q.getFormatSize();
                        break;
                    }
                }
                break;
            }
        }
        return formatSize;
    }

    public Integer getKeyFormatSize() {
        return this.key.getFormatSize();
    }

    public Boolean getKeyPadding() {
        return this.key.getKeyPadding();
    }

    //	TODO: profile
    public Boolean getColumnPadding(String family, String qualifier) {
        Boolean columnPadding = null;
        for (Family f : this.columnFamilies) {
            if (f.getFamilyName().equals(family)) {
                columnPadding = f.getColumnPadding();
                for (Qualifier q : f.getQualifiers()) {
                    if (q.getName().equals(qualifier)) {
                        columnPadding = q.getPadding();
                        break;
                    }
                }
                break;
            }
        }
        if (columnPadding == null) {
            columnPadding = this.defaultColumnPadding;
        }
        return columnPadding;
    }

    //TODO : CHECK if ConccurrentHashMap is the correct type for the var. Are locks needed for correct behavior.
    private Map<DatabaseSchema.CryptoType, Boolean> initializeEnabledCryptoTypes() {
        Map<DatabaseSchema.CryptoType, Boolean> cTypes = new ConcurrentHashMap<DatabaseSchema.CryptoType, Boolean>();
        for (DatabaseSchema.CryptoType ct : DatabaseSchema.CryptoType.values()) {
            cTypes.put(ct, false);
        }
        return cTypes;
    }

    private void enableCryptoType(DatabaseSchema.CryptoType cType) {
        this.enabledCryptoTypes.put(cType, true);
    }

    public List<DatabaseSchema.CryptoType> getEnabledCryptoTypes() {
        List<DatabaseSchema.CryptoType> cTypes = new ArrayList<DatabaseSchema.CryptoType>(this.enabledCryptoTypes.size());
        for (DatabaseSchema.CryptoType ct : this.enabledCryptoTypes.keySet()) {
            if (enabledCryptoTypes.get(ct).equals(true)) {
                cTypes.add(ct);
            }
        }
        return cTypes;
    }

    public void printEnabledCryptoTypes() {
        System.out.println(this.enabledCryptoTypes.toString());
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Table Schema\n");
        sb.append("Table Name: ").append(this.tablename).append("\n");
        sb.append("Default Schema: \n");
        sb.append("> Default Key CryptoType: ").append(defaultKeyCryptoType).append("\n");
        sb.append("> Default Columns CryptoType: ").append(defaultColumnsCryptoType).append("\n");
        sb.append("> Default Key Format Size CryptoType: ").append(defaultKeyFormatSize).append("\n");
        sb.append("> Default Column Format Size CryptoType: ").append(defaultColumnFormatSize).append("\n");
        sb.append("> Default Encryption Mode: ").append(defaultEncryptionMode).append("\n");
        sb.append("Key CryptoType: ").append(this.key.toString()).append("\n");
        sb.append("Columns: \n");
        for (Family family : this.columnFamilies) {
            sb.append("> Family: ").append(family.toString()).append("\n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableSchema)) return false;

        TableSchema that = (TableSchema) o;

        if (defaultKeyFormatSize != that.defaultKeyFormatSize) return false;
        if (defaultColumnFormatSize != that.defaultColumnFormatSize) return false;
        if (tablename != null ? !tablename.equals(that.tablename) : that.tablename != null) return false;
        if (defaultKeyCryptoType != that.defaultKeyCryptoType) return false;
        if (defaultColumnsCryptoType != that.defaultColumnsCryptoType) return false;
        if (defaultKeyPadding != null ? !defaultKeyPadding.equals(that.defaultKeyPadding) : that.defaultKeyPadding != null)
            return false;
        if (defaultColumnPadding != null ? !defaultColumnPadding.equals(that.defaultColumnPadding) : that.defaultColumnPadding != null)
            return false;
        if (defaultEncryptionMode != null ? !defaultEncryptionMode.equals(that.defaultEncryptionMode) : that.defaultEncryptionMode != null)
            return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (columnFamilies != null ? !columnFamilies.equals(that.columnFamilies) : that.columnFamilies != null)
            return false;
        return enabledCryptoTypes != null ? enabledCryptoTypes.equals(that.enabledCryptoTypes) : that.enabledCryptoTypes == null;
    }


}
