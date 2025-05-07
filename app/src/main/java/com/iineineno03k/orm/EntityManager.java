package com.iineineno03k.orm;

import java.util.HashMap;
import java.util.Map;

public class EntityManager {
    private DatabaseConfig config;
    private static final Map<Long, Object> storage = new HashMap<>();

    public EntityManager(DatabaseConfig config) {
        this.config = config;
    }

    public void save(Object entity) {
        try {
            Long id = (Long) entity.getClass().getMethod("getId").invoke(entity);
            storage.put(id, entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save entity", e);
        }
    }

    public <T> T findById(Class<T> entityClass, Long id) {
        Object entity = storage.get(id);
        if (entity != null && entityClass.isInstance(entity)) {
            return entityClass.cast(entity);
        }
        return null;
    }
}
