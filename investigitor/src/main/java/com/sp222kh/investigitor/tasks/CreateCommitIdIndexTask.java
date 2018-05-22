package com.sp222kh.investigitor.tasks;

import java.sql.Connection;
import java.sql.Statement;

public class CreateCommitIdIndexTask implements Task {

    private final Connection connection;

    public CreateCommitIdIndexTask(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() throws Exception {
        Statement statement = connection.createStatement();
        statement.execute("CREATE INDEX ON project_commit (commit_id)");
    }
}
