package com.iineineno03k.orm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.iineineno03k.orm.sql.DatabaseType;

/**
 * DatabaseConfigのテストクラス
 */
@DisplayName("DatabaseConfig のテスト")
public class DatabaseConfigTest {
    
    private static final String URL = "jdbc:mysql://localhost:3306/testdb";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpass";
    
    @Nested
    @DisplayName("enum を使用するコンストラクタのテスト")
    class EnumConstructorTest {
        @Test
        @DisplayName("デフォルトのデータベースタイプはMySQLである")
        void shouldUseDefaultDatabaseType() {
            DatabaseConfig config = new DatabaseConfig(URL, USERNAME, PASSWORD);
            assertEquals(DatabaseType.MYSQL, config.getDatabaseType());
        }
        
        @ParameterizedTest
        @EnumSource(DatabaseType.class)
        @DisplayName("すべてのデータベースタイプが受け入れられる")
        void shouldAcceptAllDatabaseTypes(DatabaseType type) {
            DatabaseConfig config = new DatabaseConfig(URL, USERNAME, PASSWORD, type);
            assertEquals(type, config.getDatabaseType());
            assertEquals(URL, config.getUrl());
            assertEquals(USERNAME, config.getUsername());
            assertEquals(PASSWORD, config.getPassword());
        }
        
        @Test
        @DisplayName("nullのデータベースタイプに対して例外を投げる")
        void shouldThrowExceptionForNullDatabaseType() {
            DatabaseType nullType = null;
            assertThrows(IllegalArgumentException.class, () -> {
                new DatabaseConfig(URL, USERNAME, PASSWORD, nullType);
            });
        }
    }
    
    @Nested
    @DisplayName("文字列を使用するコンストラクタのテスト (後方互換性)")
    class StringConstructorTest {
        @Test
        @DisplayName("文字列 'MySQL' は正しく変換される")
        void shouldAcceptMySQLDatabaseType() {
            DatabaseConfig config = new DatabaseConfig(URL, USERNAME, PASSWORD, "MySQL");
            assertEquals(DatabaseType.MYSQL, config.getDatabaseType());
        }
        
        @Test
        @DisplayName("文字列 'PostgreSQL' は正しく変換される")
        void shouldAcceptPostgreSQLDatabaseType() {
            DatabaseConfig config = new DatabaseConfig(URL, USERNAME, PASSWORD, "PostgreSQL");
            assertEquals(DatabaseType.POSTGRESQL, config.getDatabaseType());
        }
        
        @Test
        @DisplayName("文字列 'Oracle' は正しく変換される")
        void shouldAcceptOracleDatabaseType() {
            DatabaseConfig config = new DatabaseConfig(URL, USERNAME, PASSWORD, "Oracle");
            assertEquals(DatabaseType.ORACLE, config.getDatabaseType());
        }
        
        @Test
        @DisplayName("nullの文字列に対して例外を投げる")
        void shouldThrowExceptionForNullString() {
            assertThrows(IllegalArgumentException.class, () -> {
                new DatabaseConfig(URL, USERNAME, PASSWORD, (String)null);
            });
        }
        
        @Test
        @DisplayName("サポートされていないデータベースタイプの文字列に対して例外を投げる")
        void shouldThrowExceptionForUnsupportedDatabaseType() {
            assertThrows(IllegalArgumentException.class, () -> {
                new DatabaseConfig(URL, USERNAME, PASSWORD, "UnsupportedDB");
            });
        }
    }
    
    @Nested
    @DisplayName("getDatabaseTypeAsString メソッドのテスト")
    class GetDatabaseTypeAsStringTest {
        @ParameterizedTest
        @EnumSource(DatabaseType.class)
        @DisplayName("データベースタイプを正しく文字列に変換する")
        void shouldReturnCorrectStringRepresentation(DatabaseType type) {
            DatabaseConfig config = new DatabaseConfig(URL, USERNAME, PASSWORD, type);
            assertEquals(type.name(), config.getDatabaseTypeAsString());
        }
    }
} 