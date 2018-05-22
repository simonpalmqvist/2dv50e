package com.sp222kh.investigitor.tasks;

import java.sql.Connection;

public class CreateIndexesTask implements Task {

    private final Connection connection;

    public CreateIndexesTask(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() throws Exception {
        connection.createStatement().execute("CREATE INDEX ON project_commit (project_id)");
        connection.createStatement().execute("CREATE INDEX ON watcher (project_id)");
    }
}
