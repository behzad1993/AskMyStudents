package de.htw.ai.web.askmystudents.service;

import de.htw.ai.web.askmystudents.models.courses.Course;
import de.htw.ai.web.askmystudents.models.courses.Lesson;
import de.htw.ai.web.askmystudents.models.users.Teacher;
import de.htw.ai.web.askmystudents.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class CourseService implements IService<Course, Lesson, Teacher> {

    private final static String NEW = "[new]";

    private final CourseRepository repository;
    private LessonService lessonService;

    @Autowired
    public CourseService(final CourseRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setService(final LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Override
    public List<Course> getAllFromParent(final Long id) {
        return this.repository.findAll();
    }

    public List<Course> getAllFromUser(final String username) {
        return this.repository.findByUsername(username);
    }

    @Override
    public Course insert(final Course course) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String user = authentication.getName();
        course.setUsername(user);
        return this.repository.save(course);
    }

    public Course update(final Course course) {
        return this.repository.save(course);
    }

    public Course getById(final Long id) {
        return this.repository.getOne(id);
    }

    public List<Lesson> getObjects(final Long courseId) {
        final Course course = this.repository.getOne(courseId);
        final List<Lesson> lessons = course.getLessons();
        lessons.sort(Comparator.comparing(Lesson::getName));
        return lessons;
    }

    public Course addChildObject(final Long courseId, final Lesson lesson) {
        final Course courseToUpdate = this.repository.getOne(courseId);
        courseToUpdate.addLesson(lesson);
        return this.repository.save(courseToUpdate);
    }

    @Override
    public Course addParentObject(final Long id, final Teacher teacher) {
        return null;
    }


    @Override
    public Course deleteChildObject(final Long id, final Long childId) {
        final Course course = this.repository.getOne(id);
        final List<Lesson> lessons = course.getLessons();
        lessons.removeIf(lesson -> lesson.getId() == childId);
        course.setLessons(lessons);
        return this.repository.save(course);
    }

    @Override
    public void delete(final Long id) {
        final Course course = this.repository.getOne(id);
        final List<Lesson> lessonList = course.getLessons();
        for (final Lesson lesson : lessonList) {
            course.setLessons(new LinkedList<>());
            this.repository.save(course);
            this.lessonService.delete(lesson.getId());
        }
        this.repository.deleteById(id);
    }

    @Override
    public Course duplicate(final Long id, final boolean withParent) {
        final Course course = this.repository.getOne(id);
        final Course.CourseBuilder builder = new Course().builder();
        final List<Lesson> lessonList = course.getLessons();
        final List<Lesson> lessonListDuplicate = new LinkedList<>();

        for (final Lesson quiz : lessonList) {
            final Lesson quizDuplicate = this.lessonService.duplicate(quiz.getId(), false);
            lessonListDuplicate.add(quizDuplicate);
        }

        final Course courseDuplicate = builder.description(course.getDescription())
                .name(course.getName() + NEW)
                .username(course.getUsername())
                .lessons(lessonListDuplicate)
                .build();

        return this.repository.save(courseDuplicate);
    }
}
