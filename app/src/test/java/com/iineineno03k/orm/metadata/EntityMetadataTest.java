package com.iineineno03k.orm.metadata;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.iineineno03k.orm.annotation.Column;
import com.iineineno03k.orm.annotation.Entity;
import com.iineineno03k.orm.annotation.Id;
import com.iineineno03k.orm.annotation.Table;

public class EntityMetadataTest {

    @Nested
    class ConstructorTests {
        @ParameterizedTest
        @CsvSource({
            // クラス名, テーブル名
            "SimpleEntity, simple_entity",           // デフォルト（クラス名をスネークケースに変換）
            "ComplexEntityName, complex_entity_name", // 複数の単語を含むクラス名
            "EntityWithTable, custom_table_name",    // @Tableアノテーションあり
            "EntityWithEmptyTable, entity_with_empty_table" // @Tableアノテーションのnameが空
        })
        void shouldResolveTableName(String className, String expectedTableName) {
            @Entity
            @Table(name = "custom_table_name")
            class EntityWithTable {
                @Id
                private Long id;
            }

            @Entity
            @Table(name = "")
            class EntityWithEmptyTable {
                @Id
                private Long id;
            }

            @Entity
            class SimpleEntity {
                @Id
                private Long id;
            }

            @Entity
            class ComplexEntityName {
                @Id
                private Long id;
            }

            Class<?> entityClass = switch (className) {
                case "SimpleEntity" -> SimpleEntity.class;
                case "ComplexEntityName" -> ComplexEntityName.class;
                case "EntityWithTable" -> EntityWithTable.class;
                case "EntityWithEmptyTable" -> EntityWithEmptyTable.class;
                default -> throw new IllegalArgumentException("Unknown class name: " + className);
            };

            EntityMetadata metadata = new EntityMetadata(entityClass);
            assertEquals(expectedTableName, metadata.getTableName());
        }

        @ParameterizedTest
        @CsvSource({
            // フィールド名, カラム名, nullable, unique, isId
            "id, id, true, false, true",           // @Idのみ
            "name, custom_name, false, true, false", // @Column(name, nullable=false, unique=true)
            "code, code, false, false, false",      // @Column(nullable=false)
            "description, description, true, true, false", // @Column(unique=true)
            "active, active, true, false, false"    // アノテーションなし
        })
        void shouldProcessFields(String fieldName, String expectedColumnName, boolean expectedNullable, boolean expectedUnique, boolean expectedIsId) {
            @Entity
            class TestEntity {
                @Id
                private Long id;

                @Column(name = "custom_name", nullable = false, unique = true)
                private String name;

                @Column(nullable = false)
                private String code;

                @Column(unique = true)
                private String description;

                private boolean active;
            }

            EntityMetadata metadata = new EntityMetadata(TestEntity.class);
            Map<String, FieldMetadata> fields = metadata.getFieldMetadataMap();
            FieldMetadata field = fields.get(fieldName);

            assertNotNull(field, "Field " + fieldName + " should exist");
            assertEquals(expectedColumnName, field.getColumnName());
            assertEquals(expectedNullable, field.isNullable());
            assertEquals(expectedUnique, field.isUnique());
            assertEquals(expectedIsId, field.isId());
        }

        @Test
        void shouldThrowExceptionForEntityWithoutId() {
            @Entity
            class EntityWithoutId {
                private String name;
            }

            assertThrows(IllegalArgumentException.class, () -> {
                new EntityMetadata(EntityWithoutId.class);
            });
        }

        @Test
        void shouldHandleMultipleIdFields() {
            @Entity
            class EntityWithMultipleIds {
                @Id
                private Long id1;

                @Id
                private Long id2;
            }

            EntityMetadata metadata = new EntityMetadata(EntityWithMultipleIds.class);
            assertNotNull(metadata.getIdField());
            assertEquals("id1", metadata.getIdField().getColumnName());
        }

        @Test
        void shouldHandleEmptyEntity() {
            @Entity
            class EmptyEntity {
                @Id
                private Long id;
            }

            EntityMetadata metadata = new EntityMetadata(EmptyEntity.class);
            assertEquals(1, metadata.getFieldMetadataMap().size());
            assertNotNull(metadata.getIdField());
        }
    }

    @Nested
    class CreateTableSqlTests {
        @Test
        void shouldGenerateBasicCreateTableSql() {
            @Entity
            class BasicEntity {
                @Id
                private Long id;
            }

            EntityMetadata metadata = new EntityMetadata(BasicEntity.class);
            String expected = """
                CREATE TABLE IF NOT EXISTS basic_entity (
                    id BIGINT PRIMARY KEY
                )""";

            assertEquals(expected, metadata.generateCreateTableSql());
        }

        @Test
        void shouldGenerateCreateTableSqlWithAllConstraints() {
            @Entity
            class ComplexEntity {
                @Id
                private Long id;

                @Column(nullable = false, unique = true)
                private String name;

                @Column(nullable = false)
                private Integer age;

                @Column(unique = true)
                private String email;
            }

            EntityMetadata metadata = new EntityMetadata(ComplexEntity.class);
            String expected = """
                CREATE TABLE IF NOT EXISTS complex_entity (
                    id BIGINT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL UNIQUE,
                    age INTEGER NOT NULL,
                    email VARCHAR(255) UNIQUE
                )""";

            assertEquals(expected, metadata.generateCreateTableSql());
        }

        @Test
        void shouldGenerateCreateTableSqlWithCustomTableName() {
            @Entity
            @Table(name = "custom_users")
            class UserEntity {
                @Id
                private Long id;

                @Column(nullable = false)
                private String username;
            }

            EntityMetadata metadata = new EntityMetadata(UserEntity.class);
            String expected = """
                CREATE TABLE IF NOT EXISTS custom_users (
                    id BIGINT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL
                )""";

            assertEquals(expected, metadata.generateCreateTableSql());
        }

        @Test
        void shouldGenerateCreateTableSqlWithAllFieldTypes() {
            @Entity
            class AllTypesEntity {
                @Id
                private Long id;

                private String text;
                private Integer number;
                private Double decimal;
                private Boolean flag;
            }

            EntityMetadata metadata = new EntityMetadata(AllTypesEntity.class);
            String expected = """
                CREATE TABLE IF NOT EXISTS all_types_entity (
                    id BIGINT PRIMARY KEY,
                    text VARCHAR(255),
                    number INTEGER,
                    decimal DOUBLE,
                    flag BOOLEAN
                )""";

            assertEquals(expected, metadata.generateCreateTableSql());
        }

        @Test
        void shouldGenerateCreateTableSqlWithCustomColumnNames() {
            @Entity
            class CustomColumnEntity {
                @Id
                private Long id;

                @Column(name = "user_name")
                private String name;

                @Column(name = "user_age")
                private Integer age;
            }

            EntityMetadata metadata = new EntityMetadata(CustomColumnEntity.class);
            String expected = """
                CREATE TABLE IF NOT EXISTS custom_column_entity (
                    id BIGINT PRIMARY KEY,
                    user_name VARCHAR(255),
                    user_age INTEGER
                )""";

            assertEquals(expected, metadata.generateCreateTableSql());
        }
    }
} 