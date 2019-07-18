package de.htw.ai.web.askmystudents.service;

import de.htw.ai.web.askmystudents.models.courses.Lesson;
import de.htw.ai.web.askmystudents.models.courses.Quiz;
import de.htw.ai.web.askmystudents.models.questions.Question;
import de.htw.ai.web.askmystudents.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class QuizService implements IService<Quiz, Question, Lesson> {

    private final static String NEW = "[new]";

    private final QuizRepository quizRepository;
    private QuestionService questionService;
    private LessonService lessonService;

    @Autowired
    public QuizService(final QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Autowired
    public void setService(final LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Autowired
    public void setService(final QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public List<Quiz> getAllFromParent(final Long id) {
        return this.quizRepository.findAllByLesson(id);
    }

    @Override
    public Quiz insert(final Quiz quiz) {
        return this.quizRepository.save(quiz);
    }

    @Override
    public Quiz getById(final Long id) {
        return this.quizRepository.getOne(id);
    }

    public Quiz getByQuestion(final Question question) {
        return this.quizRepository.findByQuestionsContaining(question);
    }

    @Override
    public Quiz update(final Quiz quiz) {
        return this.quizRepository.save(quiz);
    }

    @Override
    public List<Question> getObjects(final Long id) {
        final Quiz quiz = this.quizRepository.getOne(id);
        final List<Question> questions = quiz.getQuestions();
        questions.sort(Comparator.comparing(Question::getText));
        return questions;
    }

    @Override
    public Quiz addChildObject(final Long quizId, final Question question) {
        final Quiz quizToUpdate = this.quizRepository.getOne(quizId);
        quizToUpdate.addQuestion(question);
        return this.quizRepository.save(quizToUpdate);
    }

    @Override
    public Quiz addParentObject(final Long quizId, final Lesson lesson) {
        final Quiz quizToUpdate = this.quizRepository.getOne(quizId);
        quizToUpdate.setLesson(lesson);
        return this.quizRepository.save(quizToUpdate);
    }

    @Override
    public Quiz deleteChildObject(final Long id, final Long childId) {
        final Quiz quiz = this.quizRepository.getOne(id);
        List<Question> questionList = quiz.getQuestions();
        questionList = reOrderIndex(questionList, childId);
        quiz.setQuestions(questionList);
        return this.quizRepository.save(quiz);
    }

    private List<Question> reOrderIndex(final List<Question> questionList, final Long toDelete) {
        final Question questionToDelete = questionList.stream()
                .filter(question -> toDelete.equals(question.getId()))
                .findAny()
                .orElse(null);
        if (questionToDelete == null) {
            return questionList;
        }

        questionList.removeIf(question -> question.getId() == toDelete);

        final int orderIndex = questionToDelete.getOrderIndex();
        questionList.sort(Comparator.comparing(Question::getOrderIndex));

        for (int i = orderIndex; i < questionList.size(); i++) {
            final Question questionDecreasingOrderIndex = questionList.get(i);
            questionDecreasingOrderIndex.setOrderIndex(i);
        }
        return questionList;
    }

    @Override
    public void delete(final Long id) {
        final Quiz quiz = this.quizRepository.getOne(id);
        final List<Question> questionList = quiz.getQuestions();
        for (final Question question : questionList) {
            quiz.setQuestions(new LinkedList<>());
            this.quizRepository.save(quiz);
            this.questionService.delete(question.getId());
        }
        this.quizRepository.deleteById(id);
    }

    @Override
    public Quiz duplicate(final Long id, final boolean withParent) {
        final Quiz quiz = this.quizRepository.getOne(id);
        final Quiz.QuizBuilder builder = new Quiz().builder();
        final List<Question> questionList = quiz.getQuestions();
        final List<Question> questionListDuplicate = new LinkedList<>();

        for (final Question question : questionList) {
            final Question answerDuplicate = this.questionService.duplicate(question.getId(), false);
            questionListDuplicate.add(answerDuplicate);
        }

        Quiz quizDuplicate = builder.description(quiz.getDescription())
                .name(quiz.getName() + NEW)
                .lesson(quiz.getLesson())
                .questions(questionListDuplicate)
                .build();

        quizDuplicate = this.quizRepository.save(quizDuplicate);
        if (withParent) {
            this.lessonService.addChildObject(quizDuplicate.getLesson().getId(), quizDuplicate);
        }

        return quizDuplicate;
    }
}
