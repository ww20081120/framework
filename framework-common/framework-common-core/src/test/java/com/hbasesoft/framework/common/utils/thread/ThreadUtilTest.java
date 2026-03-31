/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.thread;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ThreadUtil 测试类
 *
 * @author 王伟
 * @version 1.0
 * @taskId
 * @CreateDate 2018年12月27日
 * @since V1.0
 * @see com.hbasesoft.framework.common.utils.thread
 */
@DisplayName("线程工具类测试")
class ThreadUtilTest {

    @Test
    @DisplayName("应该禁止实例化 ThreadUtil 工具类")
    void shouldThrowExceptionWhenInstantiate() {
        // Given
        final var constructor = ThreadUtil.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        // When & Then
        assertThatThrownBy(() -> constructor.newInstance())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Utility class cannot be instantiated");
    }

    @Test
    @DisplayName("应该成功执行虚拟线程任务")
    void shouldExecuteVirtualThreadSuccessfully() throws InterruptedException {
        // Given
        final var executed = new AtomicBoolean(false);
        final var latch = new CountDownLatch(1);
        final var taskName = "test-task";
        final var threadNameRef = new AtomicReference<String>();

        final Runnable task = () -> {
            threadNameRef.set(Thread.currentThread().getName());
            executed.set(true);
            latch.countDown();
        };

        // When
        ThreadUtil.execute(taskName, task);

        // Then
        final var completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed)
                .as("任务应该在5秒内完成")
                .isTrue();
        assertThat(executed.get())
                .as("任务应该被执行")
                .isTrue();
        assertThat(threadNameRef.get())
                .as("线程名应该包含任务名称")
                .contains(taskName);
    }

    @Test
    @DisplayName("应该支持并发执行多个任务")
    void shouldExecuteMultipleConcurrentTasks() throws InterruptedException {
        // Given
        final var taskCount = 10;
        final var executionLatch = new CountDownLatch(taskCount);
        final var completionLatch = new CountDownLatch(taskCount);

        // When
        for (int i = 0; i < taskCount; i++) {
            final var taskId = i;
            final var task = new ThreadUtilTestRunnable(executionLatch, completionLatch);
            ThreadUtil.execute("concurrent-task-" + taskId, task);
        }

        // Then
        final var allStarted = executionLatch.await(5, TimeUnit.SECONDS);
        final var allCompleted = completionLatch.await(5, TimeUnit.SECONDS);

        assertThat(allStarted)
                .as("所有任务应该开始执行")
                .isTrue();
        assertThat(allCompleted)
                .as("所有任务应该完成执行")
                .isTrue();
    }

    @Test
    @DisplayName("应该处理带有异常的任务")
    void shouldHandleTaskWithException() throws InterruptedException {
        // Given
        final var latch = new CountDownLatch(1);
        final var executed = new AtomicBoolean(false);

        final Runnable task = () -> {
            executed.set(true);
            latch.countDown();
            throw new RuntimeException("测试异常");
        };

        // When
        ThreadUtil.execute("exception-task", task);

        // Then
        final var completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed)
                .as("任务应该被执行（即使抛出异常）")
                .isTrue();
        assertThat(executed.get())
                .as("任务应该被执行")
                .isTrue();
    }

    @Test
    @DisplayName("应该为虚拟线程创建包含当前线程ID的名称")
    void shouldCreateThreadNameWithCurrentThreadId() throws InterruptedException {
        // Given
        final var latch = new CountDownLatch(1);
        final var threadNameRef = new AtomicReference<String>();
        final var currentThreadId = Thread.currentThread().threadId();
        final var taskName = "thread-id-test";

        final Runnable task = () -> {
            threadNameRef.set(Thread.currentThread().getName());
            latch.countDown();
        };

        // When
        ThreadUtil.execute(taskName, task);

        // Then
        final var completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed)
                .as("任务应该在5秒内完成")
                .isTrue();
        assertThat(threadNameRef.get())
                .as("线程名应该包含任务名称和当前线程ID")
                .contains(taskName)
                .contains(String.valueOf(currentThreadId));
    }

    @Test
    @DisplayName("应该支持空名称的任务")
    void shouldHandleTaskWithEmptyName() throws InterruptedException {
        // Given
        final var executed = new AtomicBoolean(false);
        final var latch = new CountDownLatch(1);
        final var threadNameRef = new AtomicReference<String>();

        final Runnable task = () -> {
            threadNameRef.set(Thread.currentThread().getName());
            executed.set(true);
            latch.countDown();
        };

        // When
        ThreadUtil.execute("", task);

        // Then
        final var completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed)
                .as("任务应该在5秒内完成")
                .isTrue();
        assertThat(executed.get())
                .as("任务应该被执行")
                .isTrue();
        assertThat(threadNameRef.get())
                .as("线程名应该包含当前线程ID")
                .isNotEmpty();
    }

    @Test
    @DisplayName("应该支持执行长时间运行的任务")
    void shouldHandleLongRunningTask() throws InterruptedException {
        // Given
        final var latch = new CountDownLatch(1);
        final var executed = new AtomicBoolean(false);

        final Runnable task = () -> {
            try {
                // 模拟长时间运行的任务
                Thread.sleep(100);
                executed.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        };

        // When
        ThreadUtil.execute("long-running-task", task);

        // Then
        final var completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed)
                .as("长时间运行的任务应该完成")
                .isTrue();
        assertThat(executed.get())
                .as("任务应该成功执行")
                .isTrue();
    }

    /**
     * 测试用 Runnable 实现
     */
    private static class ThreadUtilTestRunnable implements Runnable {
        private final CountDownLatch executionLatch;
        private final CountDownLatch completionLatch;

        ThreadUtilTestRunnable(CountDownLatch executionLatch, CountDownLatch completionLatch) {
            this.executionLatch = executionLatch;
            this.completionLatch = completionLatch;
        }

        @Override
        public void run() {
            executionLatch.countDown();
            try {
                // 模拟一些工作
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                completionLatch.countDown();
            }
        }
    }
}
