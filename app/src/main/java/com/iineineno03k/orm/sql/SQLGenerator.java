package com.iineineno03k.orm.sql;

/**
 * SQLジェネレーターのインターフェース
 * 各データベース固有のSQL生成器が実装する
 */
public interface SQLGenerator {
    
    /**
     * エンティティクラスと主キーを基にSELECT文を生成する
     * 
     * @param entityClass エンティティのクラス
     * @param idColumnName 主キーのカラム名
     * @return 生成されたSQL文
     */
    String createSelectSQL(Class<?> entityClass, String idColumnName);
    
    /**
     * エンティティクラスを基にINSERT文を生成する
     * 
     * @param entityClass エンティティのクラス
     * @return 生成されたSQL文
     */
    String createInsertSQL(Class<?> entityClass);
    
    /**
     * エンティティクラスと主キーを基にUPDATE文を生成する
     * 
     * @param entityClass エンティティのクラス
     * @param idColumnName 主キーのカラム名
     * @return 生成されたSQL文
     */
    String createUpdateSQL(Class<?> entityClass, String idColumnName);
    
    /**
     * エンティティクラスと主キーを基にDELETE文を生成する
     * 
     * @param entityClass エンティティのクラス
     * @param idColumnName 主キーのカラム名
     * @return 生成されたSQL文
     */
    String createDeleteSQL(Class<?> entityClass, String idColumnName);
    
    /**
     * データベース固有のJava型からSQL型への変換を行う
     * 
     * @param javaType Javaの型
     * @return SQLの型
     */
    String mapJavaTypeToSQLType(Class<?> javaType);
    
    /**
     * エンティティクラスに基づいてテーブル作成用のSQL文を生成する
     * 
     * @param entityClass エンティティのクラス
     * @return 生成されたCREATE TABLE文
     */
    String createTableSQL(Class<?> entityClass);
    
    /**
     * エンティティクラスから全件取得用のSELECT文を生成する
     * 
     * @param entityClass エンティティクラス
     * @return 生成されたSQL
     */
    String createSelectAllSQL(Class<?> entityClass);
} 