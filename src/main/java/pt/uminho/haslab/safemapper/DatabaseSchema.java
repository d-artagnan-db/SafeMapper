package pt.uminho.haslab.safemapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.uminho.haslab.cryptoenv.CryptoTechnique;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * DatabaseSchema class.
 * Used to parse the database schema file and store the database
 */
public class DatabaseSchema implements DatabaseSchemaInterface {
    static final Log LOG = LogFactory.getLog(DatabaseSchema.class.getName());
    public Map<String, TableSchema> tableSchemas;

    private CryptoTechnique.CryptoType defaultPropertiesKey;
    private CryptoTechnique.CryptoType defaultPropertiesColumns;
    private Boolean defaultPropertiesKeyPadding;
    private Boolean defaultPropertiesColumnPadding;
    private int defaultPropertiesKeyFormatSize;
    private int defaultPropertiesColFormatSize;
    private boolean hasDefaultDatabaseProperties;

    private Boolean defaultEncryptionMode;

    private String databaseSchemaFile;


    public DatabaseSchema(String databaseSchemaFile) {
        if (databaseSchemaFile == null) {
            throw new IllegalStateException("Schema file name cannot be null.");
        }

        this.tableSchemas = new HashMap<String, TableSchema>();
        this.hasDefaultDatabaseProperties = false;
        this.databaseSchemaFile = databaseSchemaFile;

        this.parseDatabaseTables();
    }

    public Map<String, TableSchema> getSchemas() {
        return this.tableSchemas;
    }

    public TableSchema getTableSchema(String tablename) {
        if (this.tableSchemas.containsKey(tablename)) {
            return this.tableSchemas.get(tablename);
        } else {
            return null;
        }
    }

    public Map<String, Object> getDatabaseDefaultProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("defaultPropertiesKey", this.defaultPropertiesKey);
        properties.put("defaultPropertiesColumns", this.defaultPropertiesColumns);
        properties.put("defaultPropertiesKeyPadding", this.defaultPropertiesKeyPadding);
        properties.put("defaultPropertiesColumnPadding", this.defaultPropertiesColumnPadding);
        properties.put("defaultPropertiesKeyFormatSize", this.defaultPropertiesKeyFormatSize);
        properties.put("defaultPropertiesColFormatSize", this.defaultPropertiesColFormatSize);
        properties.put("defaultPropertiesEncryptionMode", this.defaultEncryptionMode);

