package pl.rg9.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Chapter14Test_Console {

	@Test
	void returnsNull_ifConsoleNotAvailable() {
		assertThat(System.console()).isNull();
	}

}
