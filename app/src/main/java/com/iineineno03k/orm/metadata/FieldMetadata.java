package com.iineineno03k.orm.metadata;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.iineineno03k.orm.annotation.Column;
import com.iineineno03k.orm.annotation.Id;

public class FieldMetadata {
    private final Field field;
    private final boolean isId;
    private final String columnName;
    private final boolean nullable;
    private final boolean unique;

    public FieldMetadata(Field field) {
        this.field = field;
        this.isId = field.isAnnotationPresent(Id.class);
        
        Column column = field.getAnnotation(Column.class);
        this.columnName = column != null ? column.name() : field.getName();
        this.nullable = column == null || column.nullable();
        this.unique = column != null && column.unique();
    }

    public String getSqlType() {
        Class<?> type = field.getType();
        
        if (type == Long.class || type == long.class) {
            return "BIGINT";
        } else if (type == String.class) {
            return "VARCHAR(255)";
        } else if (type == boolean.class || type == Boolean.class) {
            return "BOOLEAN";
        } else if (type == Integer.class || type == int.class) {
            return "INTEGER";
        } else if (type == Double.class || type == double.class) {
            return "DOUBLE";
        } else if (type == Float.class || type == float.class) {
            return "FLOAT";
        } else if (type == Short.class || type == short.class) {
            return "SMALLINT";
        } else if (type == Byte.class || type == byte.class) {
            return "TINYINT";
        } else if (type == LocalDate.class) {
            return "DATE";
        } else if (type == LocalTime.class) {
            return "TIME";
        } else if (type == LocalDateTime.class) {
            return "TIMESTAMP";
        }
        
        throw new IllegalArgumentException("Unsupported type: " + type.getName());
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