        return properties;
    }

    /**
     * parse(filename : String) method : parse the database schema file (<schema>.xml)
     */
    private void parseDatabaseTables() {
        try {
//			Read schema file
            File inputFile = new File(this.databaseSchemaFile);
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputFile);


//			Map the schema file into an Element object
            Element rootElement = document.getRootElement();

            parseDatabaseDefaultProperties(rootElement.element("default"));

            List<Element> tables = rootElement.elements("table");
            for (Element table_element : tables) {
                TableSchema temp_schema = parseTable(table_element);
                this.tableSchemas.put(temp_schema.getTablename(), temp_schema);
            }


        } catch (DocumentException e) {
            LOG.error("CryptoWorker:DatabaseSchema:parse:DocumentException:" + e.getMessage());
        }
    }


    private boolean strIsEmpty(String str) {
        return str.length() > 0 ? false : true;
    }

    private void parseDatabaseDefaultProperties(Element rootElement) {
        if (rootElement != null) {
            String key = rootElement.elementText("key");
            String cols = rootElement.elementText("columns");
            String keyPadding = rootElement.elementText("keypadding");
            String colPadding = rootElement.elementText("colpadding");
            String keySize = rootElement.elementText("keyformatsize");
            String colSize = rootElement.elementText("colformatsize");
            String encryptionMode = rootElement.elementText("encryptionmode");

            if (key == null || strIsEmpty(key)) {
                throw new NullPointerException("CryptoWorker:DatabaseSchema:parseDatabaseDefaultProperties:Default Row-Key Cryptographic Type cannot be null nor empty.");
            }
            if (cols == null || strIsEmpty(cols)) {
                throw new NullPointerException("CryptoWorker:DatabaseSchema:parseDatabaseDefaultProperties:Default columns Cryptographic Type cannot be null nor empty.");
            }
            if (keyPadding == null || strIsEmpty(keyPadding)) {
                throw new NullPointerException("CryptoWorker:DatabaseSchema:parseDatabaseDefaultProperties:Default key padding cannot be null nor empty.");
            }
            if (colPadding == null || strIsEmpty(colPadding)) {
                throw new NullPointerException("CryptoWorker:DatabaseSchema:parseDatabaseDefaultProperties:Default columns padding cannot be null nor empty.");
            }
            if (keySize == null || strIsEmpty(keySize)) {
                throw new NullPointerException("CryptoWorker:DatabaseSchema:parseDatabaseDefaultProperties:Default key format size cannot be null nor empty.");
            }
            if (colSize == null || strIsEmpty(colSize)) {
                throw new NullPointerException("CryptoWorker:DatabaseSchema:parseDatabaseDefaultProperties:Default columns format size cannot be null nor empty.");
            }
            if (encryptionMode == null || strIsEmpty(encryptionMode)) {
                throw new NullPointerException("CryptoWorker:DatabaseSchema:parseDatabaseDefaultProperties:Default encryption mode cannot be null nor empty.");
            }

            this.defaultPropertiesKey = switchCryptoType(key);
            this.defaultPropertiesColumns = switchCryptoType(cols);
            this.defaultPropertiesKeyPadding = paddingBooleanConvertion(keyPadding);
            this.defaultPropertiesColumnPadding = paddingBooleanConvertion(colPadding);
            this.defaultPropertiesKeyFormatSize = formatSizeIntegerValue(keySize);
            this.defaultPropertiesColFormatSize = formatSizeIntegerValue(colSize);
            this.hasDefaultDatabaseProperties = true;
            this.defaultEncryptionMode = modeConversion(encryptionMode);
        } else {
            throw new NullPointerException("DatabaseSchema:parseDatabaseDefaultProperties:Default element cannot be null.");
        }
    }

    private TableSchema parseTable(Element rootElement) {
        TableSchema ts = new TableSchema();

        parseTablename(rootElement, ts);
        parseTableDefaultProperties(rootElement, ts);
        parseKey(rootElement, ts);
        parseColumns(rootElement, ts);

        return ts;
    }


    /**
     * parseTablename(rootElement : Element) method : parse the table name
     *
     * @param rootElement main Element node
     */
    private void parseTablename(Element rootElement, TableSchema tableSchema) {
        Element nameElement = rootElement.element("name");
        String name = nameElement.getText();
        if (name == null || strIsEmpty(name)) {
            throw new NullPointerException("CryptoWorker:DatabaseSchema:parseTablename:Table name cannot be null nor empty.");
        }

        tableSchema.setTablename(name);
    }

    /**
     * parseDefault(rootElement : Element) method : parse the default database parameters
     *
     * @param rootElement main Element node
     */
    private void parseTableDefaultProperties(Element rootElement, TableSchema tableSchema) {
        Element defaultElement = rootElement.element("default");
        if (defaultElement != null) {
            String key = defaultElement.elementText("key");
            String columns = defaultElement.elementText("columns");
            String keyformatsize = defaultElement.elementText("keyformatsize");
            String colformatsize = defaultElement.elementText("colformatsize");
            String keypadding = defaultElement.elementText("keypadding");
            String colpadding = defaultElement.elementText("colpadding");
            String encryptionMode = defaultElement.elementText("encryptionmode");

            if (key == null || strIsEmpty(key)) {
                tableSchema.setDefaultKeyCryptoType(this.defaultPropertiesKey);
            } else {
                tableSchema.setDefaultKeyCryptoType(switchCryptoType(key));
            }

            if (columns == null || strIsEmpty(columns)) {
                tableSchema.setDefaultColumnsCryptoType(this.defaultPropertiesColumns);
            } else {
                tableSchema.setDefaultColumnsCryptoType(switchCryptoType(columns));
            }

            if (keyformatsize == null || strIsEmpty(keyformatsize)) {
                tableSchema.setDefaultKeyFormatSize(this.defaultPropertiesKeyFormatSize);
            } else {
                tableSchema.setDefaultKeyFormatSize(formatSizeIntegerValue(keyformatsize));
            }

            if (colformatsize == null || strIsEmpty(colformatsize)) {
                tableSchema.setDefaultColumnFormatSize(this.defaultPropertiesColFormatSize);
            } else {
                tableSchema.setDefaultColumnFormatSize(formatSizeIntegerValue(colformatsize));
            }

            if (keypadding == null || strIsEmpty(keypadding)) {
                tableSchema.setDefaultKeyPadding(this.defaultPropertiesKeyPadding);
            } else {
                tableSchema.setDefaultKeyPadding(paddingBooleanConvertion(keypadding));
            }

            if (colpadding == null || strIsEmpty(colpadding)) {
                tableSchema.setDefaultColumnPadding(this.defaultPropertiesColumnPadding);
            } else {
                tableSchema.setDefaultColumnPadding(paddingBooleanConvertion(colpadding));
            }

            if (encryptionMode == null || strIsEmpty(encryptionMode)) {
                tableSchema.setEncryptionMode(this.defaultEncryptionMode);
            } else {
                tableSchema.setEncryptionMode(modeConversion(encryptionMode));
            }
        } else if (this.hasDefaultDatabaseProperties) {
            tableSchema.setDefaultKeyCryptoType(this.defaultPropertiesKey);
            tableSchema.setDefaultColumnsCryptoType(this.defaultPropertiesColumns);
            tableSchema.setDefaultKeyFormatSize(this.defaultPropertiesKeyFormatSize);
            tableSchema.setDefaultColumnFormatSize(this.defaultPropertiesColFormatSize);
            tableSchema.setDefaultKeyPadding(this.defaultPropertiesKeyPadding);
            tableSchema.setDefaultColumnPadding(this.defaultPropertiesColumnPadding);
            tableSchema.setEncryptionMode(this.defaultEncryptionMode);
        } else {
            throw new NullPointerException("CryptoWorker:DatabaseSchema:parseDefault:Default arguments specification cannot be null nor empty.");
        }
    }

    /**
     * parseKey(rootElement : Element) method : parse the key properties from the database schema
     *
     * @param rootElement main Element node
     */
    private void parseKey(Element rootElement, TableSchema tableSchema) {
        Element keyElement = rootElement.element("key");
        if (keyElement != null) {
            String cryptotechnique = keyElement.elementText("cryptotechnique");
            String formatsize = keyElement.elementText("formatsize");
            String keypadding = keyElement.elementText("keypadding");

            String instance = keyElement.elementText("instance");
            String radix = keyElement.elementText("radix");
            String tweak = keyElement.elementText("tweak");

            if (cryptotechnique == null || strIsEmpty(cryptotechnique)) {
                cryptotechnique = tableSchema.getDefaultKeyCryptoType().toString();
            }

            if (formatsize == null || strIsEmpty(formatsize)) {
                formatsize = String.valueOf(tableSchema.getDefaultKeyFormatSize());
            }

            if (keypadding == null || strIsEmpty(keypadding)) {
                keypadding = String.valueOf(tableSchema.getDefaultKeyPadding());
            }

            if (cryptotechnique.equals("FPE")) {
                validateFPEArguments(instance, radix, tweak);
            }

            if (!cryptotechnique.equals("FPE")) {
                Key key = new Key(switchCryptoType(cryptotechnique), formatSizeIntegerValue(formatsize), paddingBooleanConvertion(keypadding));
                tableSchema.setKey(key);
            } else {
                Key key = new KeyFPE(
                        switchCryptoType(cryptotechnique),
                        formatSizeIntegerValue(formatsize),
                        paddingBooleanConvertion(keypadding),
                        instance,
                        radixIntegerValue(radix),
                        tweak);
                tableSchema.setKey(key);
            }
        } else {
//			If key arguments are not specified in schema file create key with the default values
            tableSchema.setKey(new Key(tableSchema.getDefaultKeyCryptoType(), tableSchema.getDefaultKeyFormatSize(), tableSchema.getDefaultKeyPadding()));
        }
    }

    /**
     * parseColumns(rootElement : Element) method : parse the column families and qualifiers properties from the database schema
     *
     * @param rootElement main Element node
     */
    private void parseColumns(Element rootElement, TableSchema tableSchema) {
        Element columnsElement = rootElement.element("columns");
        if (columnsElement == null) {
            throw new NoSuchElementException("Columns arguments cannot be null.");
        }

        List<Element> familiesElement = columnsElement.elements("family");
        for (Element family : familiesElement) {
            if (family != null) {
                String familyName = family.elementText("name");
                String familyCryptoTechnique = family.elementText("cryptotechnique");
                String familyFormatSize = family.elementText("colformatsize");
                String familyPadding = family.elementText("colpadding");

                if (familyName == null || strIsEmpty(familyName)) {
                    throw new NullPointerException("Column Family name cannot be null nor empty.");
                }

                if (familyCryptoTechnique == null || strIsEmpty(familyCryptoTechnique)) {
                    familyCryptoTechnique = tableSchema.getDefaultColumnsCryptoType().toString();
                }

                if (familyFormatSize == null || strIsEmpty(familyFormatSize)) {
                    familyFormatSize = String.valueOf(tableSchema.getDefaultColumnFormatSize());
                }

                if (familyPadding == null || strIsEmpty(familyPadding)) {
                    familyPadding = String.valueOf(tableSchema.getDefaultColumnPadding());
                }


                Family f = new Family(
                        familyName,
                        switchCryptoType(familyCryptoTechnique),
                        formatSizeIntegerValue(familyFormatSize),
                        paddingBooleanConvertion(familyPadding));

                tableSchema.addFamily(f);

                List<Element> qualifiersElement = family.elements("qualifier");
                for (Element qualifier : qualifiersElement) {
                    String qualifierName = qualifier.elementText("name");
                    String cryptotechniqueQualifier = qualifier.elementText("cryptotechnique");
                    String qualifierFormatsize = qualifier.elementText("colformatsize");
                    String qualifierPadding = qualifier.elementText("colpadding");

                    String instance = qualifier.elementText("instance");
                    String radix = qualifier.elementText("radix");
                    String tweak = qualifier.elementText("tweak");

                    List<Element> misc = qualifier.elements("misc");
                    Map<String, String> properties = parseMiscellaneous(misc);

                    if (qualifierName == null || strIsEmpty(qualifierName)) {
                        throw new NullPointerException("Column qualifier name cannot be null nor empty.");
                    }

                    if (cryptotechniqueQualifier == null || strIsEmpty(cryptotechniqueQualifier)) {
                        cryptotechniqueQualifier = familyCryptoTechnique;
                    }

                    if (qualifierFormatsize == null || strIsEmpty(qualifierFormatsize)) {
                        qualifierFormatsize = familyFormatSize;
                    }

                    if (qualifierPadding == null || strIsEmpty(qualifierPadding)) {
                        qualifierPadding = familyPadding;
                    }

                    if (cryptotechniqueQualifier.equals("FPE")) {
                        validateFPEArguments(instance, radix, tweak);
                    }

                    Qualifier q;
                    if (!cryptotechniqueQualifier.equals("FPE")) {
                        q = new Qualifier(
                                qualifierName,
                                switchCryptoType(cryptotechniqueQualifier),
                                formatSizeIntegerValue(qualifierFormatsize),
                                paddingBooleanConvertion(qualifierPadding),
                                properties);

                    } else {
                        q = new QualifierFPE(
                                qualifierName,
                                switchCryptoType(cryptotechniqueQualifier),
                                formatSizeIntegerValue(qualifierFormatsize),
                                paddingBooleanConvertion(qualifierPadding),
                                properties,
                                instance,
                                radixIntegerValue(radix),
                                tweak
                        );
                    }

                    tableSchema.addQualifier(familyName, q);

                    if (cryptotechniqueQualifier.equals("OPE")) {
                        String stdQualifierName = qualifierName + "_STD";
                        String stdCType = "STD";

                        Qualifier std = new Qualifier(
                                stdQualifierName,
                                switchCryptoType(stdCType),
                                formatSizeIntegerValue(qualifierFormatsize),
                                paddingBooleanConvertion(qualifierPadding),
                                properties
                        );

                        tableSchema.addQualifier(familyName, std);
                    }

                }
            }
        }
    }


    /**
     * parseMiscellaneous(properties : List<Element>) method : parse random properties from the database schema
     *
     * @param properties list of Element nodes
     * @return a mapper of the property and the type in Map<String,String> format
     */
    private Map<String, String> parseMiscellaneous(List<Element> properties) {
        Map<String, String> result = new HashMap<String, String>();
        for (Element property : properties) {
            result.put(property.elementText("property"), property.elementText("type"));
        }
        return result;
    }

    //	FIXME: read cType with Enum.valueOf()
    private CryptoTechnique.CryptoType switchCryptoType(String cType) {
        if (cType == null) {
            return null;
        } else {
            if (cType.equals("STD")) {
                return CryptoTechnique.CryptoType.STD;
            } else if (cType.equals("DET")) {
                return CryptoTechnique.CryptoType.DET;

            } else if (cType.equals("OPE")) {
                return CryptoTechnique.CryptoType.OPE;
            } else if (cType.equals("FPE")) {
                return CryptoTechnique.CryptoType.FPE;
            } else if (cType.equals("PLT")) {
                return CryptoTechnique.CryptoType.PLT;

            }
            return CryptoTechnique.CryptoType.PLT;
        }


    }

    private int formatSizeIntegerValue(String formatSize) {
        int value;
        if (formatSize == null || strIsEmpty(formatSize))
            value = 0;
        else {
            try {
                value = Integer.parseInt(formatSize);
            } catch (NumberFormatException e) {
                LOG.error("DatabaseSchema:formatSizeIntegerValue:NumberFormatException:" + e.getMessage());
                value = 0;
            }
        }
        return value;
    }

    private Boolean paddingBooleanConvertion(String padding) {
        Boolean value;
        if (padding == null || strIsEmpty(padding)) {
            throw new NullPointerException("DatabaseSchema:paddingBooleanConvertion:Boolean Value cannot be null nor empty.");
        } else {
            value = Boolean.valueOf(padding);
        }
        return value;
    }

    private int radixIntegerValue(String radix) {
        if (radix == null || strIsEmpty(radix))
            return 10;
        else
            return Integer.parseInt(radix);
    }

    private void validateFPEArguments(String instance, String radix, String tweak) {
        if (instance == null || strIsEmpty(instance)) {
            throw new NullPointerException("Format-Preserving Encryption instance cannot be null nor empty.");
        }

        if (radix == null || strIsEmpty(radix)) {
            throw new NullPointerException("Format-Preserving Encryption radix cannot be null nor empty.");
        }

        if (tweak == null) {
            throw new NullPointerException("Format-Preserving Encryption tweak cannot be null.");
        }
    }

    public Boolean modeConversion(String mode) {
        if (mode.equals("enable") || mode.equals("ENABLE")) {
            return true;
        } else if (mode.equals("disable") || mode.equals("DISABLE")) {
            return false;
        } else {
            throw new NullPointerException("DatabaseSchema - Invalid mode conversion");
        }
    }

    public String printDatabaseSchemas() {
        StringBuilder sb = new StringBuilder();
        sb.append("ParseDatabaseDefaultProperties:\n");
        sb.append("Default Key CryptoType: ").append(this.defaultPropertiesKey).append("\n");
        sb.append("Default Columns CryptoType: ").append(this.defaultPropertiesColumns).append("\n");
        sb.append("Default Key Padding: ").append(this.defaultPropertiesKeyPadding).append("\n");
        sb.append("Default Column Padding: ").append(this.defaultPropertiesColumnPadding).append("\n");
        sb.append("Default Key FormatSize: ").append(this.defaultPropertiesKeyFormatSize).append("\n");
        sb.append("Default Column FormatSize: ").append(this.defaultPropertiesColFormatSize).append("\n");

        for (String schema : tableSchemas.keySet()) {
            sb.append("---------------------------\n");
            sb.append(tableSchemas.get(schema).toString());
        }
        return sb.toString();
    }

    public boolean containsKey(String tableName) {
        return this.tableSchemas.containsKey(tableName);
    }

    public TableSchema getSchema(String tableName) {
        return this.tableSchemas.get(tableName);
    }
}
