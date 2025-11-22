package pl.rg9.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class Chapter14Test_NIO2 {

	@Test
	void create_path_reference() {
		assertThatThrownBy(() -> Path.of(null)).isInstanceOf(NullPointerException.class);
		assertThat(Path.of("")).hasToString("");
		assertThat(Path.of(".")).hasToString(".");
		assertThat(Path.of("../")).hasToString("..");
		assertThat(Path.of("Chapter14/pom.xml")).hasToString("Chapter14/pom.xml");
		assertThat(Paths.get("Chapter14/pom.xml")).hasToString("Chapter14/pom.xml");
		assertThat(FileSystems.getDefault().getPath("Chapter14/pom.xml")).hasToString("Chapter14/pom.xml");
	}

	@Test
	void path_subPath() {
		var path = Path.of("/zoo/animals/bear/koala.txt");
		assertThat(path.subpath(0, 2)).hasToString("zoo/animals");
		assertThat(path.subpath(1, 4)).hasToString("animals/bear/koala.txt");
		assertThatCode(() -> path.subpath(1, 5)).isInstanceOf(IllegalArgumentException.class);
		assertThatCode(() -> path.subpath(-1, 1)).isInstanceOf(IllegalArgumentException.class);

		assertThat(path.getName(0)).hasToString("zoo"); // returns Path
		assertThat(path.getName(2)).hasToString("bear");
		assertThatCode(() -> path.getName(10)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void realPath() throws IOException {
		assertThat(Paths.get(".").toRealPath().toString()).endsWith("/Chapter14");

		assertThatCode(() -> Paths.get("/not-existing").toRealPath()).isInstanceOf(NoSuchFileException.class);
	}

	@Test
	void listFiles() throws IOException {
		try (var list = Files.list(Paths.get("."))) {
			assertThat(list)
				.extracting(path -> {
						Path fileName = path.getFileName();
						return fileName.toString();
					},
					Files::isDirectory)
				.contains(
					tuple("src", true),
					tuple("pom.xml", false)
				);
		}
	}

	@Test
	void sameFile_whetherPathsReferToTheSameFile() throws IOException {
		var tempDir = Files.createTempDirectory("sameFileTest");
		var testFile = tempDir.resolve("test.txt");
		Files.write(testFile, List.of("test"));
		var link = tempDir.resolve("link.txt");
		Files.createSymbolicLink(link, testFile);
		assertThat(link.toRealPath()).isEqualTo(testFile);

		assertThat(Files.isSameFile(testFile, link)).isTrue();
		assertThat(Files.mismatch(testFile, link)).isEqualTo(-1);

		assertThatCode(() -> Files.isSameFile(testFile, Path.of("abc")))
			.isInstanceOf(NoSuchFileException.class);
		assertThatCode(() -> Files.mismatch(testFile, Path.of("abc")))
			.isInstanceOf(NoSuchFileException.class);
	}

	@Test
	void readAndWriteAttributes() throws IOException {
		var tmpFile = Files.createTempFile("test", "123");

		var basicFileAttributes = Files.readAttributes(tmpFile, BasicFileAttributes.class);

		assertThat(basicFileAttributes.isRegularFile()).isTrue();
		assertThat(basicFileAttributes.isDirectory()).isFalse();
		assertThat(basicFileAttributes.isOther()).isFalse();
		assertThat(basicFileAttributes.size()).isZero();
		assertThat(basicFileAttributes.creationTime()).isNotNull(); // e.g. 2025-10-29T19:53:21.373017685Z
		assertThat(basicFileAttributes.lastModifiedTime()).isEqualTo(basicFileAttributes.creationTime());
		assertThat(basicFileAttributes.lastAccessTime()).isEqualTo(basicFileAttributes.creationTime());

		var posixFileAttributes = Files.readAttributes(tmpFile, PosixFileAttributes.class);
		assertThat(posixFileAttributes.owner().getName()).isEqualTo("rafal");
		assertThat(posixFileAttributes.group().getName()).isEqualTo("rafal");
		assertThat(posixFileAttributes.permissions())
			.extracting(p -> p.name())
			.containsExactlyInAnyOrder("OWNER_WRITE", "OWNER_READ");

		assertThatCode(() -> Files.setAttribute(tmpFile, "posix:owner", "root"))
			.hasMessageContaining("class java.lang.String cannot be cast to class java.nio.file.attribute.UserPrincipal");
		// Files.setOwner(tmpFile, null);
		assertThat(((UserPrincipal) Files.getAttribute(tmpFile, "posix:owner")).getName()).isEqualTo("rafal");
//		Files.getOwner(tmpFile);

		Files.setLastModifiedTime(tmpFile, FileTime.from(Instant.EPOCH));
		assertThat(((FileTime) Files.getAttribute(tmpFile, "lastModifiedTime")).toInstant()).isEqualTo(Instant.EPOCH);

		var fileAttributeView = Files.getFileAttributeView(tmpFile, PosixFileAttributeView.class);
		fileAttributeView.setTimes(FileTime.from(Instant.EPOCH), FileTime.from(Instant.EPOCH), FileTime.from(Instant.EPOCH));
		fileAttributeView.setPermissions(Set.of(PosixFilePermission.OWNER_WRITE, PosixFilePermission.GROUP_READ));
		assertThatCode(() -> fileAttributeView.setTimes(FileTime.from(Instant.EPOCH), FileTime.from(Instant.EPOCH), FileTime.from(Instant.EPOCH)))
			.as("after changing permissions")
			.isInstanceOf(AccessDeniedException.class);
		assertThatCode(() -> Files.setLastModifiedTime(tmpFile, FileTime.from(Instant.EPOCH)))
			.isInstanceOf(AccessDeniedException.class);

		var attr2 = Files.readAttributes(tmpFile, PosixFileAttributes.class);
		assertThat(attr2.creationTime()).isEqualTo(FileTime.from(Instant.EPOCH));
		assertThat(attr2.lastAccessTime()).isEqualTo(FileTime.from(Instant.EPOCH));
		assertThat(attr2.permissions())
			.extracting(p -> p.name())
			.containsExactlyInAnyOrder("OWNER_WRITE", "GROUP_READ");

		Files.setPosixFilePermissions(tmpFile, Set.of(PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_READ));
		assertThat(Files.getPosixFilePermissions(tmpFile))
			.extracting(p -> p.name())
			.containsExactlyInAnyOrder("OWNER_WRITE", "OWNER_READ");

	}

	@Test
	void walk() throws IOException {
		var tempDir = Files.createTempDirectory("sameFileTest");
		Files.write(tempDir.resolve("test.txt"), List.of("test"));
		Files.write(tempDir.resolve("test2.txt"), List.of("test2"));
		var books = tempDir.resolve("books");
		Files.createDirectory(books);
		Files.createFile(books.resolve("Clean Code"));
		Files.createFile(books.resolve("Clean Coder"));

		assertThat(Files.walk(tempDir)
			.map(path -> tempDir.relativize(path))
			.map(Path::toString))
			.containsExactlyInAnyOrder(
				"", "books", "books/Clean Coder", "books/Clean Code", "test2.txt", "test.txt"
			);

		assertThat(Files.walk(tempDir, 1)
			.map(path -> tempDir.relativize(path))
			.map(Path::toString))
			.containsExactlyInAnyOrder(
				"", "books", "test2.txt", "test.txt"
			);

		assertThat(Files.walk(tempDir, 1, FileVisitOption.FOLLOW_LINKS) // FOLLOW_LINKS may throw FileSystemLoopException
			.map(path -> tempDir.relativize(path))
			.map(Path::toString))
			.containsExactlyInAnyOrder(
				"", "books", "test2.txt", "test.txt"
			);

		assertThat(Files.find(tempDir, 5, (path, attr) -> attr.isRegularFile()
				&& path.getFileName().toString().endsWith(".txt"))
			.map(path -> tempDir.relativize(path))
			.map(Path::toString))
			.containsExactlyInAnyOrder(
				"test2.txt", "test.txt"
			);
	}

	@Test
	void createDir() throws IOException {
		var tempDir = Files.createTempDirectory("sameFileTest");

		var testDir = Files.createDirectory(tempDir.resolve("test"));

		assertThatCode(() -> Files.createDirectory(testDir))
			.isInstanceOf(FileAlreadyExistsException.class);

		assertThatCode(() -> Files.createDirectory(Path.of("non-existent-root-dir").resolve("test")))
			.isInstanceOf(NoSuchFileException.class);

		Files.createDirectories(tempDir.resolve("test2").resolve("child"));

		assertThat(Files.walk(tempDir)
			.map(path -> tempDir.relativize(path))
			.map(Path::toString))
			.containsExactlyInAnyOrder(
				"", "test2", "test2/child", "test"
			);
	}

	@Test
	void copy() throws IOException {
		var tempDir = Files.createTempDirectory("copy");
		var test1 = tempDir.resolve("test1");
		Files.writeString(test1, "test1");

		Files.copy(test1, tempDir.resolve("test1"));

		Files.copy(test1, tempDir.resolve("test2"));
		assertThatCode(() -> Files.copy(test1, tempDir.resolve("test2"))).isInstanceOf(FileAlreadyExistsException.class);
		Files.copy(test1, tempDir.resolve("test2"), StandardCopyOption.REPLACE_EXISTING);

		assertThatCode(() -> Files.copy(test1, tempDir.resolve("test3"), StandardCopyOption.ATOMIC_MOVE))
			.isInstanceOf(UnsupportedOperationException.class)
			.hasMessageContaining("Unsupported copy option");

		Files.copy(test1, tempDir.resolve("test3"), StandardCopyOption.COPY_ATTRIBUTES);

		assertThat(Files.walk(tempDir)
			.map(path -> tempDir.relativize(path))
			.map(Path::toString))
			.containsExactlyInAnyOrder(
				"", "test3", "test2", "test1"
			);
	}

	@Test
	void move() throws IOException {
		var dir1 = Files.createTempDirectory("dir1");
		var test1 = dir1.resolve("test1");
		Files.writeString(test1, "test1");
		var dir2 = Files.createTempDirectory("dir2");

		Files.move(test1, dir1.resolve("test1"));

		Files.move(test1, dir1.resolve("test2")); // rename
		Files.move(dir1.resolve("test2"), test1); // rename back

		Files.move(test1, dir2.resolve("test3"), StandardCopyOption.ATOMIC_MOVE);

		assertThat(Files.walk(dir1)
			.map(path -> dir1.relativize(path))
			.map(Path::toString))
			.containsExactlyInAnyOrder(
				""
			);
		assertThat(Files.walk(dir2)
			.map(path -> dir2.relativize(path))
			.map(Path::toString))
			.containsExactlyInAnyOrder(
				"", "test3"
			);
	}
}
