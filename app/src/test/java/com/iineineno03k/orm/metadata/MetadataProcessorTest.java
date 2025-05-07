package com.iineineno03k.orm.metadata;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.iineineno03k.orm.annotation.Column;
import com.iineineno03k.orm.annotation.Entity;
import com.iineineno03k.orm.annotation.Id;
import com.iineineno03k.orm.annotation.Table;
import com.iineineno03k.orm.testentity.EntityWithoutId;
import com.iineineno03k.orm.testentity.TestEntity;
import com.iineineno03k.orm.testentity.NonEntity;

public class MetadataProcessorTest {
    private final MetadataProcessor processor = new MetadataProcessor();

    @Nested
    class ProcessEntityTests {
        @Test
        void shouldProcessEntityWithAnnotations() {
            @Entity
            @Table(name = "test_entities")
            class TestEntity {
                @Id
                private Long id;

                @Column(name = "entity_name", nullable = false)
                private String name;

                @Column(nullable = false, unique = true)
                private String code;

                private String description;
            }

            EntityMetadata metadata = processor.processEntity(TestEntity.class);

            assertEquals("test_entities", metadata.getTableName());
            assertNotNull(metadata.getIdField());
            assertEquals("id", metadata.getIdField().getColumnName());
            assertTrue(metadata.getIdField().isId());

            Map<String, FieldMetadata> fields = metadata.getFieldMetadataMap();
            assertEquals("entity_name", fields.get("name").getColumnName());
            assertEquals("code", fields.get("code").getColumnName());
            assertFalse(fields.get("code").isNullable());
            assertTrue(fields.get("code").isUnique());
            assertEquals("description", fields.get("description").getColumnName());
        }

        @Test
        void shouldThrowExceptionForNonEntityClass() {
            class NonEntity {
                private Long id;
            }

            assertThrows(IllegalArgumentException.class, () -> {
                processor.processEntity(NonEntity.class);
            });
        }

        @Test
        void shouldThrowExceptionForEntityWithoutId() {
            @Entity
            class EntityWithoutId {
                private String name;
            }

            assertThrows(IllegalArgumentException.class, () -> {
                processor.processEntity(EntityWithoutId.class);
            });
        }

        @Test
        void shouldCacheMetadata() {
            @Entity
            class CacheTestEntity {
                @Id
                private Long id;
            }

            EntityMetadata metadata1 = processor.processEntity(CacheTestEntity.class);
            EntityMetadata metadata2 = processor.processEntity(CacheTestEntity.class);
            assertSame(metadata1, metadata2);
        }
    }

    @Nested
    class GenerateCreateTableSqlTests {
        @Test
        void shouldGenerateCompleteCreateTableSql() {
            @Entity
            @Table(name = "test_entities")
            class TestEntity {
                @Id
                private Long id;

                @Column(name = "entity_name", nullable = false)
                private String name;

                @Column(nullable = false, unique = true)
                private String code;

                private String description;
            }

            EntityMetadata metadata = processor.processEntity(TestEntity.class);
            String createTableSql = metadata.generateCreateTableSql();

            String expectedSql = """
                CREATE TABLE IF NOT EXISTS test_entities (
                    id BIGINT PRIMARY KEY,
                    entity_name VARCHAR(255) NOT NULL,
                    code VARCHAR(255) NOT NULL UNIQUE,
                    description VARCHAR(255)
                )""";

            assertEquals(expectedSql, createTableSql);
        }
    }
}