package de.htw.ai.web.askmystudents.service;

import de.htw.ai.web.askmystudents.models.courses.Course;
import de.htw.ai.web.askmystudents.models.courses.Lesson;
import de.htw.ai.web.askmystudents.models.courses.Quiz;
import de.htw.ai.web.askmystudents.models.feedbacks.Feedback;
import de.htw.ai.web.askmystudents.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LessonService implements IService<Lesson, Quiz, Course> {

    private final static String NEW = "[new]";
    private static final int MIN = 1000;
    private static final int MAX = 9999;

    private final LessonRepository repository;
    private CourseService courseService;
    private FeedbackService feedbackService;
    private QuizService quizService;


    @Autowired
    public LessonService(final LessonRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setService(final QuizService quizService) {
        this.quizService = quizService;
    }

    @Autowired
    public void setService(final FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Autowired
    public void setService(final CourseService courseService) {
        this.courseService = courseService;
    }


    @Override
    public List<Lesson> getAllFromParent(final Long id) {
        return this.repository.findLessonsByCourseId(id);
    }

    @Override
    public Lesson insert(final Lesson lesson) {
        final Lesson lessonToCreate = this.repository.findLessonById(lesson.getId());
        int pin = 0;
        if (lessonToCreate == null) {
            Lesson lessonHasPin = new Lesson();
            while (lessonHasPin != null) {
                pin = ThreadLocalRandom.current().nextInt(MIN, MAX + 1);
                lessonHasPin = this.repository.findByPin(pin);
            }
            lesson.setPin(pin);
        }
        lesson.setIsActive(false);
        return this.repository.save(lesson);
    }


    public Lesson setLessonIsActive(final Long lessonId, final boolean isActive) {
        final Lesson lesson = this.repository.getOne(lessonId);
        lesson.setIsActive(isActive);
        this.repository.save(lesson);
        return lesson;
    }

    @Override
    public Lesson getById(final Long id) {
        return this.repository.getOne(id);
    }

    public Lesson getByPin(final int pin) {
        return this.repository.findByPin(pin);
    }

    @Override
    public Lesson update(final Lesson lesson) {
        return this.repository.save(lesson);
    }

    @Override
    public List<Quiz> getObjects(final Long lessonId) {
        final Lesson lesson = this.repository.getOne(lessonId);
        final List<Quiz> quizzes = lesson.getQuizzes();
        quizzes.sort(Comparator.comparing(Quiz::getName));
        return quizzes;
    }

    @Override
    public Lesson addChildObject(final Long lessonId, final Quiz quiz) {
        final Lesson lessonToUpdate = this.repository.getOne(lessonId);
        lessonToUpdate.addQuiz(quiz);
        return this.repository.save(lessonToUpdate);
    }

    @Override
    public Lesson addParentObject(final Long lessonId, final Course course) {
        final Lesson lessonToUpdate = this.repository.getOne(lessonId);
        lessonToUpdate.setCourse(course);
        return this.repository.save(lessonToUpdate);
    }

    public Lesson addChildObject(final Long lessonId, final Feedback feedback) {
        final Lesson lessonToUpdate = this.repository.getOne(lessonId);
        lessonToUpdate.addFeedback(feedback);
        return this.repository.save(lessonToUpdate);
    }

    @Override
    public Lesson deleteChildObject(final Long lessonId, final Long quizId) {
        final Lesson lesson = this.repository.getOne(lessonId);
        final List<Quiz> lessons = lesson.getQuizzes();
        lessons.removeIf(quiz -> quiz.getId() == quizId);
        lesson.setQuizzes(lessons);
        return this.repository.save(lesson);
    }


    public Lesson deleteFeedback(final Long lessonId, final Long feedbackId) {
        final Lesson lesson = this.repository.getOne(lessonId);
        final List<Feedback> feedbacks = lesson.getFeedbacks();
        feedbacks.removeIf(feedback -> feedback.getId() == feedbackId);
        lesson.setFeedbacks(feedbacks);
        return this.repository.save(lesson);
    }

    @Override
    public void delete(final Long id) {
        final Lesson lesson = this.repository.getOne(id);
        final List<Feedback> feedbackList = lesson.getFeedbacks();
        for (final Feedback feedback : feedbackList) {
            lesson.setFeedbacks(new LinkedList<>());
            this.repository.save(lesson);
            this.feedbackService.delete(feedback.getId());
        }
        final List<Quiz> quizList = lesson.getQuizzes();
        for (final Quiz quiz : quizList) {
            lesson.setQuizzes(new LinkedList<>());
            this.repository.save(lesson);
            this.quizService.delete(quiz.getId());
        }
        this.repository.deleteById(id);
    }


    @Override
    public Lesson duplicate(final Long id, final boolean withParent) {
        final Lesson lesson = this.repository.getOne(id);
        final Lesson.LessonBuilder builder = new Lesson().builder();
        final List<Quiz> quizList = lesson.getQuizzes();
        final List<Quiz> quizListDuplicate = new LinkedList<>();

        for (final Quiz quiz : quizList) {
            final Quiz quizDuplicate = this.quizService.duplicate(quiz.getId(), false);
            quizListDuplicate.add(quizDuplicate);
        }

        Lesson lessonDuplicate = builder.description(lesson.getDescription())
                .name(lesson.getName() + NEW)
                .course(lesson.getCourse())
                .quizzes(quizListDuplicate)
                .isActive(false)
                .build();

        lessonDuplicate = this.insert(lessonDuplicate);
        if (withParent) {
            this.courseService.addChildObject(lessonDuplicate.getCourse().getId(), lessonDuplicate);
        }

        return lessonDuplicate;
    }
}
