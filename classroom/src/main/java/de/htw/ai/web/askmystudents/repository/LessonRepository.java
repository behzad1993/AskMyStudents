package de.htw.ai.web.askmystudents.repository;

import de.htw.ai.web.askmystudents.models.courses.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findLessonsByCourseId(@Param("id") Long id);

    Lesson findLessonById(@Param("id") Long id);

    Lesson findByPin(@Param("pin") int pin);
}
