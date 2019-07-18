package de.htw.ai.web.askmystudents.repository;

import de.htw.ai.web.askmystudents.models.users.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByNameAndPin(@Param("name") String name, @Param("pin") int pin);

    List<Student> findByPin(@Param("pin") int pin);
}
