package de.htw.ai.web.askmystudents.repository;

import de.htw.ai.web.askmystudents.models.questions.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    public List<Question> findAllByQuiz(@Param("id") Long id);
}
