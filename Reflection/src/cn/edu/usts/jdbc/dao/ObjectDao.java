package cn.edu.usts.jdbc.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.usts.jdbc.util.MyConnection;

/**
 * 自定义SQL执行对象
 * <p>
 * Title:MyQuery
 * </p>
 * 
 * @version
 */
public class ObjectDao implements IObjectDao {
	// 数据库连接对象
	public Connection connection = null;

	public ObjectDao(Connection connection) {
		this.connection = connection;
	}

	/* (non-Javadoc)
	 * @see cn.edu.usts.jdbc.dao.IObjectDao#findByField(java.lang.Class, java.lang.String, java.lang.Object)
	 */

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> findByField(Class<T> t, String field, Object value) {
		String tablename = null;
		Class<? extends Object> c1 = null;
		T tinstance;
		List<T> objectList = new ArrayList<>();
		try {
			T newInstance = t.newInstance();
			c1 = newInstance.getClass();
			tablename = c1.getSimpleName().toLowerCase();
		} catch (InstantiationException | IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			@SuppressWarnings("unused")
			T instance = (T) t.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// System.out.println("tablename = " + tablename.toLowerCase());
		// System.out.println("field = " + field.toLowerCase());
		// System.out.println("value = " + value.toString());
		// String sql = "select * from ? where ? = '" + "?" +"';";
		String sql = "select * from " + tablename + " where " + field + " = " + "'" + value.toString() + "'" + ";";
		try {
			System.out.println("ObjectDao = " + connection);
			ps = connection.prepareStatement(sql);
			System.out.println(sql);
			// 执行查询
			rs = ps.executeQuery();
			// rs = connection.createStatement().executeQuery(sql);
			while (rs.next()) {
				// 创建一个新的实例
				tinstance = (T) c1.newInstance();
				// 获取结果集元数据
				ResultSetMetaData resultSetMetaData = rs.getMetaData();
				// 获取查询出来的总列数
				int columnCount = resultSetMetaData.getColumnCount();
				System.out.println("columnCount =====" + columnCount);
				for (int i = 0; i < columnCount; i++) {
					// 获取列名
					String columnName = resultSetMetaData.getColumnName(i + 1);
					System.out.println("columnName =====" + columnName);
					// 获取列值
					Object fieldValue = rs.getObject(columnName);
					// 根据列名获取字段
					Field declaredField = null;
					try {
						declaredField = c1.getDeclaredField(columnName);
					} catch (NoSuchFieldException | SecurityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// 私有字段设置允许访问
					declaredField.setAccessible(true);
					// 调用方法给字段赋新的值
					try {
						declaredField.set(tinstance, fieldValue);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				objectList.add(tinstance);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MyConnection.close(rs, ps);
		}
		return objectList;

	}

	/* (non-Javadoc)
	 * @see cn.edu.usts.jdbc.dao.IObjectDao#insert(java.lang.Object)
	 */
	@Override
	public void insert(Object object) {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		Class<? extends Object> clazz = object.getClass();
		// 获取简单类名，数据库表名和类名一致
		String simpleName = clazz.getSimpleName();
		// 获取字段
		Field[] declaredFields = clazz.getDeclaredFields();
		String sql = "insert into " + simpleName;
		String fieldString = "(";
		String valueString = "values(";
		// 获取字段的个数
		int length = declaredFields.length;
		try {
			for (int i = 0; i < length - 1; i++) {
				// 私有字段设置允许访问
				declaredFields[i].setAccessible(true);
				// 获取字段值
				Object fieldValue = declaredFields[i].get(object);

				String typeName = declaredFields[i].getGenericType().getTypeName();
				if (typeName.toLowerCase().contains("date")) {
					// 如果是日期类型
					Date date = (Date) fieldValue;
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String format = dateFormat.format(date);
					fieldValue = format;
				}
				// 拼接字段名
				fieldString += declaredFields[i].getName() + ",";
				// 拼接字段值
				valueString += "'" + fieldValue + "',";
			}
			declaredFields[length - 1].setAccessible(true);
			// 获取字段值
			Object fieldValue = declaredFields[length - 1].get(object);
			// 获取字段类型名称
			String typeName = declaredFields[length - 1].getGenericType().getTypeName();
			if (typeName.toLowerCase().contains("date")) {
				// 如果是日期类型
				Date date = (Date) fieldValue;
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String format = dateFormat.format(date);
				fieldValue = format;
			}
			// 拼接字段名
			fieldString += declaredFields[length - 1].getName() + ") ";
			// 拼接字段值
			valueString += "'" + fieldValue + "')";
			// 拼接SQL语句
			sql = sql + fieldString + valueString;
			System.out.println("SQL = " + sql);
			// 设置事务手动提交
			// this.
			prepareStatement = this.connection.prepareStatement(sql);
			prepareStatement.executeUpdate();
			// 提交事务应当在service层手动
			// this.connection.commit();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				// 回滚事务
				this.connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			// 关闭连接
			MyConnection.close(resultSet, prepareStatement);
		}
	}

	/* (non-Javadoc)
	 * @see cn.edu.usts.jdbc.dao.IObjectDao#select(java.lang.Class)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "resource" })
	public <T> List<T> select(Class<T> t) {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<T> objectList = new ArrayList<>();
		// 获取模板T的实例
		T newInstance2 = null;
		try {
			newInstance2 = t.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		T newInstance;
		// 获取实例的Class类对象
		Class<? extends Object> clazz = newInstance2.getClass();
		// 获取简单类名，数据库表名和类名一致
		String simpleName = clazz.getSimpleName();
		try {
			// 获取数据库元数据
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			// 获取表列
			resultSet = databaseMetaData.getColumns(null, null, simpleName, null);
			List<String> columnNameList = new ArrayList<>();
			while (resultSet.next()) {
				// 获取列名
				String columnName = resultSet.getString("COLUMN_NAME");
				columnNameList.add(columnName);
			}
			// 定义SQL语句
			String sql = "select ";
			for (String columnName : columnNameList) {
				sql += columnName + ",";
			}
			// 删除最后一个逗号
			int lastIndexOf = sql.lastIndexOf(",");
			sql = sql.substring(0, lastIndexOf);
			sql += " from " + simpleName;

			System.out.println("SQL = " + sql);
			prepareStatement = this.connection.prepareStatement(sql);
			// 执行查询
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				// 创建一个新的实例
				newInstance = (T) clazz.newInstance();
				// 获取结果集元数据
				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				// 获取查询出来的总列数
				int columnCount = resultSetMetaData.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					// 获取列名
					String columnName = resultSetMetaData.getColumnName(i + 1);
					// 获取列值
					Object fieldValue = resultSet.getObject(columnName);
					// 根据列名获取字段
					Field declaredField = clazz.getDeclaredField(columnName);
					// 私有字段设置允许访问
					declaredField.setAccessible(true);
					// 调用方法给字段赋新的值
					declaredField.set(newInstance, fieldValue);
				}
				objectList.add(newInstance);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			MyConnection.close(resultSet, prepareStatement);
		}
		return objectList;
	}

	/* (non-Javadoc)
	 * @see cn.edu.usts.jdbc.dao.IObjectDao#selectByPrimaryKey(T)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "resource" })
	public <T> T selectByPrimaryKey(T t) {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		Class<? extends Object> clazz = t.getClass();
		// 获取简单类名，数据库表名和类名一致
		String simpleName = clazz.getSimpleName();
		T newInstance = null;
		try {
			// 创建一个新的实例
			newInstance = (T) clazz.newInstance();
			// 获取数据库元数据
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			// 获取给定表主键列的描述
			resultSet = databaseMetaData.getPrimaryKeys(null, null, simpleName);
			String primaryKey = null;
			if (resultSet.next()) {
				// 获取主键列的名称
				primaryKey = resultSet.getString("COLUMN_NAME");
			}
			// 获取表列
			resultSet = databaseMetaData.getColumns(null, null, simpleName, null);
			List<String> columnNameList = new ArrayList<>();
			while (resultSet.next()) {
				// 获取列名
				String columnName = resultSet.getString("COLUMN_NAME");
				columnNameList.add(columnName);
			}
			if (null != primaryKey) {
				// 获取主键在实体类中的同名字段
				Field declaredField = clazz.getDeclaredField(primaryKey);
				// 设置私有字段允许访问
				declaredField.setAccessible(true);
				// 获取字段的值
				Object primaryKeyValue = declaredField.get(t);
				// 获取所有字段
				Field[] declaredFields = clazz.getDeclaredFields();
				// 定义SQL语句
				String sql = "select ";
				for (String columnName : columnNameList) {
					sql += columnName + ",";
				}
				// 删除最后一个逗号
				int lastIndexOf = sql.lastIndexOf(",");
				sql = sql.substring(0, lastIndexOf);
				sql += " from " + simpleName + " where " + primaryKey + "='" + primaryKeyValue + "'";
				System.out.println("SQL = " + sql);

				prepareStatement = connection.prepareStatement(sql);
				resultSet = prepareStatement.executeQuery();
				if (resultSet.next()) {
					// 获取结果集元数据
					ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
					// 获取查找出来的总列数
					int columnCount = resultSetMetaData.getColumnCount();

					for (int i = 0; i < columnCount; i++) {
						// 获取列名
						String columnName = resultSetMetaData.getColumnName(i + 1);
						// 获取列值
						Object columnValue = resultSet.getObject(columnName);
						for (Field field : declaredFields) {
							field.setAccessible(true);
							// 属性名和字段名一样
							if (field.getName().equals(columnName)) {
								// 设置属性值
								field.set(newInstance, columnValue);
							}
						}
					}
				}
			} else {
				System.err.println("主键为空！");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} finally {
			MyConnection.close(resultSet, prepareStatement);
		}
		return newInstance;
	}

	/* (non-Javadoc)
	 * @see cn.edu.usts.jdbc.dao.IObjectDao#updateByPrimaryKey(java.lang.Object)
	 */
	@Override
	public void updateByPrimaryKey(Object object) {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		Class<? extends Object> clazz = object.getClass();
		// 获取简单类名，数据库表名和类名一致
		String simpleName = clazz.getSimpleName();
		try {
			// 获取数据库元数据
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			// 获取给定表主键列的描述
			resultSet = databaseMetaData.getPrimaryKeys(null, null, simpleName);
			String primaryKey = null;
			if (resultSet.next()) {
				// 获取主键列的名称
				primaryKey = resultSet.getString("COLUMN_NAME");
			}
			if (null != primaryKey) {
				// 获取主键在实体类中的同名字段
				Field declaredField = clazz.getDeclaredField(primaryKey);
				// 设置私有字段允许访问
				declaredField.setAccessible(true);
				// 获取字段的值
				Object primaryKeyValue = declaredField.get(object);
				// 获取字段
				Field[] declaredFields = clazz.getDeclaredFields();
				// 定义SQL语句
				String sql = "update " + simpleName + " set ";
				for (Field field : declaredFields) {
					String fieldName = field.getName();
					// 这个字段不是主键
					if (!fieldName.equals(primaryKey)) {
						// 设置允许访问
						field.setAccessible(true);
						sql += fieldName + "='";
						// 获取属性值
						Object fieldValue = field.get(object);
						// 获取字段类型名称
						String typeName = field.getGenericType().getTypeName();
						// 不是基本数据类型
						if (typeName.toLowerCase().contains("date")) {
							// 如果是日期类型
							Date date = (Date) fieldValue;
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String format = dateFormat.format(date);
							sql += format + "', ";
						} else {
							sql += fieldValue + "', ";
						}
					}
				}
				// 删除最后一个逗号
				int lastIndexOf = sql.lastIndexOf(",");
				sql = sql.substring(0, lastIndexOf);

				sql += " where " + primaryKey + "='" + primaryKeyValue + "' ";
				System.out.println("SQL = " + sql);
				// 开启事务
				
				prepareStatement = connection.prepareStatement(sql);
				prepareStatement.executeUpdate();
				// 提交事务
				//connection.commit();
			} else {
				System.err.println("主键为空！");
			}
		} catch (SQLException e) {
			try {
				// 事务回滚
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			MyConnection.close(resultSet, prepareStatement);
		}
	}

	/* (non-Javadoc)
	 * @see cn.edu.usts.jdbc.dao.IObjectDao#deleteByPrimaryKey(java.lang.Object)
	 */
	@Override
	public void deleteByPrimaryKey(Object object) {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		Class<? extends Object> clazz = object.getClass();
		// 获取简单类名，数据库表名和类名一致
		String simpleName = clazz.getSimpleName();
		try {
			// 获取数据库元数据
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			// 获取给定表主键列的描述
			resultSet = databaseMetaData.getPrimaryKeys(null, null, simpleName.toLowerCase());
			String primaryKey = null;
			if (resultSet.next()) {
				// 获取主键列的名称
				primaryKey = resultSet.getString("COLUMN_NAME");
			}
			if (null != primaryKey) {
				// 获取主键在实体类中的同名字段
				Field declaredField = clazz.getDeclaredField(primaryKey);
				// 设置私有字段允许访问
				declaredField.setAccessible(true);
				// 获取字段的值
				Object primaryKeyValue = declaredField.get(object);

				// 定义SQL语句
				String sql = "delete from " + simpleName + " where " + primaryKey + "='" + primaryKeyValue + "'";
				System.out.println("SQL = " + sql);
				prepareStatement = connection.prepareStatement(sql);
				prepareStatement.executeUpdate();
				//connection.commit();
			} else {
				System.err.println("主键为空！");
			}
		} catch (SQLException e) {
			try {
				// 事务回滚
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			MyConnection.close(resultSet, prepareStatement);
		}
	}

	/**
	 * 判断字段的类型，给字段赋值
	 * 
	 * @param newInstance
	 * @param declaredField
	 * @param fieldValue
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	// private void fieldisPrimitive(Object newInstance,Field declaredField,
	// Object fieldValue) throws IllegalArgumentException,
	// IllegalAccessException {
	// // 私有字段设置允许访问
	// declaredField.setAccessible(true);
	// // 获取字段类型
	// Class<?> type = declaredField.getType();
	// // 获取类型名称
	// String typeName = declaredField.getGenericType().getTypeName();
	// // 判断这个Class 对象是否表示一个基本类型
	// if(type.isPrimitive()) {
	// switch (typeName) {
	// case "boolean":
	// Boolean boolean1 = (Boolean) fieldValue;
	// declaredField.setBoolean(newInstance, boolean1);
	// break;
	// case "byte":
	// Byte byte1 = (Byte) fieldValue;
	// declaredField.setByte(newInstance, byte1);
	// break;
	// case "char":
	// Character character = (Character) fieldValue;
	// declaredField.setChar(newInstance, character);
	// break;
	// case "short":
	// Short short1 = (Short) fieldValue;
	// declaredField.setShort(newInstance, short1);
	// break;
	// case "int":
	// Integer integer = (Integer) fieldValue;
	// declaredField.setInt(newInstance, integer);
	// break;
	// case "long":
	// Long long1 = (Long) fieldValue;
	// declaredField.setLong(newInstance, long1);
	// break;
	// case "float":
	// Float float1 = (Float) fieldValue;
	// declaredField.setFloat(newInstance, float1);
	// break;
	// case "double":
	// Double double1 = (Double) fieldValue;
	// declaredField.setDouble(newInstance, double1);
	// break;
	// default:
	// break;
	// }
	// }else {
	// // 不是基本数据类型
	// if(typeName.toLowerCase().contains("date")) {
	// // 如果是日期类型
	// Date date = (Date) fieldValue;
	// declaredField.set(newInstance, new java.sql.Date(date.getTime()));
	// }else {
	// declaredField.set(newInstance, fieldValue);
	// }
	// }
	// }
}
