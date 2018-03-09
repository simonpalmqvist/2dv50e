package com.sp222kh.investigitor.repositories;

import com.sp222kh.investigitor.models.Commit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

public interface CommitRepository extends CrudRepository<Commit, Long> {

    List<Commit> findByProjectId(Long projectId);

    @Query("SELECT COUNT(DISTINCT authorId) FROM Commit c WHERE projectId= ?1")
    public int findContributorsForProject(Long projectId);

    @Query("SELECT MAX(createdAt) FROM Commit c WHERE projectId = ?1")
    public Date findLatestCommitDateForProject(Long projectId);

    @Query("SELECT id FROM Project WHERE id IN " +
            "(SELECT DISTINCT projectId from Commit WHERE sha IN (SELECT sha FROM Commit WHERE projectId = ?1))" +
            "ORDER BY watchers DESC, contributors DESC")
    public List<Long> findProjectDuplicates(Long projectId);

    @Transactional
    public void deleteByProjectId(Long projectId);
}
