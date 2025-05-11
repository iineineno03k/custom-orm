package com.iineineno03k.orm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.iineineno03k.orm.sql.DatabaseType;
import com.iineineno03k.orm.testentity.TestEntity;

public class EntityManagerTest {
    private EntityManager entityManager;
    private EntityManager postgresEntityManager;
    private EntityManager oracleEntityManager;

    @BeforeEach
    void setUp() {
        // テスト用のDBコネクションの設定
        DatabaseConfig mysqlConfig = new DatabaseConfig("jdbc:h2:mem:test", "sa", "", DatabaseType.MYSQL);
        entityManager = new EntityManager(mysqlConfig);
        
        // PostgreSQL用のエンティティマネージャー
        DatabaseConfig postgresConfig = new DatabaseConfig("jdbc:h2:mem:test", "sa", "", DatabaseType.POSTGRESQL);
        postgresEntityManager = new EntityManager(postgresConfig);
        
        // Oracle用のエンティティマネージャー
        DatabaseConfig oracleConfig = new DatabaseConfig("jdbc:h2:mem:test", "sa", "", DatabaseType.ORACLE);
        oracleEntityManager = new EntityManager(oracleConfig);
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
    
    @Test
    void shouldGenerateCorrectCreateTableSQL() {
        // MySQLのテーブル作成SQL
        String mysqlSql = entityManager.generateCreateTableSQL(TestEntity.class);
        assertNotNull(mysqlSql);
        assertTrue(mysqlSql.startsWith("CREATE TABLE"));
        assertTrue(mysqlSql.contains("ENGINE=InnoDB"));
        
        // PostgreSQLのテーブル作成SQL
        String postgresSql = postgresEntityManager.generateCreateTableSQL(TestEntity.class);
        assertNotNull(postgresSql);
        assertTrue(postgresSql.startsWith("CREATE TABLE"));
        assertTrue(postgresSql.contains(");"));  // PostgreSQL特有の終端
        
        // Oracleのテーブル作成SQL
        String oracleSql = oracleEntityManager.generateCreateTableSQL(TestEntity.class);
        assertNotNull(oracleSql);
        assertTrue(oracleSql.startsWith("CREATE TABLE"));
        assertTrue(!oracleSql.contains("ENGINE"));  // Oracleにはエンジン指定がない
    }
}