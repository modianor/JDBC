package cn.edu.usts.jdbc.service;

import java.sql.SQLException;
import java.util.List;

public interface IEntityService {

	void commit() throws SQLException;

	void release() throws SQLException;

	/**
	 * 按照字段查询
	 */
	<T> List<T> findByField(Class<T> t, String field, Object value);

	/**
	 * 添加实体信息
	 * 
	 * @throws SQLException
	 */
	<T> void insert(T t) throws SQLException;

	/**
	 * 查询所有实体
	 */
	<T> List<T> getEntityList(Class<T> t);

	/**
	 * 根据主键查询实体的信息
	 */
	<T> T selectByPrimaryKey(T t);

	/**
	 * 根据主键修改实体的信息
	 * @throws SQLException 
	 */
	<T> void updateByPrimaryKey(T t) throws SQLException;

	/**
	 * 根据主键删除实体的信息
	 * @throws SQLException 
	 */
	<T> void deleteByPrimaryKey(T t) throws SQLException;

}