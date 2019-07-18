package de.htw.ai.web.askmystudents.service;

import de.htw.ai.web.askmystudents.models.users.Student;
import de.htw.ai.web.askmystudents.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService implements IService<Student, Object, Object> {

    private final StudentRepository studentRepository;
    private final Encoder encoder;


    @Autowired
    public StudentService(final StudentRepository studentRepository, final Encoder encoder) {
        this.studentRepository = studentRepository;
        this.encoder = encoder;
    }

    public Student getByNameAndPin(final String username, final int pin) {
        return this.studentRepository.findByNameAndPin(username, pin);
    }

    public void deleteFromLesson(final int pin) {
        final List<Student> students = this.studentRepository.findByPin(pin);
        this.studentRepository.deleteAll(students);
    }

    @Override
    public List<Student> getAllFromParent(final Long id) {
        return this.studentRepository.findAll();
    }

    @Override
    public Student insert(final Student student) {
        final String nameAndBirthdayToEncode = student.getName() + student.getBirthday();
        String encodedStudent = this.encoder.encode(nameAndBirthdayToEncode);
        encodedStudent = encodedStudent.replace('/', '!');
        student.setToken(encodedStudent);
        return this.studentRepository.save(student);
    }

    @Override
    public Student getById(final Long id) {
        return this.studentRepository.getOne(id);
    }

    @Override
    public Student update(final Student student) {
        return this.studentRepository.save(student);
    }

    @Override
    public List<Object> getObjects(final Long id) {
        return null;
    }

    @Override
    public Student addChildObject(final Long id, final Object o) {
        return null;
    }

    @Override
    public Student addParentObject(final Long id, final Object o) {
        return null;
    }

    @Override
    public Student deleteChildObject(final Long id, final Long childId) {
        return null;
    }

    @Override
    public void delete(final Long id) {

    }

    @Override
    public Student duplicate(final Long id, final boolean withParents) {
        return null;
    }
}
