package cn.edu.usts.jdbc.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 自定义数据源
 * <p>Title:MyDataSource</p>
 * @version
 */
public class MyDataSource {
	// 数据库驱动
	private final String driverClassName;
	// 数据库连接URL
	private final String url;
	// 数据库用户名
	private final String username;
	// 数据库密码
	private final String password;
	
	/**
	 * 构造方法注入属性值
	 */
	public MyDataSource(){
		Properties properties = new Properties();
		InputStream inputStream;
		try {
			String path = this.getClass().getResource("").getPath();
			System.out.println(path);
			inputStream = new FileInputStream(path + "jdbc.properties");
			properties.load(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.driverClassName = properties.getProperty("jdbc.driverClassName");
		this.url = properties.getProperty("jdbc.url");
		this.username = properties.getProperty("jdbc.username");
		this.password = properties.getProperty("jdbc.password");
	}
	
	public String getDriverClassName() {
		return driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "MyDataSource [driverClassName=" + driverClassName + ", url=" + url + ", username=" + username
				+ ", password=" + password + "]";
	}
	
	
}
