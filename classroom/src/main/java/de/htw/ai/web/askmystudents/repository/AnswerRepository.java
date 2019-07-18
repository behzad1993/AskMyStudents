package de.htw.ai.web.askmystudents.repository;

import de.htw.ai.web.askmystudents.models.questions.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    public List<Answer> findAllByQuestion(@Param("id") Long id);

    public List<Answer> findByQuestion(@Param("course") Long id);


}
