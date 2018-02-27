package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.models.Status;
import com.sp222kh.investigitor.repositories.StatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskRunner {

    private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);

    private Task[] tasks;
    private StatusRepository repository;

    public TaskRunner(Task[] tasks, StatusRepository repository) {
        this.tasks = tasks;
        this.repository = repository;
    }

    public boolean run() {
        for(Task task : tasks) {
            String name = task.getClass().getSimpleName();

            Status status = repository.findByName(name);
            if(status == null) status = new Status(name);

            // Only run step if it hasn't been run before
            if(!status.getIsFinished()) {
                try {
                    log.info(name + " started");
                    runTask(task, status);
                    log.info(name + " finished");

                }
                catch(Exception e) {
                    log.error(name + " failed: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            } else {
                log.info(name + " skipped");
            }
        }

        return true;
    }

    void runTask(Task task, Status status) throws Exception {
        status.started();
        task.run();
        status.finished();
        repository.save(status);
    }

}
