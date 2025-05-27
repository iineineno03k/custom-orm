package com.iineineno03k.orm.sql;

/**
 * サポートされているデータベースタイプを定義するenum.
 */
public enum DatabaseType {
    MYSQL,
    POSTGRESQL,
    ORACLE;
    
    /**
     * 文字列表現からDatabaseTypeを取得
     * 
     * @param type データベースタイプの文字列表現
     * @return 対応するDatabaseType
     * @throws IllegalArgumentException サポートされていない文字列の場合
     */
    public static DatabaseType fromString(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Database type cannot be null or empty");
        }
        
        try {
            return DatabaseType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            
            throw new IllegalArgumentException("Unsupported database type: " + type);
        }
    }
} 