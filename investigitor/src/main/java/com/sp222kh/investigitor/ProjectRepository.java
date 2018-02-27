package com.sp222kh.investigitor;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    List<Project> findByName(String name);

    @Query("SELECT id FROM Project")
    public Set<Long> findAllProjectIds();
}
