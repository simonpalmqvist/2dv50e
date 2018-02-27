package com.sp222kh.investigitor.tasks;




import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;

import ch.qos.logback.core.Appender;
import com.sp222kh.investigitor.models.Status;
import com.sp222kh.investigitor.repositories.StatusRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskRunnerTests {

    private TaskRunner taskRunner;

    private TaskRunner taskRunner2;

    private Logger log = (Logger) LoggerFactory.getLogger(TaskRunner.class);

    @Mock
    private StatusRepository repository;

    @Mock
    private Appender mockAppender;

    @Mock
    private Task task;

    @Mock
    private Task1 task1;

    @Mock
    private Task2 task2;

    @Mock
    private Status status;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;


    @Test
    public void someTest() {
        assertEquals(1,1);
    }

    @Before
    public void setup() {
        taskRunner = new TaskRunner(new Task[]{task}, repository);
        taskRunner2 = new TaskRunner(new Task[]{task, task1, task2}, repository);
        log.addAppender(mockAppender);
    }

    @After
    public void tearDown() {
        log.detachAppender(mockAppender);
    }

    @Test
    public void shouldExecuteOneTask() throws Exception {
        taskRunner.run();

        verify(task).run();
    }

    @Test
    public void shouldReturnTrueWhenAllTasksAreDone() throws Exception {
        assertTrue(taskRunner.run());
    }

    @Test
    public void shouldLogWhenTaskIsStartedAndFinished() {
        taskRunner.run();

        verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());

        assertEquals(
                task.getClass().getSimpleName() + " started",
                captorLoggingEvent.getAllValues().get(0).getMessage()
        );

        assertEquals(
                task.getClass().getSimpleName() + " finished",
                captorLoggingEvent.getAllValues().get(1).getMessage()
        );
    }

    @Test
    public void shouldLogWhenTaskIsSkipped() {
        String name = task.getClass().getSimpleName();

        when(status.getIsFinished()).thenReturn(true);
        when(repository.findByName(name)).thenReturn(status);

        taskRunner.run();

        verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());

        assertEquals(
                name + " skipped",
                captorLoggingEvent.getAllValues().get(0).getMessage()
        );
    }

    @Test
    public void shouldExecuteSeveralTasks() throws Exception {
        taskRunner2.run();

        verify(task).run();
        verify(task1).run();
        verify(task2).run();
    }

    @Test
    public void shouldNotExecuteRestOfTasksOnFailure() throws Exception {
        doThrow(new Exception())
                .when(task1)
                .run();

        taskRunner2.run();

        verify(task).run();
        verify(task1).run();
        verifyZeroInteractions(task2);
    }

    @Test
    public void shouldSkipAlreadyDoneTask() throws Exception {
        String name = task.getClass().getSimpleName();

        when(status.getIsFinished()).thenReturn(true);
        when(repository.findByName(name)).thenReturn(status);

        taskRunner2.run();

        verifyZeroInteractions(task);
        verify(task1).run();
        verify(task2).run();

    }

    @Test
    public void shoulReturnFalseOnFailure() throws Exception {
        doThrow(new Exception())
                .when(task1)
                .run();

        assertFalse(taskRunner2.run());
    }

    @Test
    public void shouldLogFailureReason() throws Exception {
        String reason = "Something went wrong";

        doThrow(new Exception(reason))
                .when(task1)
                .run();

        taskRunner2.run();

        verify(mockAppender, times(4)).doAppend(captorLoggingEvent.capture());

        LoggingEvent event = captorLoggingEvent.getAllValues().get(3);

        assertEquals(task1.getClass().getSimpleName() + " failed: " + reason, event.getMessage());
        assertEquals(Level.ERROR, event.getLevel());
    }

    @Test
    public void shouldExecuteMethodsInRightOrder() throws Exception {
        InOrder inOrder = inOrder(task, status, repository);

        taskRunner.runTask(task, status);

        inOrder.verify(status).started();
        inOrder.verify(task).run();
        inOrder.verify(status).finished();
        inOrder.verify(repository).save(status);
    }

    class Task1 implements Task { @Override public void run() throws Exception {} }

    class Task2 implements Task { @Override public void run() throws Exception {} }
}
