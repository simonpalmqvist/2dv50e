package com.sp222kh.investigitor.repositories;

import com.sp222kh.investigitor.models.Commit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public interface CommitRepository extends CrudRepository<Commit, Long> {

    List<Commit> findByProjectId(Long projectId);

    List<Commit> findById(Long id);

    @Query("SELECT COUNT(DISTINCT authorId) FROM Commit c WHERE projectId= ?1")
    public int findContributorsForProject(Long projectId);

    @Query("SELECT MAX(createdAt) FROM Commit c WHERE projectId = ?1")
    public Date findLatestCommitDateForProject(Long projectId);

    @Query(value = "SELECT p.id FROM project p WHERE p.id IN " +
            "(SELECT DISTINCT pc.project_id from project_commit pc WHERE pc.commit_id IN (SELECT c.commit_id FROM project_commit c WHERE c.project_id = ?1)) " +
            "ORDER BY (SELECT COUNT(w.project_id) FROM watcher w WHERE w.project_id = p.id) DESC", nativeQuery = true)
    public List<BigInteger> findProjectDuplicates(Long projectId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProjectCommit pc WHERE NOT EXISTS (SELECT p.id FROM Project p WHERE p.id = pc.projectId)")
    public void deleteProjectCommitsWithoutProject();

    @Modifying
    @Transactional
    @Query("DELETE FROM Commit c WHERE NOT EXISTS (SELECT pc.commitId FROM ProjectCommit pc WHERE pc.commitId= c.id)")
    public void deleteCommitWithoutProject();

    @Transactional
    public void deleteByProjectId(Long projectId);
}
