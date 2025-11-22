package pl.rg9.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;

public class Chapter14Test_Streams {

	@Test
	void printStream() {
		var out = new ByteArrayOutputStream();
		var printStream = new PrintStream(out);
		printStream.print("Hello, ");
		printStream.print(new MyRecord(2));

		assertThat(out.toString()).isEqualTo("Hello, MyRecord[a=2]");
	}

	@Test
	void printWriter() {
		var out = new ByteArrayOutputStream();
		var printWriter = new PrintWriter(out);

		printWriter.print("Hello, ");
		printWriter.print(new MyRecord(2));

		printWriter.flush();
		assertThat(out.toString()).isEqualTo("Hello, MyRecord[a=2]");
	}

	record MyRecord(int a) {

	}

	@Test
	void readAndWriteFiles_bufferedWriter() throws IOException {
		var tmpFile = Files.createTempFile("readAndWriteFiles", "1");

		// IO
		try (var bufferedWriter = new BufferedWriter(new FileWriter(tmpFile.toFile()))) {
			bufferedWriter.write("test1");
		}
		try (var bufferedReader = new BufferedReader(new FileReader(tmpFile.toFile()))) {
			assertThat(bufferedReader.readLine()).isEqualTo("test1");
		}

		// NIO
		try (var bufferedWriter = Files.newBufferedWriter(tmpFile)) {
			bufferedWriter.write("test2");
		}
		try (var bufferedReader = Files.newBufferedReader(tmpFile)) {
			assertThat(bufferedReader.readLine()).isEqualTo("test2");
		}
	}

	//	StandardOpenOption	OpenOption	APPEND	If file is already open for write, append to the end.
//	CREATE	Create new file if it does not exist.
//	CREATE_NEW	Create new file only if it does not exist; fail otherwise.
//	READ	Open for read access.
//	TRUNCATE_EXISTING	If file is already open for write, erase file and append to beginning.
//	WRITE	Open for write access.

	@Test
	void readAndWriteFiles_bufferedWriter_appendOption() throws IOException {
		var tmpFile = Files.createTempFile("readAndWriteFiles", "1");

		try (var bufferedWriter = Files.newBufferedWriter(tmpFile)) { // see: java.nio.file.spi.FileSystemProvider.DEFAULT_OPEN_OPTIONS
			bufferedWriter.write("test1");
		}
		try (var bufferedWriter = Files.newBufferedWriter(tmpFile)) {
			bufferedWriter.write("test2");
		}
		try (var bufferedWriter = Files.newBufferedWriter(tmpFile, StandardOpenOption.APPEND)) {
			bufferedWriter.write("test3");
		}
		assertThat(Files.readString(tmpFile)).isEqualTo("test2test3");

		try (var bufferedWriter = Files.newBufferedWriter(tmpFile, StandardOpenOption.WRITE)) { // same behavior for CREATE
			bufferedWriter.write("test4");
		}
		assertThat(Files.readString(tmpFile)).isEqualTo("test4test3");

		try (var bufferedWriter = Files.newBufferedWriter(tmpFile, StandardOpenOption.TRUNCATE_EXISTING)) {
			bufferedWriter.write("test5");
		}
		assertThat(Files.readString(tmpFile)).isEqualTo("test5");
	}

	@Test
	void readAndWriteFiles_bufferedWriter_createNew() throws IOException {
		var tmpFile = Files.createTempFile("readAndWriteFiles", "1");

		assertThatCode(() -> Files.newBufferedWriter(tmpFile, StandardOpenOption.CREATE_NEW))
			.isInstanceOf(FileAlreadyExistsException.class);

		try (var bufferedWriter = Files.newBufferedWriter(tmpFile, StandardOpenOption.DELETE_ON_CLOSE)) {
		}

		try (var bufferedWriter = Files.newBufferedWriter(tmpFile)) { // CREATE by default, see: java.nio.file.spi.FileSystemProvider.DEFAULT_OPEN_OPTIONS
			bufferedWriter.write("test");
		}

		assertThat(Files.readString(tmpFile)).isEqualTo("test");

	}

	@Test
	void copyFiles() throws IOException {
		var file1 = Files.createTempFile("file", "1");
		var file2 = Files.createTempFile("file", "2");

		Files.writeString(file1, "01234567");
		copyFile_broken(file1.toFile(), file2.toFile());
		assertThat(Files.readString(file2)).as("incorrect result").isEqualTo("01234567\0\0");

		Files.writeString(file1, "01234567890123");
		copyFile_broken(file1.toFile(), file2.toFile());
		assertThat(Files.readString(file2)).as("incorrect result").isEqualTo("01234567890123456789");

		var multipleOf10 = "01234567890123456789";
		Files.writeString(file1, multipleOf10);
		copyFile_broken(file1.toFile(), file2.toFile());
		assertThat(Files.readString(file2)).as("correct result").isEqualTo(multipleOf10);

		Files.writeString(file1, "01234567");
		copyFile_fixed(file1.toFile(), file2.toFile());
		assertThat(Files.readString(file2)).isEqualTo("01234567");

		Files.writeString(file1, "01234567890123");
		copyFile_fixed(file1.toFile(), file2.toFile());
		assertThat(Files.readString(file2)).isEqualTo("01234567890123");

		Files.writeString(file1, multipleOf10);
		copyFile_fixed(file1.toFile(), file2.toFile());
		assertThat(Files.readString(file2)).isEqualTo(multipleOf10);
	}

	// from review questions
	void copyFile_broken(File file1, File file2) throws IOException {
		var reader = new InputStreamReader(new FileInputStream(file1), StandardCharsets.UTF_8);
		try (var writer = new FileWriter(file2)) {
			char[] buffer = new char[10];
			while (reader.read(buffer) != -1) {
				writer.write(buffer);
				// n1
			}
		}
		reader.close(); // otherwise there could be resource leak
	}

	// from review questions
	void copyFile_fixed(File file1, File file2) throws IOException {
		var reader = new InputStreamReader(new FileInputStream(file1), StandardCharsets.UTF_8);
		try (var writer = new FileWriter(file2)) {
			char[] buffer = new char[10];
			int charRead = 0;
			while ((charRead = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, charRead);
				// n1
			}
		}
		reader.close(); // otherwise there could be resource leak
	}

	@Test
	void mark() throws IOException {
		var tmpFile = Files.createTempFile("mark", "test");

		Files.writeString(tmpFile, "test");

//		var reader = new FileReader(tmpFile.toFile()); // mark unsupported: will throw "java.io.IOException: mark() not supported"
		var reader = new BufferedReader(new FileReader(tmpFile.toFile()));
		try (reader) {
			var markSupported = reader.markSupported();
			System.out.println("markSupported: " + markSupported);
			while (true) {
				if (markSupported) {
					reader.mark(1); // buffer size

					if (reader.read() == -1) {
						break;
					}

					reader.reset(); // so I can read again
					System.out.println((char) reader.read());
				} else {
					var read = reader.read();
					if (read == -1) {
						break;
					}
					System.out.println((char) read);
				}

			}

		}

	}

}
