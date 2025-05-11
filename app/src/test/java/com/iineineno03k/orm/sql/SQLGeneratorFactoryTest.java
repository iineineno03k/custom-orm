package com.iineineno03k.orm.sql;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * SQLGeneratorFactoryのテストクラス
 */
@DisplayName("SQLGeneratorFactory のテスト")
public class SQLGeneratorFactoryTest {
    
    @Nested
    @DisplayName("createGenerator(DatabaseType) メソッドのテスト")
    class CreateGeneratorWithEnumTest {
        
        @DisplayName("有効なデータベースタイプに対して正しいジェネレータを生成する")
        @ParameterizedTest(name = "{0} => {1}")
        @MethodSource("databaseTypeToGeneratorProvider")
        void shouldCreateCorrectGenerator(DatabaseType databaseType, Class<? extends SQLGenerator> expectedType) {
            SQLGenerator generator = SQLGeneratorFactory.createGenerator(databaseType);
            assertNotNull(generator);
            assertTrue(expectedType.isInstance(generator), 
                    "生成されたジェネレータは " + expectedType.getSimpleName() + " のインスタンスであるべき");
        }
        
        static Stream<Arguments> databaseTypeToGeneratorProvider() {
            return Stream.of(
                Arguments.of(DatabaseType.MYSQL, MySQLSQLGenerator.class),
                Arguments.of(DatabaseType.POSTGRESQL, PostgresSQLGenerator.class),
                Arguments.of(DatabaseType.ORACLE, OracleSQLGenerator.class)
            );
        }
        
        @Test
        @DisplayName("nullのデータベースタイプに対して例外を投げる")
        void shouldThrowExceptionForNullDatabaseType() {
            DatabaseType nullType = null;
            assertThrows(IllegalArgumentException.class, () -> {
                SQLGeneratorFactory.createGenerator(nullType);
            });
        }
    }
    
    @Nested
    @DisplayName("createGenerator(String) メソッドのテスト")
    class CreateGeneratorWithStringTest {
        
        @DisplayName("有効な文字列からデータベースタイプを変換して正しいジェネレータを生成する")
        @ParameterizedTest(name = "文字列: {0}")
        @ValueSource(strings = {"MYSQL", "mysql", "MySQL", "POSTGRESQL", "postgresql", "PostgreSQL", "ORACLE", "oracle", "Oracle"})
        void shouldCreateGeneratorFromValidString(String databaseType) {
            SQLGenerator generator = SQLGeneratorFactory.createGenerator(databaseType);
            assertNotNull(generator);
        }
        
        @DisplayName("無効なデータベースタイプ文字列に対して例外を投げる")
        @ParameterizedTest(name = "無効な値: {0}")
        @ValueSource(strings = {"UnsupportedDB", "Invalid", "MongoDB"})
        void shouldThrowExceptionForUnsupportedDatabaseTypeString(String invalidType) {
            assertThrows(IllegalArgumentException.class, () -> {
                SQLGeneratorFactory.createGenerator(invalidType);
            });
        }
        
        @DisplayName("null または 空文字のデータベースタイプに対して例外を投げる")
        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowExceptionForNullOrEmptyDatabaseType(String invalidType) {
            assertThrows(IllegalArgumentException.class, () -> {
                SQLGeneratorFactory.createGenerator(invalidType);
            });
        }
    }
} 