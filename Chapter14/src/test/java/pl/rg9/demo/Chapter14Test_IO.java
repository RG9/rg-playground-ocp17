package pl.rg9.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.File;

import org.junit.jupiter.api.Test;

class Chapter14Test_IO {

	@Test
	void create_file_reference() {
		assertThat(new File("")).hasToString("");
		assertThat(new File(".")).hasToString(".");

		assertThat(new File("/root/user/.bashrc")).hasToString("/root/user/.bashrc");
		assertThat(new File(new File("/root/user"), ".bashrc")).hasToString("/root/user/.bashrc");
		assertThat(new File(new File("/root"), "/user/.bashrc")).hasToString("/root/user/.bashrc");
	}

	@Test
	void listFiles() {
		assertThat(new File(".").listFiles())
			.extracting(File::getName, File::isDirectory)
			.contains(
				tuple("src", true),
				tuple("pom.xml", false)
			);
	}
}
