package com.iineineno03k.orm;

import java.util.HashMap;
import java.util.Map;

import com.iineineno03k.orm.sql.SQLGenerator;
import com.iineineno03k.orm.sql.SQLGeneratorFactory;

public class EntityManager {
    private DatabaseConfig config;
    private SQLGenerator sqlGenerator;
    private static final Map<Long, Object> storage = new HashMap<>();

    public EntityManager(DatabaseConfig config) {
        this.config = config;
        this.sqlGenerator = SQLGeneratorFactory.createGenerator(config.getDatabaseType());
    }

    public void save(Object entity) {
        try {
            Long id = (Long) entity.getClass().getMethod("getId").invoke(entity);
            storage.put(id, entity);
            
            // 実際のDBに接続する場合はSQLを発行する
            // String sql = sqlGenerator.createInsertSQL(entity.getClass());
            // System.out.println("Generated SQL: " + sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save entity", e);
        }
    }

    public <T> T findById(Class<T> entityClass, Long id) {
        Object entity = storage.get(id);
        if (entity != null && entityClass.isInstance(entity)) {
            return entityClass.cast(entity);
        }
        
        // 実際のDBに接続する場合はSQLを発行する
        // String sql = sqlGenerator.createSelectSQL(entityClass, "id");
        // System.out.println("Generated SQL: " + sql);
        
        return null;
    }
    
    /**
     * テーブル作成SQLを生成する
     * 
     * @param entityClass エンティティクラス
     * @return 生成されたSQL
     */
    public String generateCreateTableSQL(Class<?> entityClass) {
        return sqlGenerator.createTableSQL(entityClass);
    }
}
