package com.iineineno03k.orm.sql;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.iineineno03k.orm.annotation.Column;
import com.iineineno03k.orm.annotation.Id;
import com.iineineno03k.orm.annotation.Table;

/**
 * MySQL用のSQLジェネレータ実装
 */
public class MySQLSQLGenerator extends AbstractSQLGenerator {
    
    private static final Map<Class<?>, String> TYPE_MAPPING = new HashMap<>();
    
    static {
        // MySQL用の型マッピングを初期化
        TYPE_MAPPING.put(String.class, "VARCHAR(255)");
        TYPE_MAPPING.put(Integer.class, "INT");
        TYPE_MAPPING.put(int.class, "INT");
        TYPE_MAPPING.put(Long.class, "BIGINT");
        TYPE_MAPPING.put(long.class, "BIGINT");
        TYPE_MAPPING.put(Double.class, "DOUBLE");
        TYPE_MAPPING.put(double.class, "DOUBLE");
        TYPE_MAPPING.put(Float.class, "FLOAT");
        TYPE_MAPPING.put(float.class, "FLOAT");
        TYPE_MAPPING.put(Boolean.class, "TINYINT(1)");
        TYPE_MAPPING.put(boolean.class, "TINYINT(1)");
        TYPE_MAPPING.put(Date.class, "DATE");
        TYPE_MAPPING.put(Time.class, "TIME");
        TYPE_MAPPING.put(Timestamp.class, "TIMESTAMP");
        TYPE_MAPPING.put(BigDecimal.class, "DECIMAL(19,4)");
    }
    
    @Override
    public String createSelectSQL(Class<?> entityClass, String idColumnName) {
        String tableName = getTableName(entityClass);
        return String.format("SELECT * FROM %s WHERE %s = ?", tableName, idColumnName);
    }
    
    @Override
    public String createInsertSQL(Class<?> entityClass) {
        String tableName = getTableName(entityClass);
        List<String> fieldNames = getFieldNames(entityClass);
        
        String columns = fieldNames.stream()
                .map(this::fieldToColumnName)
                .collect(Collectors.joining(", "));
        
        String placeholders = createPlaceholders(fieldNames.size());
        
        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);
    }
    
    @Override
    public String createUpdateSQL(Class<?> entityClass, String idColumnName) {
        String tableName = getTableName(entityClass);
        List<String> fieldNames = getFieldNames(entityClass);
        
        String setClause = fieldNames.stream()
                .filter(field -> !field.equals(idColumnName))
                .map(field -> fieldToColumnName(field) + " = ?")
                .collect(Collectors.joining(", "));
        
        return String.format("UPDATE %s SET %s WHERE %s = ?", tableName, setClause, idColumnName);
    }
    
    @Override
    public String createDeleteSQL(Class<?> entityClass, String idColumnName) {
        String tableName = getTableName(entityClass);
        return String.format("DELETE FROM %s WHERE %s = ?", tableName, idColumnName);
    }
    
    @Override
    public String mapJavaTypeToSQLType(Class<?> javaType) {
        String sqlType = TYPE_MAPPING.get(javaType);
        return sqlType != null ? sqlType : "VARCHAR(255)";
    }
    
    @Override
    protected String createPlaceholders(int count) {
        if (count <= 0) {
            return "";
        }
        
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                placeholders.append(", ");
            }
            placeholders.append("?");
        }
        
        return placeholders.toString();
    }
    
    @Override
    public String createTableSQL(Class<?> entityClass) {
        String tableName = determineTableName(entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(tableName).append(" (\n");
        
        Field[] fields = entityClass.getDeclaredFields();
        String primaryKey = null;
        
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            
            // 静的フィールドをスキップ
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            
            String columnDefinition = createColumnDefinition(field);
            sql.append("  ").append(columnDefinition);
            
            // Primary Key を特定
            if (field.isAnnotationPresent(Id.class)) {
                primaryKey = fieldToColumnName(field.getName());
            }
            
            // 最後のフィールドでなければコンマを追加
            if (i < fields.length - 1) {
                sql.append(",\n");
            } else {
                sql.append("\n");
            }
        }
        
        // Primary Key制約を追加
        if (primaryKey != null) {
            sql.append(", PRIMARY KEY (").append(primaryKey).append(")");
        }
        
        sql.append("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;");
        
        return sql.toString();
    }
    
    private String createColumnDefinition(Field field) {
        String columnName = fieldToColumnName(field.getName());
        String sqlType = mapJavaTypeToSQLType(field.getType());
        
        StringBuilder definition = new StringBuilder();
        definition.append(columnName).append(" ").append(sqlType);
        
        // アノテーションの処理
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            
            // NULL制約
            if (!column.nullable()) {
                definition.append(" NOT NULL");
            }
            
            // UNIQUE制約
            if (column.unique()) {
                definition.append(" UNIQUE");
            }
        }
        
        return definition.toString();
    }
    
    private String determineTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            return tableAnnotation.name();
        }
        return getTableName(entityClass);
    }
} 