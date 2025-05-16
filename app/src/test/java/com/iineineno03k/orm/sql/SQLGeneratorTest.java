package com.iineineno03k.orm.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.iineineno03k.orm.testentity.TestEntity;

public class SQLGeneratorTest {
    
    @Test
    void mySQLGeneratorShouldCreateCorrectSelectSQL() {
        // Arrange
        SQLGenerator sqlGenerator = SQLGeneratorFactory.createGenerator("MySQL");
        
        // Act
        String sql = sqlGenerator.createSelectSQL(TestEntity.class, "id");
        
        // Assert
        assertEquals("SELECT * FROM test_entities WHERE id = ?", sql);
    }
    
    @Test
    void mySQLGeneratorShouldCreateCorrectInsertSQL() {
        // Arrange
        SQLGenerator sqlGenerator = SQLGeneratorFactory.createGenerator("MySQL");
        
        // Act
        String sql = sqlGenerator.createInsertSQL(TestEntity.class);
        
        // Assert
        assertEquals("INSERT INTO test_entities (id, name, code, description, active) VALUES (?, ?, ?, ?, ?)", sql);
    }
    
    @Test
    void postgresGeneratorShouldCreateCorrectSelectSQL() {
        // Arrange
        SQLGenerator sqlGenerator = SQLGeneratorFactory.createGenerator("PostgreSQL");
        
        // Act
        String sql = sqlGenerator.createSelectSQL(TestEntity.class, "id");
        
        // Assert
        assertEquals("SELECT * FROM test_entities WHERE id = $1", sql);
    }
    
    @Test
    void oracleGeneratorShouldCreateCorrectSelectSQL() {
        // Arrange
        SQLGenerator sqlGenerator = SQLGeneratorFactory.createGenerator("Oracle");
        
        // Act
        String sql = sqlGenerator.createSelectSQL(TestEntity.class, "id");
        
        // Assert
        assertEquals("SELECT * FROM test_entities WHERE id = :1", sql);
    }
    
    @Test
    void shouldMapJavaTypesToSQLTypesCorrectly() {
        // Arrange
        SQLGenerator mySqlGenerator = SQLGeneratorFactory.createGenerator("MySQL");
        SQLGenerator postgresGenerator = SQLGeneratorFactory.createGenerator("PostgreSQL");
        SQLGenerator oracleGenerator = SQLGeneratorFactory.createGenerator("Oracle");
        
        // Act & Assert
        // MySQL
        assertEquals("INT", mySqlGenerator.mapJavaTypeToSQLType(Integer.class));
        assertEquals("VARCHAR(255)", mySqlGenerator.mapJavaTypeToSQLType(String.class));
        assertEquals("TINYINT(1)", mySqlGenerator.mapJavaTypeToSQLType(Boolean.class));
        
        // PostgreSQL
        assertEquals("INTEGER", postgresGenerator.mapJavaTypeToSQLType(Integer.class));
        assertEquals("VARCHAR(255)", postgresGenerator.mapJavaTypeToSQLType(String.class));
        assertEquals("BOOLEAN", postgresGenerator.mapJavaTypeToSQLType(Boolean.class));
        
        // Oracle
        assertEquals("NUMBER(10)", oracleGenerator.mapJavaTypeToSQLType(Integer.class));
        assertEquals("VARCHAR2(255)", oracleGenerator.mapJavaTypeToSQLType(String.class));
        assertEquals("NUMBER(1)", oracleGenerator.mapJavaTypeToSQLType(Boolean.class));
    }
    
    @Test
    void shouldCreateTableSQL() {
        // Arrange
        SQLGenerator mySqlGenerator = SQLGeneratorFactory.createGenerator("MySQL");
        
        // Act
        String sql = mySqlGenerator.createTableSQL(TestEntity.class);
        
        // Assert
        assertNotNull(sql);
        assertTrue(sql.startsWith("CREATE TABLE"));
        assertTrue(sql.contains("id"));
        assertTrue(sql.contains("name"));
        assertTrue(sql.contains("code"));
        assertTrue(sql.contains("description"));
        assertTrue(sql.contains("active"));
    }
} 