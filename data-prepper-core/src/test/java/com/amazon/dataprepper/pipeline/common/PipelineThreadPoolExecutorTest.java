package com.amazon.dataprepper.pipeline.common;

import com.amazon.dataprepper.pipeline.Pipeline;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PipelineThreadPoolExecutorTest {
    private static final int NUM_THREADS = 1;

    @Mock
    RunnableFuture runnableFuture;
    @Mock
    Runnable runnable;
    @Mock
    Throwable throwable;
    @Mock
    PipelineThreadFactory pipelineThreadFactory;
    @Mock
    Pipeline pipeline;

    PipelineThreadPoolExecutor sut;

    @Before
    public void setup() {
        sut = PipelineThreadPoolExecutor.newFixedThreadPool(NUM_THREADS,
                pipelineThreadFactory,
                pipeline);

    }

    @Test
    public void testAfterExecuteNonNullThrowable() {
        sut.afterExecute(null, throwable);

        verify(pipeline).shutdown();
    }

    @Test
    public void testAfterExecuteNonFutureRunnable() {
        sut.afterExecute(runnable, null);

        verify(pipeline, never()).shutdown();
    }

    @Test
    public void testAfterExecuteRunnableFuture() throws Exception {
        sut.afterExecute(runnableFuture, null);

        verify(runnableFuture).get();
        verify(pipeline, never()).shutdown();
    }

    @Test
    public void testAfterExecuteRunnableFutureThrowsCancellationException() throws Exception {
        when(runnableFuture.get()).thenThrow(CancellationException.class);

        sut.afterExecute(runnableFuture, null);

        verify(runnableFuture).get();
        verify(pipeline).shutdown();
    }

    @Test
    public void testAfterExecuteRunnableFutureThrowsExecutionException() throws Exception {
        when(runnableFuture.get()).thenThrow(ExecutionException.class);

        sut.afterExecute(runnableFuture, null);

        verify(runnableFuture).get();
        verify(pipeline).shutdown();
    }

    @Test
    public void testAfterExecuteRunnableFutureThrowsInterruptedException() throws Exception {
        when(runnableFuture.get()).thenThrow(InterruptedException.class);

        sut.afterExecute(runnableFuture, null);

        verify(runnableFuture).get();
        verify(pipeline).shutdown();
    }
}
