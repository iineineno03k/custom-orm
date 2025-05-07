package com.iineineno03k.orm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.iineineno03k.orm.testentity.TestEntity;

public class EntityManagerTest {
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // テスト用のDBコネクションの設定
        DatabaseConfig config = new DatabaseConfig("jdbc:h2:mem:test", "sa", "");
        entityManager = new EntityManager(config);
    }

    @Test
    void shouldSaveAndRetrieveEntity() {
        // テスト用のエンティティを作成
        TestEntity entity = new TestEntity();
        entity.setId(1L);
        entity.setName("Test Entity");
        entity.setCode("TEST001");
        entity.setDescription("Test Description");
        entity.setActive(true);

        // エンティティを保存
        entityManager.save(entity);

        // IDによってエンティティを取得
        TestEntity retrieved = entityManager.findById(TestEntity.class, 1L);

        // 検証
        assertNotNull(retrieved);
        assertEquals(1L, retrieved.getId());
        assertEquals("Test Entity", retrieved.getName());
        assertEquals("TEST001", retrieved.getCode());
        assertEquals("Test Description", retrieved.getDescription());
        assertTrue(retrieved.isActive());
    }
}