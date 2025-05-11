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
 * PostgresSQLGeneratorのテストクラス
 */
@DisplayName("PostgresSQLGenerator のテスト")
public class PostgresSQLGeneratorTest {
    
    private PostgresSQLGenerator generator;
    
    @BeforeEach
    void setUp() {
        generator = new PostgresSQLGenerator();
    }
    
    @Nested
    @DisplayName("createSelectSQL メソッドのテスト")
    class CreateSelectSQLTest {
        @Test
        @DisplayName("正しいSELECT SQLを生成する")
        void shouldCreateCorrectSelectSQL() {
            String sql = generator.createSelectSQL(TestEntity.class, "id");
            assertEquals("SELECT * FROM test_entity WHERE id = $1", sql);
        }
    }
    
    @Nested
    @DisplayName("createInsertSQL メソッドのテスト")
    class CreateInsertSQLTest {
        @Test
        @DisplayName("正しいINSERT SQLを生成する")
        void shouldCreateCorrectInsertSQL() {
            String sql = generator.createInsertSQL(TestEntity.class);
            assertEquals("INSERT INTO test_entity (id, name, code, description, active) VALUES ($1, $2, $3, $4, $5)", sql);
        }
    }
    
    @Nested
    @DisplayName("createUpdateSQL メソッドのテスト")
    class CreateUpdateSQLTest {
        @Test
        @DisplayName("正しいUPDATE SQLを生成する")
        void shouldCreateCorrectUpdateSQL() {
            String sql = generator.createUpdateSQL(TestEntity.class, "id");
            
            // PostgreSQLの場合、プレースホルダーは$1, $2...のようになる
            assertTrue(sql.startsWith("UPDATE test_entity SET"));
            assertTrue(sql.contains("name = $1"));
            assertTrue(sql.contains("code = $2"));
            assertTrue(sql.contains("description = $3"));
            assertTrue(sql.contains("active = $4"));
            assertTrue(sql.contains("WHERE id = $5"));
        }
    }
    
    @Nested
    @DisplayName("createDeleteSQL メソッドのテスト")
    class CreateDeleteSQLTest {
        @Test
        @DisplayName("正しいDELETE SQLを生成する")
        void shouldCreateCorrectDeleteSQL() {
            String sql = generator.createDeleteSQL(TestEntity.class, "id");
            assertEquals("DELETE FROM test_entity WHERE id = $1", sql);
        }
    }
    
    @Nested
    @DisplayName("mapJavaTypeToSQLType メソッドのテスト")
    class MapJavaTypeToSQLTypeTest {
        
        @DisplayName("JavaタイプからPostgreSQLタイプへの変換を正しく行う")
        @ParameterizedTest(name = "{0} は {1} に変換される")
        @MethodSource("javaToPostgreSQLTypeProvider")
        void shouldMapJavaTypesToPostgreSQLTypes(Class<?> javaType, String expectedSqlType) {
            assertEquals(expectedSqlType, generator.mapJavaTypeToSQLType(javaType));
        }
        
        static Stream<Arguments> javaToPostgreSQLTypeProvider() {
            return Stream.of(
                Arguments.of(String.class, "VARCHAR(255)"),
                Arguments.of(Integer.class, "INTEGER"),
                Arguments.of(int.class, "INTEGER"),
                Arguments.of(Long.class, "BIGINT"),
                Arguments.of(long.class, "BIGINT"),
                Arguments.of(Double.class, "DOUBLE PRECISION"),
                Arguments.of(double.class, "DOUBLE PRECISION"),
                Arguments.of(Float.class, "REAL"),
                Arguments.of(float.class, "REAL"),
                Arguments.of(Boolean.class, "BOOLEAN"),
                Arguments.of(boolean.class, "BOOLEAN"),
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
        @DisplayName("PostgreSQL固有のテーブル作成SQLを生成する")
        void shouldCreatePostgreSQLTableSQL() {
            String sql = generator.createTableSQL(TestEntity.class);
            
            assertNotNull(sql);
            assertTrue(sql.startsWith("CREATE TABLE"));
            
            // PostgreSQL固有の要素が含まれていることを確認
            assertTrue(sql.endsWith(");"));
            
            // テーブル定義の要素を確認
            assertTrue(sql.contains("id BIGINT"));
            assertTrue(sql.contains("name VARCHAR(255)"));
            assertTrue(sql.contains("code VARCHAR(255)"));
            assertTrue(sql.contains("description VARCHAR(255)"));
            assertTrue(sql.contains("active BOOLEAN"));
            assertTrue(sql.contains("PRIMARY KEY (id)"));
            
            // MySQLの要素は含まれないことを確認
            assertTrue(!sql.contains("ENGINE=InnoDB"));
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
                Arguments.of(1, "$1"),
                Arguments.of(2, "$1, $2"),
                Arguments.of(3, "$1, $2, $3"),
                Arguments.of(5, "$1, $2, $3, $4, $5")
            );
        }
    }
} 