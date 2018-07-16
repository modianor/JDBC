package cn.edu.usts.jdbc.test;

import java.sql.SQLException;
import java.util.List;

import cn.edu.usts.jdbc.pojo.Book;
import cn.edu.usts.jdbc.pojo.Emp;
import cn.edu.usts.jdbc.service.EntityService;

public class Test {

	public static void main(String[] args) {
		EntityService es = new EntityService();
		// List<Emp> list = es.findByField(Emp.class, "address", "株洲");
		List<Book> list = es.getEntityList(Book.class);
		try {
			es.release();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Book emp : list) {
			System.out.println(emp);
		}
	}

}
