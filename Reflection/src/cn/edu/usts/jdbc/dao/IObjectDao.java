package cn.edu.usts.jdbc.dao;

import java.util.List;

public interface IObjectDao {

	/**
	 * 按照字段查询
	 * 
	 * @param object
	 */

	<T> List<T> findByField(Class<T> t, String field, Object value);

	/**
	 * 插入一条记录
	 * 
	 * @param object
	 */
	void insert(Object object);

	/**
	 * 查询所有信息
	 * 
	 * @param t
	 * @return
	 */
	<T> List<T> select(Class<T> t);

	/**
	 * 根据主键查询
	 * 
	 * @param t
	 * @return
	 */
	<T> T selectByPrimaryKey(T t);

	/**
	 * 根据主键修改记录
	 * 
	 * @param object
	 */
	void updateByPrimaryKey(Object object);

	/**
	 * 根据主键删除记录
	 * 
	 * @param object
	 */
	void deleteByPrimaryKey(Object object);

}