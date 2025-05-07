package com.iineineno03k.orm.metadata;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import com.iineineno03k.orm.annotation.Id;
import com.iineineno03k.orm.annotation.Table;

public class EntityMetadata {
    private final Class<?> entityClass;
    private final String tableName;
    private final Map<String, FieldMetadata> fieldMetadataMap;
    private FieldMetadata idField;

    public EntityMetadata(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.tableName = resolveTableName(entityClass);
        this.fieldMetadataMap = new LinkedHashMap<>();
        
        // フィールドの処理
        for (Field field : entityClass.getDeclaredFields()) {
            FieldMetadata metadata = new FieldMetadata(field);
            fieldMetadataMap.put(field.getName(), metadata);

            if (field.isAnnotationPresent(Id.class)) {
                if (idField != null) {
                    // 複数の@Idフィールドがある場合は最初のものを使用
                    continue;
                }
                idField = metadata;
            }
        }

        // @Idフィールドの存在チェック
        if (idField == null) {
            throw new IllegalArgumentException("Entity " + entityClass.getName() + " must have an @Id field");
        }
    }

    private String resolveTableName(Class<?> entityClass) {
        // @Tableアノテーションのname属性を優先
        if (entityClass.isAnnotationPresent(Table.class)) {
            String name = entityClass.getAnnotation(Table.class).name();
            if (!name.isEmpty()) {
                return name;
            }
        }
        // デフォルトはクラス名をスネークケースに変換
        String className = entityClass.getSimpleName();
        return className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public String generateCreateTableSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");

        boolean first = true;
        for (FieldMetadata metadata : fieldMetadataMap.values()) {
            if (!first) {
                sql.append(",\n");
            }
            first = false;

            sql.append("    ")
               .append(metadata.getColumnName())
               .append(" ")
               .append(metadata.getSqlType());

            if (metadata.isId()) {
                sql.append(" PRIMARY KEY");
            }
            if (!metadata.isNullable()) {
                sql.append(" NOT NULL");
            }
            if (metadata.isUnique()) {
                sql.append(" UNIQUE");
            }
        }

        sql.append("\n)");
        return sql.toString();
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public FieldMetadata getIdField() {
        return idField;
    }

    public Map<String, FieldMetadata> getFieldMetadataMap() {
        return fieldMetadataMap;
    }
}