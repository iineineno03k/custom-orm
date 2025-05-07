package com.iineineno03k.orm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntityManagerTest {
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // テスト用のDBコネクションの設定
        DatabaseConfig config = new DatabaseConfig("jdbc:h2:mem:test", "sa", "");
        entityManager = new EntityManager(config);
    }

    @Test
    void shouldSaveAndRetrieveSimpleEntity() {
        // テスト用のエンティティを作成
        TestEntity entity = new TestEntity();
        entity.setId(1L);
        entity.setName("Test Entity");

        // エンティティを保存
        entityManager.save(entity);

        // IDによってエンティティを取得
        TestEntity retrieved = entityManager.findById(TestEntity.class, 1L);

        // 検証
        assertNotNull(retrieved);
        assertEquals(1L, retrieved.getId());
        assertEquals("Test Entity", retrieved.getName());
    }
}

// テスト用のエンティティクラス
class TestEntity {
    private Long id;
    private String name;

    // Getter, Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
