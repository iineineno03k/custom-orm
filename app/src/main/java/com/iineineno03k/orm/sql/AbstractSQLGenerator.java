package com.iineineno03k.orm.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLジェネレーターの抽象基底クラス
 * 共通の実装を提供する
 */
public abstract class AbstractSQLGenerator implements SQLGenerator {
    
    /**
     * テーブル名を取得する（エンティティクラス名をスネークケースに変換）
     * 
     * @param entityClass エンティティのクラス
     * @return テーブル名
     */
    protected String getTableName(Class<?> entityClass) {
        String className = entityClass.getSimpleName();
        // キャメルケースからスネークケースへの変換
        return camelToSnake(className);
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
} 