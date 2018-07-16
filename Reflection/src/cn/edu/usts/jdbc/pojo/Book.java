package cn.edu.usts.jdbc.pojo;

public class Book {
	private Integer id;
	private String author;
	private String title;
	private Double price;
	public Book() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Book(Integer id, String author, String title, Double price) {
		super();
		this.id = id;
		this.author = author;
		this.title = title;
		this.price = price;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	@Override
	public String toString() {
		return "Book [id=" + id + ", author=" + author + ", title=" + title + ", price=" + price + "]";
	}
	
	
}
