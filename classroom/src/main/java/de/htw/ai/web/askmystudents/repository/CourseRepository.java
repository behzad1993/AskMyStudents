package de.htw.ai.web.askmystudents.repository;

import de.htw.ai.web.askmystudents.models.courses.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    public List<Course> findByUsername(@Param("email") String email);
}
