package com.iineineno03k.orm.metadata;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.iineineno03k.orm.annotation.Column;
import com.iineineno03k.orm.annotation.Id;

public class FieldMetadataTest {

    @Nested
    class SqlTypeConversionTests {
        @Test
        void shouldConvertBasicTypes() throws NoSuchFieldException {
            class TestClass {
                private Long id;
                private String name;
                private boolean active;
            }

            assertEquals("BIGINT", createFieldMetadata(TestClass.class, "id").getSqlType());
            assertEquals("VARCHAR(255)", createFieldMetadata(TestClass.class, "name").getSqlType());
            assertEquals("BOOLEAN", createFieldMetadata(TestClass.class, "active").getSqlType());
        }

        @Test
        void shouldConvertNumericTypes() throws NoSuchFieldException {
            class TestClass {
                private Integer intValue;
                private Double doubleValue;
                private Float floatValue;
                private Short shortValue;
                private Byte byteValue;
            }

            assertEquals("INTEGER", createFieldMetadata(TestClass.class, "intValue").getSqlType());
            assertEquals("DOUBLE", createFieldMetadata(TestClass.class, "doubleValue").getSqlType());
            assertEquals("FLOAT", createFieldMetadata(TestClass.class, "floatValue").getSqlType());
            assertEquals("SMALLINT", createFieldMetadata(TestClass.class, "shortValue").getSqlType());
            assertEquals("TINYINT", createFieldMetadata(TestClass.class, "byteValue").getSqlType());
        }

        @Test
        void shouldConvertTemporalTypes() throws NoSuchFieldException {
            class TestClass {
                private LocalDate date;
                private LocalTime time;
                private LocalDateTime dateTime;
            }

            assertEquals("DATE", createFieldMetadata(TestClass.class, "date").getSqlType());
            assertEquals("TIME", createFieldMetadata(TestClass.class, "time").getSqlType());
            assertEquals("TIMESTAMP", createFieldMetadata(TestClass.class, "dateTime").getSqlType());
        }

        @Test
        void shouldHandleUnsupportedType() throws NoSuchFieldException {
            class TestClass {
                private Object unsupported;
            }

            assertThrows(IllegalArgumentException.class, () -> {
                createFieldMetadata(TestClass.class, "unsupported").getSqlType();
            });
        }
    }

    @Nested
    class AnnotationTests {
        @Test
        void shouldHandleIdAnnotation() throws NoSuchFieldException {
            class TestClass {
                @Id
                private Long id;
            }

            FieldMetadata metadata = createFieldMetadata(TestClass.class, "id");
            assertTrue(metadata.isId());
        }

        @Test
        void shouldHandleColumnAnnotation() throws NoSuchFieldException {
            class TestClass {
                @Column(name = "custom_name", nullable = false, unique = true)
                private String name;
            }

            FieldMetadata metadata = createFieldMetadata(TestClass.class, "name");
            assertEquals("custom_name", metadata.getColumnName());
            assertFalse(metadata.isNullable());
            assertTrue(metadata.isUnique());
        }
    }

    private FieldMetadata createFieldMetadata(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        return new FieldMetadata(field);
    }
} 