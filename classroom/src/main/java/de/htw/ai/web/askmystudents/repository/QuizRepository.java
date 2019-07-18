package de.htw.ai.web.askmystudents.repository;

import de.htw.ai.web.askmystudents.models.courses.Quiz;
import de.htw.ai.web.askmystudents.models.questions.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    public List<Quiz> findAllByLesson(@Param("id") Long id);

    public Quiz findByQuestionsContaining(@Param("question") Question question);
}
