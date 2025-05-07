package com.iineineno03k.orm.metadata;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import com.iineineno03k.orm.testentity.EntityWithoutId;
import com.iineineno03k.orm.testentity.TestEntity;
import com.iineineno03k.orm.testentity.NonEntity;

public class MetadataProcessorTest {
    private final MetadataProcessor processor = new MetadataProcessor();

    @Test
    void shouldProcessEntityWithAnnotations() {
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
        assertThrows(IllegalArgumentException.class, () -> {
            processor.processEntity(NonEntity.class);
        });
    }

    @Test
    void shouldThrowExceptionForEntityWithoutId() {
        assertThrows(IllegalArgumentException.class, () -> {
            processor.processEntity(EntityWithoutId.class);
        });
    }

    @Test
    void shouldCacheMetadata() {
        EntityMetadata metadata1 = processor.processEntity(TestEntity.class);
        EntityMetadata metadata2 = processor.processEntity(TestEntity.class);
        assertSame(metadata1, metadata2);
    }
}