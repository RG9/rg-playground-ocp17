package pl.rg9.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.jupiter.api.Test;

public class Chapter14Test_Serialization {

	@Test
	void serialization() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);

		out.writeObject(new MyRecord(2));

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

		var o = (MyRecord) in.readObject();
		assertThat(o).hasToString("MyRecord[a=2]");
	}

	@Test
	void serialization_objectHierarchy() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);

		var serializable = new ChildRecord();
		serializable.a = 2;
		serializable.myRecord = new MyRecord(1);
		out.writeObject(serializable);

		System.out.println("Deserialization");

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		var child = (ChildRecord) in.readObject();
		assertThat(child.a).isEqualTo(0);
		assertThat(child.myRecord).hasToString("MyRecord[a=1]");

		in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		var parent = (ParentRecord) in.readObject();
		assertThat(parent.myRecord).hasToString("MyRecord[a=1]");
	}

	record MyRecord(int a) implements Serializable {

	}

	static class ChildRecord extends ParentRecord {

		transient int a;

		public ChildRecord() {
			System.out.println("ChildRecord()");
		}
	}

	static class ParentRecord extends SuperParentNoSerializable implements Serializable {

		MyRecord myRecord;

//		 Object field = new Object(); // must be null or transient, otherwise java.io.NotSerializableException

		Object field;

		public ParentRecord() {
			System.out.println("ParentRecord()");
		}
	}

	static class SuperParentNoSerializable {

		// constructor of first nonserializable parent will called during deserialization
		public SuperParentNoSerializable() {
			System.out.println("SuperParentNoSerializable()");
		}
	}
}
