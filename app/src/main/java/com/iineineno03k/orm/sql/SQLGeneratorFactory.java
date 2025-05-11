package com.iineineno03k.orm.sql;

/**
 * SQLジェネレーターを生成するファクトリークラス
 */
public class SQLGeneratorFactory {
    
    /**
     * データベースタイプに応じたSQLジェネレーターを生成する
     * 
     * @param databaseType データベースタイプ
     * @return 対応するSQLGenerator実装
     * @throws IllegalArgumentException サポートされていないデータベースタイプの場合
     */
    public static SQLGenerator createGenerator(DatabaseType databaseType) {
        if (databaseType == null) {
            throw new IllegalArgumentException("Database type cannot be null");
        }
        
        switch (databaseType) {
            case MYSQL:
                return new MySQLSQLGenerator();
            case POSTGRESQL:
                return new PostgresSQLGenerator();
            case ORACLE:
                return new OracleSQLGenerator();
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }
    
    /**
     * 文字列からデータベースタイプを指定してSQLジェネレーターを生成する
     * 後方互換性のために残してある
     * 
     * @param databaseType データベースタイプの文字列 (MySQL, PostgreSQL, Oracle)
     * @return 対応するSQLGenerator実装
     * @throws IllegalArgumentException サポートされていないデータベースタイプの場合
     */
    public static SQLGenerator createGenerator(String databaseType) {
        return createGenerator(DatabaseType.fromString(databaseType));
    }
} 