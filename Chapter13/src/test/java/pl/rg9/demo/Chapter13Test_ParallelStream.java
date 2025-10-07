package pl.rg9.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class Chapter13Test_ParallelStream {

	@Test
	void forEachOrdered() {
		var data = List.of(3, 2, 1);
		data.stream()
			.parallel()
//			.peek(System.out::println).toList() // prints undetermined result, e.g. 1, 2, 3
//			.forEachOrdered(System.out::println) // prints 3, 2, 1
			.forEach(System.out::println) // prints undetermined result, e.g. 2, 1, 3
		//
		;
	}

	@Test
	void of_vs_concat() {
		var stream1 = Stream.of("1").parallel();
		assertThat(stream1.isParallel()).isTrue();

		var stream2 = Stream.of("2").parallel();
		var streamOf = Stream.of(stream1, stream2).flatMap(s -> s);
		assertThat(streamOf.isParallel()).isFalse();

		var streamConcat = Stream.concat(stream1, stream2);
		assertThat(streamConcat.isParallel()).isTrue();
	}
}
