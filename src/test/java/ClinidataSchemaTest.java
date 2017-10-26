import org.junit.Test;
import pt.uminho.haslab.safemapper.DatabaseSchema;
import pt.uminho.haslab.safemapper.Family;
import pt.uminho.haslab.safemapper.Qualifier;
import pt.uminho.haslab.safemapper.TableSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ClinidataSchemaTest {

    private final Map<String, TableSchema> protectedTables;

    public ClinidataSchemaTest() {
        protectedTables = new HashMap<String, TableSchema>();
        protectedTables.put("R-maxdata-CLINIDATA_NEW-DTW_PATIENT", definePatientTable());
        protectedTables.put("R-maxdata-CLINIDATA_NEW-DTW_PATIENT_ID_BY_PATIENT", definePatientBPatient());
        protectedTables.put("R-maxdata-CLINIDATA_NEW-DTW_TEST_RESULT", defineRes());
    }

    @Test
    public void test() {

        String file = getClass().getResource("/q_engine.xml").getFile();
        DatabaseSchema schema = new DatabaseSchema(file);

        for (String tableName : protectedTables.keySet()) {
            assertEquals(protectedTables.get(tableName), schema.getSchema(tableName));
        }


    }

    private TableSchema definePatientTable() {
        Family fam = new Family();
        fam.setFamilyName("DQE");
        fam.setCryptoType(DatabaseSchema.CryptoType.PLT);

        Qualifier one = generateQualifier(DatabaseSchema.CryptoType.DET, "1", 100, false);
        Qualifier two = generateQualifier(DatabaseSchema.CryptoType.OPE, "2", 16, true);
        Qualifier two_std = generateQualifier(DatabaseSchema.CryptoType.STD, "2_STD", 16, true);
        Qualifier three = generateQualifier(DatabaseSchema.CryptoType.OPE, "3", 16, true);
        Qualifier three_std = generateQualifier(DatabaseSchema.CryptoType.STD, "3_STD", 16, true);

        fam.addQualifier(one);
        fam.addQualifier(two);
        fam.addQualifier(two_std);
        fam.addQualifier(three);
        fam.addQualifier(three_std);
        TableSchema schema = new TableSchema();

        schema.setTablename("R-maxdata-CLINIDATA_NEW-DTW_PATIENT");
        schema.setDefaultKeyCryptoType(DatabaseSchema.CryptoType.PLT);
        schema.setDefaultColumnsCryptoType(DatabaseSchema.CryptoType.PLT);
        schema.setDefaultKeyPadding(false);
        schema.setDefaultColumnPadding(false);
        schema.setDefaultColumnFormatSize(10);
        schema.setDefaultKeyFormatSize(10);
        schema.setEncryptionMode(true);
        schema.addFamily(fam);

        return schema;
    }


    private TableSchema definePatientBPatient() {
        Family fam = new Family();
        fam.setFamilyName("DQE");
        fam.setCryptoType(DatabaseSchema.CryptoType.PLT);

        Qualifier one = generateQualifier(DatabaseSchema.CryptoType.DET, "2", 5, false);
        fam.addQualifier(one);

        TableSchema schema = new TableSchema();
        schema.setTablename("R-maxdata-CLINIDATA_NEW-DTW_PATIENT_ID_BY_PATIENT");
        schema.setDefaultKeyPadding(false);
        schema.setDefaultColumnPadding(false);
        schema.setDefaultColumnFormatSize(10);
        schema.setDefaultKeyFormatSize(10);
        schema.setEncryptionMode(true);
        schema.addFamily(fam);

        return schema;
    }

    private TableSchema defineRes() {
        Family fam = new Family();
        fam.setFamilyName("DQE");
        fam.setCryptoType(DatabaseSchema.CryptoType.PLT);

        Qualifier one = generateQualifier(DatabaseSchema.CryptoType.STD, "3", 4000, false);
        fam.addQualifier(one);

        TableSchema schema = new TableSchema();
        schema.setTablename("R-maxdata-CLINIDATA_NEW-DTW_TEST_RESULT");
        schema.setDefaultKeyPadding(false);
        schema.setDefaultColumnPadding(false);
        schema.setDefaultColumnFormatSize(10);
        schema.setDefaultKeyFormatSize(10);
        schema.setEncryptionMode(true);
        schema.addFamily(fam);

        return schema;
    }

    private Qualifier generateQualifier(DatabaseSchema.CryptoType type, String name, int formatSize, boolean padding) {
        Qualifier qual = new Qualifier();

        qual.setQualifierName(name);
        qual.setCryptoType(type);
        qual.setFormatSize(formatSize);
        qual.setPadding(padding);

        return qual;
    }

}
