package com.harinezumi_dev.battleRoyaleHD.utils;

import com.harinezumi_dev.battleRoyaleHD.BattleRoyaleHD;
import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PersistenceService {
    private final BattleRoyaleHD plugin;
    private Connection connection;

    public PersistenceService(BattleRoyaleHD plugin) {
        this.plugin = plugin;
        initDatabase();
    }

    private void initDatabase() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            String url = "jdbc:sqlite:" + dataFolder.getAbsolutePath() + "/kits.db";
            connection = DriverManager.getConnection(url);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS kits (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "owner TEXT NOT NULL," +
                    "name TEXT NOT NULL," +
                    "data TEXT NOT NULL," +
                    "created_at INTEGER NOT NULL," +
                    "UNIQUE(owner, name)" +
                    ")"
                );
            }

            plugin.getLogger().info("Database initialized successfully");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
        }
    }

    public void saveKit(UUID owner, String name, String data) {
        CompletableFuture.runAsync(() -> {
            try {
                String sql = "INSERT OR REPLACE INTO kits (owner, name, data, created_at) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, owner.toString());
                    stmt.setString(2, name);
                    stmt.setString(3, data);
                    stmt.setLong(4, System.currentTimeMillis());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to save kit: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<String> loadKit(UUID owner, String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String sql;
                if ("*".equals(name)) {
                    sql = "SELECT data FROM kits WHERE owner = ? LIMIT 1";
                } else {
                    sql = "SELECT data FROM kits WHERE owner = ? AND name = ?";
                }

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, owner.toString());
                    if (!"*".equals(name)) {
                        stmt.setString(2, name);
                    }

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return rs.getString("data");
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to load kit: " + e.getMessage());
            }
            return null;
        });
    }

    public CompletableFuture<ResultSet> getAllKits() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String sql = "SELECT owner, name, data FROM kits";
                Statement stmt = connection.createStatement();
                return stmt.executeQuery(sql);
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to load all kits: " + e.getMessage());
                return null;
            }
        });
    }

    public void deleteKit(UUID owner, String name) {
        CompletableFuture.runAsync(() -> {
            try {
                String sql = "DELETE FROM kits WHERE owner = ? AND name = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, owner.toString());
                    stmt.setString(2, name);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to delete kit: " + e.getMessage());
            }
        });
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to close database: " + e.getMessage());
        }
    }
}