package com.iineineno03k.orm.metadata;

import java.lang.reflect.Field;

public class FieldMetadata {
    private final Field field;
    private final String columnName;
    private final boolean isId;
    private final boolean nullable;
    private final boolean unique;

    public FieldMetadata(Field field) {
        this.field = field;
        this.isId = field.isAnnotationPresent(com.iineineno03k.orm.annotation.Id.class);
        this.columnName = resolveColumnName(field);
        
        if (field.isAnnotationPresent(com.iineineno03k.orm.annotation.Column.class)) {
            com.iineineno03k.orm.annotation.Column column = field.getAnnotation(com.iineineno03k.orm.annotation.Column.class);
            this.nullable = column.nullable();
            this.unique = column.unique();
        } else {
            this.nullable = true;
            this.unique = false;
        }
    }

    private String resolveColumnName(Field field) {
        // @Columnアノテーションのname属性を優先
        if (field.isAnnotationPresent(com.iineineno03k.orm.annotation.Column.class)) {
            String name = field.getAnnotation(com.iineineno03k.orm.annotation.Column.class).name();
            if (!name.isEmpty()) {
                return name;
            }
        }
        // デフォルトはフィールド名をスネークケースに変換
        return field.getName().toLowerCase();
    }

    public Field getField() {
        return field;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isId() {
        return isId;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isUnique() {
        return unique;
    }
} 