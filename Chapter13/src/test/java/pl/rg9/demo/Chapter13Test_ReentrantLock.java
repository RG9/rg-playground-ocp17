package pl.rg9.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class Chapter13Test_ReentrantLock {

	@Nested
	class TestCount {
		private int count = 0;

		private void testCount(Runnable counter) throws InterruptedException {
			ExecutorService executor = Executors.newFixedThreadPool(10);
			IntStream.range(0, 100_000)
				.forEach(i -> {
					executor.execute(() -> {
						System.out.println(Thread.currentThread().getName() + " - " + i);
						counter.run();
					});
				});
			executor.shutdown();
			executor.awaitTermination(30, TimeUnit.SECONDS);

			assertThat(count).isEqualTo(100_000);
		}

		@Disabled // not synchronized
		@Test
		void not_synchronized() throws Exception {
			testCount(() -> count++);
		}

		@Test
		void _synchronized() throws Exception {
			testCount(() -> {
				synchronized (Chapter13Test_ReentrantLock.this) {
					count++;
				}
			});
		}

		@Test
		void basic_usage() throws Exception {
			ReentrantLock lock = new ReentrantLock();
			assertThat(lock.isLocked()).isFalse();
			assertThat(lock.isFair()).isFalse();
			assertThat(lock.hasQueuedThreads()).isFalse();
			assertThat(lock.hasQueuedThread(Thread.currentThread())).isFalse();
			assertThat(lock.getHoldCount()).isEqualTo(0);

			testCount(() -> {
				lock.lock();

				assertThat(lock.isLocked()).isTrue();
				assertThat(lock.getHoldCount()).isEqualTo(1);

				try {
					count++;
				} finally {
					lock.unlock();
				}
			});
		}

		@Test
		void fair() throws Exception {
			// "When set true, under contention, locks favor granting access to the longest-waiting thread. " - order is much more predictable
			// "Programs using fair locks accessed by many threads may display lower overall throughput"
			ReentrantLock lock = new ReentrantLock(true);
			testCount(() -> {
				lock.lock();
				try {
					count++;
				} finally {
					lock.unlock();
				}
			});
		}

		@Test
		void tryLock() throws Exception {
			ReentrantLock lock = new ReentrantLock();
			testCount(() -> {
				while (true) {
					if (lock.tryLock()) {
						try {
							if (count % 1000 == 0) {
								sleep(50);
							}
							count++;
						} finally {
							lock.unlock();
							break;
						}
					} else {
						System.out.println("waiting fbbnbhjhjhor lock");
						sleep(100);
					}
				}
			});
		}
	}

	@Test
	void lockTwice() {
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		assertThat(lock.isLocked()).isTrue();
		assertThat(lock.getHoldCount()).isEqualTo(1);

		lock.lock();
		assertThat(lock.isLocked()).isTrue();
		assertThat(lock.getHoldCount()).isEqualTo(2);

		lock.unlock();
		assertThat(lock.isLocked()).isTrue();
		assertThat(lock.getHoldCount()).isEqualTo(1);
	}

	@Test
	void lockTwice_tryLock() {
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		assertThat(lock.isLocked()).isTrue();
		assertThat(lock.getHoldCount()).isEqualTo(1);

		lock.tryLock();
		assertThat(lock.isLocked()).isTrue();
		assertThat(lock.getHoldCount()).isEqualTo(2);

		lock.unlock();
		assertThat(lock.isLocked()).isTrue();
		assertThat(lock.getHoldCount()).isEqualTo(1);
	}

	@Test
	void unlockTwice() {
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		assertThat(lock.isLocked()).isTrue();
		assertThat(lock.getHoldCount()).isEqualTo(1);

		lock.unlock();
		assertThat(lock.isLocked()).isFalse();
		assertThat(lock.getHoldCount()).isEqualTo(0);

		assertThatThrownBy(() -> lock.unlock())
			.isInstanceOf(IllegalMonitorStateException.class);
	}

	@Test
	void tryLockTimeout() {
		ReentrantLock lock = new ReentrantLock(true);
		new Thread(() -> {
			lock.lock(); // thread 1 holds lock
		}).start();

		new Thread(() -> {
			try {
				System.out.println("2nd thread");
				if (lock.tryLock(2_000, TimeUnit.MILLISECONDS)) { // waits up to 2 seconds for the lock
					System.out.println("locked 2");
				} else {
					System.out.println("not locked 2 after 2 seconds");
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}).start();

		sleep(3_000);
	}

	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
