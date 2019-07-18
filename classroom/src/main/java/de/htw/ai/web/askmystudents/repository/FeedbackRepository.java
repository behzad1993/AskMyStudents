package de.htw.ai.web.askmystudents.repository;

import de.htw.ai.web.askmystudents.models.feedbacks.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    public List<Feedback> findAllByLesson(@Param("id") Long id);
}
