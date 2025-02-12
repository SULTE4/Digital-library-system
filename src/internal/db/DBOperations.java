package internal.db;

import java.sql.Connection;
import java.util.Map;

public interface DBOperations {
    void insertRow(Connection conn, String tableName, Object... values);
    void updateData(Connection conn, String tableName, int id, Map<String, Object> updates);
    void readData(Connection conn, String tableName);
    void deleteRow(Connection conn, String tableName, int id);
}
