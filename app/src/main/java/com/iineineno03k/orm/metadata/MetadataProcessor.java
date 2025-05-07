package com.iineineno03k.orm.metadata;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.iineineno03k.orm.annotation.Entity;

public class MetadataProcessor {
    private final Map<Class<?>, EntityMetadata> metadataCache;

    public MetadataProcessor() {
        this.metadataCache = new HashMap<>();
    }

    public EntityMetadata processEntity(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Class " + entityClass.getName() + " is not an entity");
        }

        return metadataCache.computeIfAbsent(entityClass, this::createEntityMetadata);
    }

    private EntityMetadata createEntityMetadata(Class<?> entityClass) {
        EntityMetadata metadata = new EntityMetadata(entityClass);

        for (Field field : entityClass.getDeclaredFields()) {
            metadata.addFieldMetadata(field);
        }

        if (metadata.getIdField() == null) {
            throw new IllegalArgumentException("Entity " + entityClass.getName() + " must have an @Id field");
        }

        return metadata;
    }
} 