package com.iineineno03k.orm;

import com.iineineno03k.orm.sql.DatabaseType;

public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    private DatabaseType databaseType;

    public DatabaseConfig(String url, String username, String password) {
        this(url, username, password, DatabaseType.MYSQL); // デフォルトはMySQLとする
    }

    /**
     * 文字列からデータベースタイプを指定してDatabaseConfigを作成する
     * 後方互換性のために提供
     */
    public DatabaseConfig(String url, String username, String password, String databaseTypeStr) {
        this(url, username, password, DatabaseType.fromString(databaseTypeStr));
    }

    /**
     * DatabaseType enumを使用して新しいDatabaseConfigを作成する
     */
    public DatabaseConfig(String url, String username, String password, DatabaseType databaseType) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.databaseType = databaseType;

        if (databaseType == null) {
            throw new IllegalArgumentException("Database type cannot be null");
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * データベースタイプを取得する
     * 
     * @return データベースタイプ
     */
    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    /**
     * データベースタイプを文字列として取得する
     * 後方互換性のために提供
     * 
     * @return データベースタイプの文字列表現
     */
    public String getDatabaseTypeAsString() {
        return databaseType.name();
    }
}
