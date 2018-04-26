package com.sp222kh.investigitor.repositories;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import com.sp222kh.investigitor.models.Project;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    List<Project> findByName(String name);

    @Query("SELECT id FROM Project")
    public Set<Long> findAllProjectIds();


    @Modifying
    @Transactional
    @Query("DELETE FROM Project p WHERE p.id IN :ids")
    public void deleteDuplicateProjects(@Param("ids") Set<Long> ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM project p WHERE NOT (" +
            "(SELECT COUNT(w.project_id) FROM watcher w WHERE w.project_id = p.id) > 1 AND" +
            "(SELECT COUNT(DISTINCT c.author_id) FROM commit c WHERE c.project_id = p.id) > 1 AND" +
            "(SELECT MAX(c.created_at) FROM commit c WHERE c.project_id = p.id) - p.created_at > interval '99 days'" +
            ")", nativeQuery = true)
    public void deleteNonQualityProjects();

    @Modifying
    @Transactional
    @Query("DELETE FROM Project p WHERE deleted = TRUE OR language != 'Java'")
    public void deleteNonJavaProjects();

    @Modifying
    @Transactional
    @Query("DELETE FROM Watcher w WHERE NOT EXISTS (SELECT p.id FROM Project p WHERE p.id = w.projectId)")
    public void deleteWatchersWithoutProject();
}
