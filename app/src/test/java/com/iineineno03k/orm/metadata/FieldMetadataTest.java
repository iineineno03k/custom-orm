package com.iineineno03k.orm.metadata;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.iineineno03k.orm.annotation.Column;
import com.iineineno03k.orm.annotation.Id;

public class FieldMetadataTest {

    @Nested
    class ConstructorTests {
        @ParameterizedTest
        @CsvSource({
            // フィールド名, カラム名, nullable, unique
            "id, id, true, false",           // デフォルト値
            "name, custom_name, false, true", // カスタム名、nullable=false、unique=true
            "code, code, false, false",      // nullable=falseのみ
            "description, description, true, true" // unique=trueのみ
        })
        void shouldHandleColumnAnnotation(String fieldName, String expectedColumnName, boolean expectedNullable, boolean expectedUnique) throws NoSuchFieldException {
            class TestClass {
                @Column(name = "custom_name", nullable = false, unique = true)
                private String name;

                @Column(nullable = false)
                private String code;

                @Column(unique = true)
                private String description;

                private String id;
            }

            FieldMetadata metadata = createFieldMetadata(TestClass.class, fieldName);
            assertEquals(expectedColumnName, metadata.getColumnName());
            assertEquals(expectedNullable, metadata.isNullable());
            assertEquals(expectedUnique, metadata.isUnique());
        }

        @ParameterizedTest
        @ValueSource(strings = {"id", "name", "code", "description"})
        void shouldHandleIdAnnotation(String fieldName) throws NoSuchFieldException {
            class TestClass {
                @Id
                private Long id;

                @Id
                @Column(name = "custom_name")
                private String name;

                @Id
                @Column(nullable = false)
                private String code;

                @Id
                @Column(unique = true)
                private String description;
            }

            FieldMetadata metadata = createFieldMetadata(TestClass.class, fieldName);
            assertTrue(metadata.isId());
        }

        @ParameterizedTest
        @CsvSource({
            // フィールド名, アノテーションなしのデフォルト値
            "id, id, true, false",
            "name, name, true, false",
            "code, code, true, false",
            "description, description, true, false"
        })
        void shouldHandleNoAnnotation(String fieldName, String expectedColumnName, boolean expectedNullable, boolean expectedUnique) throws NoSuchFieldException {
            class TestClass {
                private Long id;
                private String name;
                private String code;
                private String description;
            }

            FieldMetadata metadata = createFieldMetadata(TestClass.class, fieldName);
            assertEquals(expectedColumnName, metadata.getColumnName());
            assertEquals(expectedNullable, metadata.isNullable());
            assertEquals(expectedUnique, metadata.isUnique());
            assertFalse(metadata.isId());
        }
    }

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

    private FieldMetadata createFieldMetadata(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        return new FieldMetadata(field);
    }
} 