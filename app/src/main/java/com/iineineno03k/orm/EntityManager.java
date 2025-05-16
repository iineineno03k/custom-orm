package com.iineineno03k.orm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.iineineno03k.orm.sql.SQLGenerator;
import com.iineineno03k.orm.sql.SQLGeneratorFactory;

public class EntityManager {
    private DatabaseConfig config;
    private SQLGenerator sqlGenerator;
    // クラスごとに別々のストレージを持つようにする
    private static final Map<Class<?>, Map<Long, Object>> entityStorage = new HashMap<>();

    public EntityManager(DatabaseConfig config) {
        this.config = config;
        this.sqlGenerator = SQLGeneratorFactory.createGenerator(config.getDatabaseType());
    }

    public void save(Object entity) {
        try {
            Class<?> entityClass = entity.getClass();
            Long id = (Long) entityClass.getMethod("getId").invoke(entity);
            
            // エンティティタイプのストレージを取得、なければ作成
            Map<Long, Object> classStorage = entityStorage.computeIfAbsent(entityClass, k -> new HashMap<>());
            classStorage.put(id, entity);
            
            // 実際のDBに接続する場合はSQLを発行する
            // String sql = sqlGenerator.createInsertSQL(entity.getClass());
            // System.out.println("Generated SQL: " + sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save entity", e);
        }
    }

    public <T> T findById(Class<T> entityClass, Long id) {
        // エンティティタイプのストレージを取得
        Map<Long, Object> classStorage = entityStorage.get(entityClass);
        if (classStorage == null) {
            return null;
        }
        
        Object entity = classStorage.get(id);
        if (entity != null && entityClass.isInstance(entity)) {
            return entityClass.cast(entity);
        }
        
        // 実際のDBに接続する場合はSQLを発行する
        // String sql = sqlGenerator.createSelectSQL(entityClass, "id");
        // System.out.println("Generated SQL: " + sql);
        
        return null;
    }
    
    /**
     * 指定されたエンティティタイプのすべてのインスタンスを取得する
     * 
     * @param <T> エンティティの型
     * @param entityClass エンティティクラス
     * @return エンティティのリスト
     */
    public <T> List<T> findAll(Class<T> entityClass) {
        // エンティティタイプのストレージを取得
        Map<Long, Object> classStorage = entityStorage.get(entityClass);
        if (classStorage == null) {
            return new ArrayList<>();
        }
        
        // エンティティをリストに変換して返す
        return classStorage.values().stream()
                .map(entity -> entityClass.cast(entity))
                .collect(Collectors.toList());
        
        // 実際のDBに接続する場合はSQLを発行する
        // String sql = sqlGenerator.createSelectAllSQL(entityClass);
        // System.out.println("Generated SQL: " + sql);
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
