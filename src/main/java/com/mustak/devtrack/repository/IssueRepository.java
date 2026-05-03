package com.mustak.devtrack.repository;

import com.mustak.devtrack.entity.Issue;
import com.mustak.devtrack.enums.Priority;
import com.mustak.devtrack.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    Page<Issue> findByStatus(Status status, Pageable pageable);

    Page<Issue> findByPriority(Priority priority, Pageable pageable);

    Page<Issue> findByAssigneeId(Long assigneeId, Pageable pageable);

    @Query("SELECT i FROM Issue i WHERE " +
            "LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Issue> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    List<Issue> findByCreatorId(Long creatorId);
}