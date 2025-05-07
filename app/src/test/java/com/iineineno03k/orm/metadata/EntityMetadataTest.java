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
} 