package com.sp222kh.investigitor.tasks;




import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;

import ch.qos.logback.core.Appender;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskRunnerTest {

    private TaskRunner taskRunner;

    private Logger log = (Logger) LoggerFactory.getLogger(TaskRunner.class);

    @Mock
    private Appender mockAppender;

    @Mock
    private Task task;

    @Mock
    private Task task1;

    @Mock
    private Task task2;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;


    @Test
    public void someTest() {
        assertEquals(1,1);
    }

    @Before
    public void setup() {
        taskRunner = new TaskRunner(new Task[]{task});
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
    public void shouldExecuteSeveralTasks() throws Exception {
        Task[] tasks = new Task[]{task, task1, task2};

        taskRunner = new TaskRunner(tasks);

        taskRunner.run();

        verify(task).run();
        verify(task1).run();
        verify(task2).run();
    }

    @Test
    public void shouldNotExecuteRestOfTasksOnFailure() throws Exception {
        doThrow(new Exception())
                .when(task1)
                .run();

        Task[] tasks = new Task[]{task, task1, task2};

        taskRunner = new TaskRunner(tasks);

        taskRunner.run();

        verify(task).run();
        verify(task1).run();
        verifyZeroInteractions(task2);
    }

    @Test
    public void shoulReturnFalseOnFailure() throws Exception {
        doThrow(new Exception())
                .when(task1)
                .run();

        Task[] tasks = new Task[]{task, task1, task2};

        taskRunner = new TaskRunner(tasks);

        assertFalse(taskRunner.run());
    }

    @Test
    public void shouldLogFailureReason() throws Exception {
        String reason = "Something went wrong";

        doThrow(new Exception(reason))
                .when(task1)
                .run();

        Task[] tasks = new Task[]{task, task1, task2};

        taskRunner = new TaskRunner(tasks);

        taskRunner.run();

        verify(mockAppender, times(4)).doAppend(captorLoggingEvent.capture());

        LoggingEvent event = captorLoggingEvent.getAllValues().get(3);

        assertEquals(task.getClass().getSimpleName() + " failed: " + reason, event.getMessage());
        assertEquals(Level.ERROR, event.getLevel());
    }

}
