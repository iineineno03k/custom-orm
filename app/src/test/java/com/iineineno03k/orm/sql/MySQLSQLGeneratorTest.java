package com.iineineno03k.orm.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.iineineno03k.orm.testentity.TestEntity;

/**
 * MySQLSQLGeneratorのテストクラス.
 */
@DisplayName("MySQLSQLGenerator のテスト")
public class MySQLSQLGeneratorTest {
    
    private MySQLSQLGenerator generator;
    
    @BeforeEach
    void setUp() {
        generator = new MySQLSQLGenerator();
    }
    
    @Nested
    @DisplayName("createSelectSQL メソッドのテスト")
    class CreateSelectSQLTest {
        @Test
        @DisplayName("正しいSELECT SQLを生成する")
        void shouldCreateCorrectSelectSQL() {
            String sql = generator.createSelectSQL(TestEntity.class, "id");
            assertEquals("SELECT * FROM test_entities WHERE id = ?", sql);
        }
    }
    
    @Nested
    @DisplayName("createInsertSQL メソッドのテスト")
    class CreateInsertSQLTest {
        @Test
        @DisplayName("正しいINSERT SQLを生成する")
        void shouldCreateCorrectInsertSQL() {
            String sql = generator.createInsertSQL(TestEntity.class);
            assertEquals("INSERT INTO test_entities (id, name, code, description, active) VALUES (?, ?, ?, ?, ?)", sql);
        }
    }
    
    @Nested
    @DisplayName("createUpdateSQL メソッドのテスト")
    class CreateUpdateSQLTest {
        @Test
        @DisplayName("正しいUPDATE SQLを生成する")
        void shouldCreateCorrectUpdateSQL() {
            String sql = generator.createUpdateSQL(TestEntity.class, "id");
            assertEquals("UPDATE test_entities SET name = ?, code = ?, description = ?, active = ? WHERE id = ?", sql);
        }
    }
    
    @Nested
    @DisplayName("createDeleteSQL メソッドのテスト")
    class CreateDeleteSQLTest {
        @Test
        @DisplayName("正しいDELETE SQLを生成する")
        void shouldCreateCorrectDeleteSQL() {
            String sql = generator.createDeleteSQL(TestEntity.class, "id");
            assertEquals("DELETE FROM test_entities WHERE id = ?", sql);
        }
    }
    
    @Nested
    @DisplayName("mapJavaTypeToSQLType メソッドのテスト")
    class MapJavaTypeToSQLTypeTest {
        
        @DisplayName("JavaタイプからMySQLタイプへの変換を正しく行う")
        @ParameterizedTest(name = "{0} は {1} に変換される")
        @MethodSource("javaToMySQLTypeProvider")
        void shouldMapJavaTypesToMySQLTypes(Class<?> javaType, String expectedSqlType) {
            assertEquals(expectedSqlType, generator.mapJavaTypeToSQLType(javaType));
        }
        
        static Stream<Arguments> javaToMySQLTypeProvider() {
            return Stream.of(
                Arguments.of(String.class, "VARCHAR(255)"),
                Arguments.of(Integer.class, "INT"),
                Arguments.of(int.class, "INT"),
                Arguments.of(Long.class, "BIGINT"),
                Arguments.of(long.class, "BIGINT"),
                Arguments.of(Double.class, "DOUBLE"),
                Arguments.of(double.class, "DOUBLE"),
                Arguments.of(Float.class, "FLOAT"),
                Arguments.of(float.class, "FLOAT"),
                Arguments.of(Boolean.class, "TINYINT(1)"),
                Arguments.of(boolean.class, "TINYINT(1)"),
                Arguments.of(java.sql.Date.class, "DATE"),
                Arguments.of(java.sql.Time.class, "TIME"),
                Arguments.of(java.sql.Timestamp.class, "TIMESTAMP")
            );
        }
        
        @Test
        @DisplayName("未知のJava型に対してはデフォルト値を返す")
        void shouldHandleUnknownJavaType() {
            assertEquals("VARCHAR(255)", generator.mapJavaTypeToSQLType(Object.class));
        }
    }
    
    @Nested
    @DisplayName("createTableSQL メソッドのテスト")
    class CreateTableSQLTest {
        
        @Test
        @DisplayName("MySQL固有のテーブル作成SQLを生成する")
        void shouldCreateMySQLTableSQL() {
            String sql = generator.createTableSQL(TestEntity.class);
            
            assertNotNull(sql);
            assertTrue(sql.startsWith("CREATE TABLE"));
            
            // MySQL固有の要素が含まれていることを確認
            assertTrue(sql.contains("ENGINE=InnoDB"));
            assertTrue(sql.contains("DEFAULT CHARSET=utf8mb4"));
            
            // テーブル定義の要素を確認
            assertTrue(sql.contains("id BIGINT"));
            assertTrue(sql.contains("name VARCHAR(255)"));
            assertTrue(sql.contains("code VARCHAR(255)"));
            assertTrue(sql.contains("description VARCHAR(255)"));
            assertTrue(sql.contains("active TINYINT(1)"));
            assertTrue(sql.contains("PRIMARY KEY (id)"));
        }
        
        @Test
        @DisplayName("カラム制約を正しく反映する")
        void shouldRespectColumnConstraints() {
            String sql = generator.createTableSQL(TestEntity.class);
            
            // NOT NULL制約
            assertTrue(sql.contains("name VARCHAR(255) NOT NULL"));
            
            // UNIQUE制約
            assertTrue(sql.contains("code VARCHAR(255) NOT NULL UNIQUE"));
        }
    }
    
    @Nested
    @DisplayName("createPlaceholders メソッドのテスト")
    class CreatePlaceholdersTest {
        
        @DisplayName("プレースホルダーを正しく生成する")
        @ParameterizedTest(name = "{0}個のプレースホルダーを生成 => {1}")
        @MethodSource("placeholderProvider")
        void shouldCreateCorrectPlaceholders(int count, String expected) {
            assertEquals(expected, generator.createPlaceholders(count));
        }
        
        static Stream<Arguments> placeholderProvider() {
            return Stream.of(
                Arguments.of(1, "?"),
                Arguments.of(2, "?, ?"),
                Arguments.of(3, "?, ?, ?"),
                Arguments.of(5, "?, ?, ?, ?, ?")
            );
        }
    }
} 