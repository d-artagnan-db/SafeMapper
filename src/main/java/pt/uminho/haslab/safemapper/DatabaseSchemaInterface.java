package pt.uminho.haslab.safemapper;

public interface DatabaseSchemaInterface {


    boolean containsKey(String tableName);

    TableSchema getSchema(String tableName);


}
