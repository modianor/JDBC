package cn.edu.usts.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.edu.usts.jdbc.dao.IObjectDao;
import cn.edu.usts.jdbc.dao.ObjectDao;
import cn.edu.usts.jdbc.util.MyConnection;

public class EntityService implements IEntityService {
	// MyDataSource myDataSource = new MyDataSource();
	// MyConnection myConnection = MyConnection.getConnection();
	IObjectDao dao = null;
	Connection connection;

	public EntityService() {
		this.connection = MyConnection.getConnection();
		// System.out.println(connection);
		this.dao = new ObjectDao(this.connection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.usts.jdbc.service.IEntityService#commit()
	 */
	@Override
	public void commit() throws SQLException {
		this.connection.commit();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.usts.jdbc.service.IEntityService#release()
	 */
	@Override
	public void release() throws SQLException {
		this.connection.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.usts.jdbc.service.IEntityService#findByField(java.lang.Class,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> List<T> findByField(Class<T> t, String field, Object value) {
		List<T> select = dao.findByField(t, field, value.toString());
		return select;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.usts.jdbc.service.IEntityService#insert(T)
	 */
	@Override
	public <T> void insert(T t) throws SQLException {
		dao.insert(t);
		commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.edu.usts.jdbc.service.IEntityService#getEntityList(java.lang.Class)
	 */
	@Override
	public <T> List<T> getEntityList(Class<T> t) {
		List<T> select = dao.select(t);
		return select;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.usts.jdbc.service.IEntityService#selectByPrimaryKey(T)
	 */
	@Override
	public <T> T selectByPrimaryKey(T t) {
		t = dao.selectByPrimaryKey(t);
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.usts.jdbc.service.IEntityService#updateByPrimaryKey(T)
	 */
	@Override
	public <T> void updateByPrimaryKey(T t) throws SQLException {
		dao.updateByPrimaryKey(t);
		commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.edu.usts.jdbc.service.IEntityService#deleteByPrimaryKey(T)
	 */
	@Override
	public <T> void deleteByPrimaryKey(T t) throws SQLException {
		dao.deleteByPrimaryKey(t);
		commit();
	}

}
