package com.sp222kh.investigitor.repositories;

import com.sp222kh.investigitor.models.Commit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CommitRepository extends CrudRepository<Commit, Long> {

    List<Commit> findById(Long id);

    @Query("SELECT COUNT(*) FROM ProjectCommit WHERE commitId = ?1")
    public int commitOccurrences(Long id);

    @Query(value = "SELECT p.id FROM project p WHERE p.id IN " +
            "(SELECT DISTINCT pc.project_id from project_commit pc WHERE pc.commit_id IN (SELECT c.commit_id FROM project_commit c WHERE c.project_id = ?1)) " +
            "ORDER BY (SELECT COUNT(w.project_id) FROM watcher w WHERE w.project_id = p.id) DESC", nativeQuery = true)
    public List<BigInteger> findProjectDuplicates(Long projectId);


    @Query("SELECT DISTINCT p.commitId FROM ProjectCommit p")
    public Set<Long> findAllCommitIds();

    @Modifying
    @Transactional
    @Query("DELETE FROM ProjectCommit pc WHERE NOT EXISTS (SELECT p.id FROM Project p WHERE p.id = pc.projectId)")
    public void deleteProjectCommitsWithoutProject();

    @Modifying
    @Transactional
    @Query("DELETE FROM Commit c WHERE NOT EXISTS (SELECT pc.commitId FROM ProjectCommit pc WHERE pc.commitId= c.id)")
    public void deleteCommitWithoutProject();

}
