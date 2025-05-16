package com.iineineno03k.orm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.iineineno03k.orm.sql.DatabaseType;
import com.iineineno03k.orm.testentity.ProductEntity;
import com.iineineno03k.orm.testentity.TestEntity;
import com.iineineno03k.orm.testentity.UserEntity;

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
    void shouldSaveAndRetrieveMultipleEntities() {
        // TestEntityを作成して保存
        TestEntity testEntity1 = new TestEntity();
        testEntity1.setId(1L);
        testEntity1.setName("Test Entity 1");
        testEntity1.setCode("TEST001");
        testEntity1.setDescription("First Test Entity");
        testEntity1.setActive(true);
        
        TestEntity testEntity2 = new TestEntity();
        testEntity2.setId(2L);
        testEntity2.setName("Test Entity 2");
        testEntity2.setCode("TEST002");
        testEntity2.setDescription("Second Test Entity");
        testEntity2.setActive(false);
        
        // ProductEntityを作成して保存
        ProductEntity productEntity1 = new ProductEntity();
        productEntity1.setId(1L);
        productEntity1.setName("Product 1");
        productEntity1.setPrice(29.99);
        productEntity1.setStockQuantity(100);
        productEntity1.setCategory("Electronics");
        
        ProductEntity productEntity2 = new ProductEntity();
        productEntity2.setId(2L);
        productEntity2.setName("Product 2");
        productEntity2.setPrice(9.99);
        productEntity2.setStockQuantity(50);
        productEntity2.setCategory("Books");
        
        // UserEntityを作成して保存
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setId(1L);
        userEntity1.setUsername("user1");
        userEntity1.setEmail("user1@example.com");
        userEntity1.setCreatedAt(new Date());
        userEntity1.setActive(true);
        
        // すべてのエンティティを保存
        entityManager.save(testEntity1);
        entityManager.save(testEntity2);
        entityManager.save(productEntity1);
        entityManager.save(productEntity2);
        entityManager.save(userEntity1);
        
        // 各エンティティを取得して検証
        TestEntity retrievedTest1 = entityManager.findById(TestEntity.class, 1L);
        TestEntity retrievedTest2 = entityManager.findById(TestEntity.class, 2L);
        ProductEntity retrievedProduct1 = entityManager.findById(ProductEntity.class, 1L);
        ProductEntity retrievedProduct2 = entityManager.findById(ProductEntity.class, 2L);
        UserEntity retrievedUser1 = entityManager.findById(UserEntity.class, 1L);
        
        // 存在しないIDで検索
        TestEntity nonExistentEntity = entityManager.findById(TestEntity.class, 999L);
        
        // 検証
        assertNotNull(retrievedTest1);
        assertEquals("Test Entity 1", retrievedTest1.getName());
        
        assertNotNull(retrievedTest2);
        assertEquals("Test Entity 2", retrievedTest2.getName());
        assertEquals(false, retrievedTest2.isActive());
        
        assertNotNull(retrievedProduct1);
        assertEquals("Product 1", retrievedProduct1.getName());
        assertEquals(29.99, retrievedProduct1.getPrice());
        
        assertNotNull(retrievedProduct2);
        assertEquals("Product 2", retrievedProduct2.getName());
        assertEquals("Books", retrievedProduct2.getCategory());
        
        assertNotNull(retrievedUser1);
        assertEquals("user1", retrievedUser1.getUsername());
        assertEquals("user1@example.com", retrievedUser1.getEmail());
        
        // 存在しないエンティティの検証
        assertNull(nonExistentEntity);
    }
    
    @Test
    void shouldFindAllEntitiesOfType() {
        // TestEntityを複数作成して保存
        TestEntity testEntity1 = new TestEntity();
        testEntity1.setId(1L);
        testEntity1.setName("Test Entity 1");
        testEntity1.setCode("TEST001");
        
        TestEntity testEntity2 = new TestEntity();
        testEntity2.setId(2L);
        testEntity2.setName("Test Entity 2");
        testEntity2.setCode("TEST002");
        
        TestEntity testEntity3 = new TestEntity();
        testEntity3.setId(3L);
        testEntity3.setName("Test Entity 3");
        testEntity3.setCode("TEST003");
        
        // ProductEntityも1つ作成
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setName("Product 1");
        productEntity.setPrice(19.99);
        
        // すべてのエンティティを保存
        entityManager.save(testEntity1);
        entityManager.save(testEntity2);
        entityManager.save(testEntity3);
        entityManager.save(productEntity);
        
        // TestEntityの全件取得
        List<TestEntity> allTestEntities = entityManager.findAll(TestEntity.class);
        
        // ProductEntityの全件取得
        List<ProductEntity> allProductEntities = entityManager.findAll(ProductEntity.class);
        
        // 存在しないタイプの全件取得
        List<UserEntity> allUserEntities = entityManager.findAll(UserEntity.class);
        
        // 検証
        assertNotNull(allTestEntities);
        assertEquals(3, allTestEntities.size());
        
        assertNotNull(allProductEntities);
        assertEquals(1, allProductEntities.size());
        assertEquals("Product 1", allProductEntities.get(0).getName());
        
        assertNotNull(allUserEntities);
        assertEquals(0, allUserEntities.size());
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