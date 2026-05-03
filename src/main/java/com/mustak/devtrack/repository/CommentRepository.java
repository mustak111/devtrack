package com.mustak.devtrack.repository;

import com.mustak.devtrack.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByIssueIdOrderByCreatedAtDesc(Long issueId);

    void deleteByIssueId(Long issueId);
}