package com.sp222kh.investigitor.repositories;

import java.util.List;
import java.util.Set;

import com.sp222kh.investigitor.models.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    List<Project> findByName(String name);

    @Query("SELECT id FROM Project")
    public Set<Long> findAllProjectIds();
}
