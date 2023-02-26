package org.fetch;

import lombok.SneakyThrows;

import java.sql.*;
import java.util.Base64;

public class PostgresClient {

    Connection conn;

    static String SQL_QUERY = "INSERT INTO user_logins (user_id, device_type, masked_ip, masked_device_id, locale, app_version, create_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
    Base64.Encoder encoder;

    @SneakyThrows
    public PostgresClient(String url, String user, String password) {
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        encoder = Base64.getEncoder();
    }

    void insertRecord(UserRecord record) throws SQLException {
        try {
            PreparedStatement statement = mapToSqlQueryStatement(record);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new row has been inserted.");
            }
        } catch (SQLException ex) {
            System.out.println("An error occurred while inserting the row: " + ex.getMessage());
        }
    }

    private PreparedStatement mapToSqlQueryStatement(UserRecord record) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(SQL_QUERY);
        statement.setString(1, record.getUser_id());
        statement.setString(2, record.device_type);
        String maskedIp = encoder.encodeToString(record.getIp().getBytes());
        statement.setString(3, maskedIp);
        String maskedDeviceId = encoder.encodeToString(record.getDevice_id().getBytes());
        statement.setString(4, maskedDeviceId);
        statement.setString(5, record.getLocale());
        int appVersionFirstDigit = Integer.parseInt(record.getApp_version().replaceAll("\\.", ""));
        statement.setInt(6, appVersionFirstDigit);
        statement.setDate(7, new Date(System.currentTimeMillis()));
        return statement;
    }

}
