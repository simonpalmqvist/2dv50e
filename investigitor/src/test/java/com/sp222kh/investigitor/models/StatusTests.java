package com.sp222kh.investigitor.models;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class StatusTests {

    private Status status;

    @Before
    public void setup() {
        status = new Status("Test");
    }

    @Test
    public void shouldHaveCorrectInitialValues() {
        assertEquals(status.getName(), "Test");
        assertNull(status.getStartedAt());
        assertNull(status.getFinishedAt());
        assertFalse(status.getIsFinished());
    }

    @Test
    public void shouldSetStartDateWhenStarted() {
        status.started();
        assertNotNull(status.getStartedAt());
    }

    @Test
    public void shouldBeAbleToSetStartTwice() throws InterruptedException {
        status.started();

        Date start = status.getStartedAt();
        sleep(1);
        status.started();

        assertNotEquals(start, status.getStartedAt());
    }

    @Test
    public void shouldSetIsFinishedWhenFinished() {
        status.finished();
        assertTrue(status.getIsFinished());
    }

    @Test
    public void shouldSetFinishedDateWhenFinished() {
        status.finished();
        assertNotNull(status.getFinishedAt());
    }

    @Test
    public void shouldBeAbleToSetFinishedTwice() throws InterruptedException {
        status.finished();

        Date finished = status.getFinishedAt();
        sleep(1);
        status.finished();

        assertNotEquals(finished, status.getFinishedAt());
    }
}
