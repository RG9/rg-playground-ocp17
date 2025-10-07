package pl.rg9.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class Chapter13Test_ExecutorService {

	int counter = 0;

	@Test
	void run_vs_start() {
		Runnable task = () -> {
			System.out.print(Thread.currentThread().getName() + ", ");
			counter++;
		};
		IntStream.range(0, 1_000)
//			.forEach(m -> new Thread(task).start()); // "start" starts new thread so result would be unpredictable
			.forEach(m -> new Thread(task).run()); // "runs" always in the invoking thread

		assertThat(counter).isEqualTo(1_000);
	}

	@Test
	void executorServiceDemo() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			executor.execute(() -> System.out.println("Hello from executor"));
			executor.execute(() -> System.out.println("Hello from executor 2"));
		} finally {
			executor.shutdown(); // otherwise JVM will wait forever
		}
	}

	@Test
	void rejectedExecutionExceptionWhenTaskSubmittedAfterShutdown() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.shutdown(); // doesn't accept new tasks - throws RejectedExecutionException
//		executor.shutdownNow();  // doesn't accept new tasks - throws RejectedExecutionException
		try {
			executor.execute(() -> System.out.println("Hello from executor after shutdown"));
		} catch (RejectedExecutionException e) {
			System.out.println("RejectedExecutionException");
		}
	}

	@Test
	void sleepInterruptedByShutdownNow() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			executor.execute(() -> {
				try {
					sleep();
				} catch (RuntimeException e) {
					System.out.println("sleep interrupted by shutdown now");
				}
			});
			executor.execute(() -> System.out.println("Won't be printed as shutdownNow removes all not started tasks"));
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	void awaitForTermination_returnsFalseInsteadOfThrowingTimeout() throws InterruptedException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> {
			sleep(5000);
			System.out.println("Executed");
		});
		executor.shutdown();
		assertThat(executor.awaitTermination(1, TimeUnit.SECONDS)).isFalse();
	}

	@Nested
	class ScheduledExecutorServiceTest {
		@Test
		void scheduleRunnable() throws InterruptedException, ExecutionException {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			ScheduledFuture<?> scheduledFuture = service.schedule(() -> System.out.println("Hello from Runnable"), 1, TimeUnit.SECONDS);
			service.shutdown();
			scheduledFuture.get();
		}

		@Test
		void scheduleCallable() throws InterruptedException, ExecutionException {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			ScheduledFuture<String> scheduledFuture = service.schedule(() -> {
				return "hello from callable";
			}, 1, TimeUnit.SECONDS);
			service.shutdown();
			System.out.println(scheduledFuture.get());
		}

		@Test
		void scheduleAtFixedRate() throws InterruptedException, ExecutionException {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			ScheduledFuture<?> scheduledFuture = service.scheduleAtFixedRate(() -> {
				System.out.println("Hello from at fixed rate task: " + LocalTime.now());
				sleep(0000);
				System.out.println("task finished: " + LocalTime.now());
			}, 0, 2, TimeUnit.SECONDS); // will wait for task to complete, but then starts immediately if overdue
//			scheduledFuture.get(); // waits indefinitely
			service.awaitTermination(15, TimeUnit.SECONDS);
			service.shutdownNow();
		}

		@Test
		void scheduleAtFixedDelay() throws InterruptedException {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			ScheduledFuture<?> scheduledFuture = service.scheduleWithFixedDelay(() -> {
				System.out.println("Hello from at fixed delay task: " + LocalTime.now());
				sleep(5000);
				System.out.println("task finished: " + LocalTime.now());
			}, 0, 2, TimeUnit.SECONDS); // will always wait 2 seconds to start new task
//			scheduledFuture.get(); // waits indefinitely
			service.awaitTermination(15, TimeUnit.SECONDS);
			service.shutdownNow();
		}
	}

	@Nested
	class AllVariantsOfSubmittingTasksToExecutorService {

		@Test
		void executeRunnable() throws Exception {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(() -> {
				sleep();
				System.out.println("Execute runnable 1 ");
			});
			executor.execute(() -> System.out.println("Execute runnable 2")); // execution will wait until previous task is finished

			sleep();
			sleep();
			System.out.println("shutdown");
			executor.shutdown();
		}

		@Test
		void submitRunnable() throws Exception {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<?> submit1 = executor.submit(() -> {
				sleep();
				System.out.println("Execute runnable 1 ");
			});
			Future<?> submit2 = executor.submit(() -> System.out.println("Execute runnable 2"));// execution will wait until previous task is finished

			executor.shutdown(); // executes tasks, so cancel of submit1 won't have any effect

			submit1.cancel(false);
			submit2.cancel(true);

			executor.awaitTermination(1, TimeUnit.MINUTES);
		}

		@Test
		void submitRunnableWithResult() throws Exception {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			var future = executor.submit(() -> {
				sleep();
				System.out.println("Submit runnable with result");
			}, "result");

			future.cancel(false);
			assertThat(future.isCancelled()).isTrue();
			assertThat(future.isDone()).isTrue();

			future = executor.submit(() -> {
				sleep();
				System.out.println("Submit runnable with result");
			}, "result");

			executor.shutdown();

			var result = future.get(1, TimeUnit.MINUTES);

			assertThat(future.isCancelled()).isFalse();
			assertThat(future.isDone()).isTrue();
			assertThat(result).isEqualTo("result");
		}

		@Test
		void futureCanceledBeforeGet() {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			var future = executor.submit(() -> {
				sleep();
				sleep();
				System.out.println("callable");
			});

			executor.shutdown();

			sleep();

			future.cancel(false);
			assertThat(future.isCancelled()).isTrue();
			assertThat(future.isDone()).isTrue();
			assertThatThrownBy(() -> future.get())
				.isInstanceOf(CancellationException.class);
		}

		@Test
		void futureTimeout() throws InterruptedException {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			var future = executor.submit(() -> {
				sleep();
			});

			executor.shutdown();

			assertThatThrownBy(() -> future.get(1, TimeUnit.MILLISECONDS))
				.isInstanceOf(TimeoutException.class);
			assertThat(future.isCancelled()).isFalse();
			assertThat(future.isDone()).isFalse();

			executor.awaitTermination(1, TimeUnit.MINUTES);

			assertThat(future.isDone()).isTrue();
		}

		@Test
		void submitCallable() throws InterruptedException, ExecutionException {
			ExecutorService executor = Executors.newSingleThreadExecutor();

			var future = executor.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "Submit callable";
				}
			});

			executor.shutdown();

			assertThat(future.get()).isEqualTo("Submit callable");
		}

		@Test
		void submitCallable_handlingException() throws Exception {
			ExecutorService executor = Executors.newSingleThreadExecutor();

			var future = executor.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					throw new IOException("Submit callable");
				}
			});

			executor.shutdown();

			assertThatThrownBy(() -> future.get())
				.isInstanceOf(ExecutionException.class)
				.hasCauseInstanceOf(IOException.class);
		}

		@Test
		void invokeAny() throws Exception {
			ExecutorService executor = Executors.newFixedThreadPool(10);

			var anyExecutedFuture = executor.invokeAny(IntStream.range(0, 10).boxed()
				.map(i -> new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						sleep();
						System.out.println(i);
						return i;
					}
				}).toList());

			executor.shutdown();

			assertThat(anyExecutedFuture).isBetween(0, 9); // not-started tasks will be cancelled
		}

		@Test
		void invokeAll() throws Exception {
			ExecutorService executor = Executors.newFixedThreadPool(10);

			var allFutures = executor.invokeAll(IntStream.range(0, 10).boxed()
				.map(i -> new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						sleep();
						System.out.println(i);
						return i;
					}
				}).toList());

			executor.shutdown();

			allFutures.forEach(future -> future.cancel(true)); // cannot cancel

			assertThat(allFutures)
				.extracting(Future::get)
				.hasSize(10);
		}
	}

	private static void sleep() {
		sleep(5000);
	}

	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}



}


