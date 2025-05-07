package com.iineineno03k.orm.metadata;

import java.lang.reflect.Field;
import java.util.HashMap;
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
        this.fieldMetadataMap = new HashMap<>();
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
        return entityClass.getSimpleName().toLowerCase();
    }

    public void addFieldMetadata(Field field) {
        FieldMetadata metadata = new FieldMetadata(field);
        fieldMetadataMap.put(field.getName(), metadata);
        
        if (field.isAnnotationPresent(Id.class)) {
            idField = metadata;
        }
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