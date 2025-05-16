package com.iineineno03k.orm.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.iineineno03k.orm.testentity.TestEntity;

/**
 * AbstractSQLGeneratorのテストクラス
 */
public class AbstractSQLGeneratorTest {
    
    private TestSQLGenerator generator;
    
    @BeforeEach
    void setUp() {
        generator = new TestSQLGenerator();
    }
    
    @Test
    void shouldGetCorrectTableName() {
        String tableName = generator.getTableNameForTest(TestEntity.class);
        assertEquals("test_entities", tableName);
    }
    
    @Test
    void shouldConvertCamelCaseToSnakeCase() {
        assertEquals("test_entity", generator.camelToSnakeForTest("testEntity"));
        assertEquals("user_profile", generator.camelToSnakeForTest("userProfile"));
        assertEquals("employee_data", generator.camelToSnakeForTest("employeeData"));
        assertEquals("person_address_info", generator.camelToSnakeForTest("personAddressInfo"));
    }
    
    @Test
    void shouldGetCorrectFieldNames() {
        List<String> fieldNames = generator.getFieldNamesForTest(TestEntity.class);
        
        assertNotNull(fieldNames);
        assertTrue(fieldNames.contains("id"));
        assertTrue(fieldNames.contains("name"));
        assertTrue(fieldNames.contains("code"));
        assertTrue(fieldNames.contains("description"));
        assertTrue(fieldNames.contains("active"));
        
        // TestEntityのフィールド数が5であることを確認
        assertEquals(5, fieldNames.size());
    }
    
    @Test
    void shouldConvertFieldToColumnName() {
        assertEquals("id", generator.fieldToColumnNameForTest("id"));
        assertEquals("user_name", generator.fieldToColumnNameForTest("userName"));
        assertEquals("employee_id", generator.fieldToColumnNameForTest("employeeId"));
        assertEquals("home_address", generator.fieldToColumnNameForTest("homeAddress"));
    }
    
    /**
     * テスト用の具体的なSQLGenerator実装
     */
    private static class TestSQLGenerator extends AbstractSQLGenerator {
        
        @Override
        public String createSelectSQL(Class<?> entityClass, String idColumnName) {
            return "SELECT * FROM " + getTableName(entityClass);
        }
        
        @Override
        public String createInsertSQL(Class<?> entityClass) {
            return "INSERT INTO " + getTableName(entityClass);
        }
        
        @Override
        public String createUpdateSQL(Class<?> entityClass, String idColumnName) {
            return "UPDATE " + getTableName(entityClass);
        }
        
        @Override
        public String createDeleteSQL(Class<?> entityClass, String idColumnName) {
            return "DELETE FROM " + getTableName(entityClass);
        }
        
        @Override
        public String mapJavaTypeToSQLType(Class<?> javaType) {
            return "VARCHAR(255)";
        }
        
        @Override
        public String createTableSQL(Class<?> entityClass) {
            return "CREATE TABLE " + getTableName(entityClass);
        }
        
        @Override
        protected String createPlaceholders(int count) {
            return "?";
        }
        
        // テスト用の公開メソッド
        
        public String getTableNameForTest(Class<?> entityClass) {
            return getTableName(entityClass);
        }
        
        public String camelToSnakeForTest(String camel) {
            return camelToSnake(camel);
        }
        
        public List<String> getFieldNamesForTest(Class<?> entityClass) {
            return getFieldNames(entityClass);
        }
        
        public String fieldToColumnNameForTest(String fieldName) {
            return fieldToColumnName(fieldName);
        }
    }
} 