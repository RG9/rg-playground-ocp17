package pl.rg9.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class Chapter13Test_Threads {

	int counter = 0;

	@Test
	void simpleThreadsDemo() {
		System.out.println("starting thread ...");
		new Thread(() -> System.out.println("Hello from lambda")).start();
		System.out.println("thread started");

		var deamonThread = new Thread(() -> {
			sleep(1000);
			System.out.println("Hello from deamon thread");
		}, "deamonThread");
		deamonThread.setDaemon(true);
		deamonThread.start();
	}

	@Test
	void interrupt_timedWating() {
		var thread = new Thread(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			}
		});
		thread.start();
		sleep(1000);
		assertThat(thread.getState()).isEqualTo(Thread.State.TIMED_WAITING);
		assertThat(thread.isInterrupted()).isFalse();

		thread.interrupt();
		assertThat(thread.isInterrupted()).isTrue();
		sleep(1000);
		assertThat(thread.getState()).isEqualTo(Thread.State.TERMINATED);
	}

	@Test
	void interrupt_waiting_lock() {
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		var thread = new Thread(() -> {
			System.out.println("locking");
			lock.lock(); // not responds to interrupts -> stil waits
			System.out.println("Hello after lock acquired thread");
		});
		thread.start();
		sleep(1000);
		assertThat(thread.getState()).isEqualTo(Thread.State.WAITING);
		assertThat(thread.isInterrupted()).isFalse();

		thread.interrupt();
		assertThat(thread.isInterrupted()).isTrue();
		sleep(1000);
		assertThat(thread.getState()).isEqualTo(Thread.State.WAITING);
	}

	@Test
	void interrupt_waiting_lockInterruptibly() {
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		var thread = new Thread(() -> {
			System.out.println("locking");
			try {
				lock.lockInterruptibly();
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			}
			System.out.println("Hello after lock acquired thread");
		});
		thread.start();
		sleep(1000);
		assertThat(thread.getState()).isEqualTo(Thread.State.WAITING);
		assertThat(thread.isInterrupted()).isFalse();

		thread.interrupt();
		assertThat(thread.isInterrupted()).isTrue();
		sleep(1000);
		assertThat(thread.getState()).isEqualTo(Thread.State.TERMINATED);
	}

	@Test
	void interrupt_waiting_checkIsInterrupted() {
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		var thread = new Thread(() -> {
			System.out.println("locking");
			while (!lock.tryLock()) {
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("interrupted");
					return;
				}
			}
			System.out.println("Hello after lock acquired thread");
		});
		thread.start();
		sleep(1000);
		assertThat(thread.getState()).isEqualTo(Thread.State.RUNNABLE);
		assertThat(thread.isInterrupted()).isFalse();

		thread.interrupt();
		sleep(1000);
		assertThat(thread.getState()).isEqualTo(Thread.State.TERMINATED);
	}

	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void deadlock() throws InterruptedException {

		Object o1 = new Object();
		Object o2 = new Object();
		var executor = Executors.newFixedThreadPool(2);
		IntStream.range(0, 10_000).forEach(i -> {
			executor.submit(() -> {
				synchronized (o1) {
					synchronized (o2) {
						counter++;
					}
				}
			});
			executor.submit(() -> {
				synchronized (o2) {
					synchronized (o1) {
						counter++;
					}
				}
			});
		});

		executor.shutdown();
		while (!isDeadlock()) {
			executor.awaitTermination(1, TimeUnit.SECONDS);
		}

		System.out.println(counter);
		System.out.println(getThreadNameAndStatuses()
			.stream()
			.collect(Collectors.joining("\n")));
		assertThat(counter).as("there should be deadlock").isLessThan(2 * 10_000);
	}

	private boolean isDeadlock() {
		return getThreadNameAndStatuses()
			.stream()
			.filter(line -> line.startsWith("pool-") && line.endsWith("BLOCKED"))
			.count() >= 2;
	}

	private static List<String> getThreadNameAndStatuses() {
		return Thread.getAllStackTraces().keySet().stream()
			.map(thread -> thread.getName() + " | " + thread.getState())
			.toList();
	}
}
