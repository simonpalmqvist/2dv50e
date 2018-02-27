package com.sp222kh.investigitor.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskRunner {

    private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);

    private Task[] tasks;

    public TaskRunner(Task[] tasks) {
        this.tasks = tasks;
    }

    public boolean run() {
        for(Task task : tasks) {
            String name = task.getClass().getSimpleName();

            try {
                log.info(name + " started");
                task.run();
                log.info(name + " finished");
            }
            catch(Exception e) {
                log.error(name + " failed: " + e.getMessage());
                return false;
            }
        }

        return true;
    }

}
