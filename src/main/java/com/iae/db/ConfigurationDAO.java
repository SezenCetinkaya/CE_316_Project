package com.iae.db;

import com.iae.core.Configuration;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationDAO {
    private final DatabaseHelper dbHelper = new DatabaseHelper();

    public int insert(Configuration config) {
        String sql = "INSERT INTO Configuration(name, language, compilerPath, compileArgs, sourceFilename, runCommand, isInterpreted, timeoutSeconds) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, config.getName());
            pstmt.setString(2, config.getLanguage());
            pstmt.setString(3, config.getCompilerPath());
            pstmt.setString(4, config.getCompileArgs());
            pstmt.setString(5, config.getSourceFilename());
            pstmt.setString(6, config.getRunCommand());
            pstmt.setInt(7, config.isInterpreted() ? 1 : 0);
            pstmt.setInt(8, config.getTimeoutSeconds());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Configuration> findAll() {
        List<Configuration> configs = new ArrayList<>();
        String sql = "SELECT * FROM Configuration";
        try (Connection conn = dbHelper.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                configs.add(mapResultSetToConfig(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return configs;
    }

    public Configuration findByName(String name) {
        String sql = "SELECT * FROM Configuration WHERE name = ?";
        try (Connection conn = dbHelper.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapResultSetToConfig(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Configuration mapResultSetToConfig(ResultSet rs) throws SQLException {
        Configuration c = new Configuration();
        c.setConfigId(rs.getInt("configId"));
        c.setName(rs.getString("name"));
        c.setLanguage(rs.getString("language"));
        c.setCompilerPath(rs.getString("compilerPath"));
        c.setCompileArgs(rs.getString("compileArgs"));
        c.setSourceFilename(rs.getString("sourceFilename"));
        c.setRunCommand(rs.getString("runCommand"));
        c.setInterpreted(rs.getInt("isInterpreted") == 1);
        c.setTimeoutSeconds(rs.getInt("timeoutSeconds"));
        return c;
    }

    public void delete(int id) {
        String sql = "DELETE FROM Configuration WHERE configId = ?";
        try (Connection conn = dbHelper.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}