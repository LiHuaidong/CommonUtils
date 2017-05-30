import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJson {

	public static void main(String[] args) throws JsonProcessingException {
		List<Person> list = new ArrayList<Person>();
		Person me = null;
		for (int i = 0; i < 10000000; i++) {
			me = new Person("test", 28, 173);
			list.add(me);
		}

		long start = System.currentTimeMillis();
		for (Person p : list) {
			JSON.toJSON(p);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);

		ObjectMapper mapper = new ObjectMapper();
		start = System.currentTimeMillis();
		for (Person p : list) {
			mapper.writeValueAsString(p);
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}

class Person implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private int age;
	private int height;

	public Person() {
	}

	public Person(String name, int age, int height) {
		super();
		this.name = name;
		this.age = age;
		this.height = height;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
