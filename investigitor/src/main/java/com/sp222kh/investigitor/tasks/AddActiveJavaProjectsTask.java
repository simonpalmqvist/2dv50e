package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.models.Project;
import com.sp222kh.investigitor.repositories.ProjectRepository;
import com.sp222kh.investigitor.csv.ProjectItem;
import net.sf.jsefa.Deserializer;

import java.io.FileReader;

public class AddActiveJavaProjectsTask implements Task {
    private ProjectRepository repository;
    private Deserializer deserializer;
    private String file;

    public AddActiveJavaProjectsTask(ProjectRepository repository, Deserializer deserializer,
                                     String file) {
        this.repository = repository;
        this.deserializer = deserializer;
        this.file = file;
    }

    @Override
    public void run() throws Exception {
        deserializer.open(new FileReader(file));

        while (deserializer.hasNext()) {
            ProjectItem p = deserializer.next();

            if (!p.deleted && p.language.equals("Java")) {
                repository.save(new Project(p));
            }
        }

        deserializer.close(true);
    }
}
