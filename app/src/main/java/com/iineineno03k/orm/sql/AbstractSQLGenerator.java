package com.iineineno03k.orm.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.iineineno03k.orm.annotation.Column;
import com.iineineno03k.orm.annotation.Entity;
import com.iineineno03k.orm.annotation.Id;
import com.iineineno03k.orm.annotation.Table;

/**
 * SQLGeneratorの基本的な実装を提供する抽象クラス。
 * 各データベース固有の実装はこのクラスを拡張する。
 */
public abstract class AbstractSQLGenerator implements SQLGenerator {
    
    /**
     * エンティティクラスのテーブル名を取得する
     * 
     * @param entityClass エンティティクラス
     * @return テーブル名
     */
    protected String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            return tableAnnotation.name();
        } else if (entityClass.isAnnotationPresent(Entity.class)) {
            return entityClass.getSimpleName().toLowerCase();
        } else {
            throw new IllegalArgumentException("Class is not an entity: " + entityClass.getName());
        }
    }
    
    /**
     * エンティティのフィールド名一覧を取得する
     * 
     * @param entityClass エンティティのクラス
     * @return フィールド名のリスト
     */
    protected List<String> getFieldNames(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        List<String> fieldNames = new ArrayList<>();
        
        for (Field field : fields) {
            // 静的フィールドは除外
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            fieldNames.add(field.getName());
        }
        
        return fieldNames;
    }
    
    /**
     * キャメルケースをスネークケースに変換する
     * 
     * @param camel キャメルケース文字列
     * @return スネークケース文字列
     */
    protected String camelToSnake(String camel) {
        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";
        return camel.replaceAll(regex, replacement).toLowerCase();
    }
    
    /**
     * フィールド名をカラム名に変換する
     * 
     * @param fieldName フィールド名
     * @return カラム名
     */
    protected String fieldToColumnName(String fieldName) {
        return camelToSnake(fieldName);
    }
    
    /**
     * プレースホルダーのリストを生成する
     * 
     * @param count プレースホルダーの数
     * @return カンマ区切りのプレースホルダー
     */
    protected abstract String createPlaceholders(int count);
    
    /**
     * フィールドのカラム名を取得する
     * 
     * @param field フィールド
     * @return カラム名
     */
    protected String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (!columnAnnotation.name().isEmpty()) {
                return columnAnnotation.name();
            }
        }
        return field.getName().toLowerCase();
    }
    
    /**
     * エンティティクラスの全フィールドを取得する
     * 
     * @param entityClass エンティティクラス
     * @return フィールドのリスト
     */
    protected List<Field> getAllFields(Class<?> entityClass) {
        return Arrays.asList(entityClass.getDeclaredFields());
    }
    
    /**
     * エンティティクラスから全件取得用のSELECT文を生成する
     * 
     * @param entityClass エンティティクラス
     * @return 生成されたSQL
     */
    @Override
    public String createSelectAllSQL(Class<?> entityClass) {
        String tableName = getTableName(entityClass);
        List<Field> fields = getAllFields(entityClass);
        
        String columnList = fields.stream()
                .map(this::getColumnName)
                .collect(Collectors.joining(", "));
                
        return String.format("SELECT %s FROM %s", columnList, tableName);
    }
} 