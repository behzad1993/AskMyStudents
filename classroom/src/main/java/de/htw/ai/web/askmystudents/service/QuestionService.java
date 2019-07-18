package de.htw.ai.web.askmystudents.service;

import de.htw.ai.web.askmystudents.models.courses.Quiz;
import de.htw.ai.web.askmystudents.models.questions.Answer;
import de.htw.ai.web.askmystudents.models.questions.Question;
import de.htw.ai.web.askmystudents.models.questions.QuestionBuilder;
import de.htw.ai.web.askmystudents.models.questions.SingleChoiceQuestion;
import de.htw.ai.web.askmystudents.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class QuestionService implements IService<Question, Answer, Quiz> {


    private final static String NEW = "[new]";
    private final QuestionRepository questionRepository;
    private AnswerService answerService;
    private QuizService quizService;

    @Autowired
    public QuestionService(final QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Autowired
    public void setService(final AnswerService answerService) {
        this.answerService = answerService;
    }

    @Autowired
    public void setService(final QuizService quizService) {
        this.quizService = quizService;
    }

    @Override
    public List<Question> getAllFromParent(final Long id) {
        return this.questionRepository.findAllByQuiz(id);
    }

    @Override
    public Question insert(final Question question) {
        final Quiz quiz = this.quizService.getById(question.getQuiz().getId());
        final List<Question> questionList = quiz.getQuestions();
        final int newIndex = questionList.size();
        question.setOrderIndex(newIndex);
        return this.questionRepository.save(question);
    }

    @Override
    public Question getById(final Long id) {
        return this.questionRepository.getOne(id);
    }

    @Override
    public Question update(final Question question) {
        return this.questionRepository.save(question);
    }


    @Override
    public List<Answer> getObjects(final Long id) {
        final Question question = this.questionRepository.getOne(id);
        final List<Answer> answers = question.getAnswers();
        answers.sort(Comparator.comparing(Answer::getAnswerText));
        return answers;
    }

    @Override
    public Question addChildObject(final Long questionId, final Answer answer) {
        final Question questionToUpdate = this.questionRepository.getOne(questionId);
        questionToUpdate.addAnswer(answer);
        return this.questionRepository.save(questionToUpdate);
    }

    @Override
    public Question addParentObject(final Long questionId, final Quiz quiz) {
        final Question lessonToUpdate = this.questionRepository.getOne(questionId);
        lessonToUpdate.setQuiz(quiz);
        return this.questionRepository.save(lessonToUpdate);
    }

    @Override
    public Question deleteChildObject(final Long id, final Long childId) {
        final Question question = this.questionRepository.getOne(id);
        final List<Answer> answers = question.getAnswers();
        answers.removeIf(answer -> answer.getId() == childId);
        question.setAnswers(answers);
        return this.questionRepository.save(question);
    }

    @Override
    public void delete(final Long id) {
        final Question question = this.questionRepository.getOne(id);
        final List<Answer> answerList = question.getAnswers();
        for (final Answer answer : answerList) {
            question.setAnswers(new LinkedList<>());
            this.questionRepository.save(question);
            this.answerService.delete(answer.getId());
        }
        this.questionRepository.deleteById(id);
    }

    @Override
    public Question duplicate(final Long id, final boolean withParent) {
        final Question question = this.questionRepository.getOne(id);
        final QuestionBuilder builder = new QuestionBuilder();
        final List<Answer> answerList = question.getAnswers();
        final List<Answer> answerListDuplicate = new LinkedList<>();

        for (final Answer answer : answerList) {
            final Answer answerDuplicate = this.answerService.duplicate(answer.getId(), false);
            answerListDuplicate.add(answerDuplicate);
        }

        if (!withParent) {
            builder.allowed(question.isAllowed())
                    .quiz(question.getQuiz())
                    .text(question.getText() + NEW)
                    .timer(question.getTimer())
                    .answers(answerListDuplicate)
                    .orderIndex(question.getOrderIndex());
        } else {
            final Quiz quiz = this.quizService.getByQuestion(question);
            final int size = quiz.getQuestions().size();

            builder.allowed(question.isAllowed())
                    .quiz(question.getQuiz())
                    .text(question.getText() + NEW)
                    .timer(question.getTimer())
                    .answers(answerListDuplicate)
                    .orderIndex(size);
        }

        Question questionDuplicate;
        if (question instanceof SingleChoiceQuestion) {
            questionDuplicate = builder.buildSCQ();
        } else {
            questionDuplicate = builder.buildMCQ();
        }

        questionDuplicate = this.questionRepository.save(questionDuplicate);
        if (withParent) {
            this.quizService.addChildObject(questionDuplicate.getQuiz().getId(), questionDuplicate);
        }

        return questionDuplicate;
    }

    public void increaseIndex(final Long questionId, final Long quizId) {
        final List<Question> questionList = this.quizService.getObjects(quizId);
        final Question question = this.questionRepository.getOne(questionId);
        final int orderIndex = question.getOrderIndex();

        questionList.sort(Comparator.comparing(Question::getOrderIndex));
        final Question increasingQuestion = questionList.get(orderIndex);
        final Question decreasingQuestion = questionList.get(orderIndex + 1);
        Collections.swap(questionList, orderIndex, orderIndex + 1);
        increasingQuestion.setOrderIndex(orderIndex + 1);
        decreasingQuestion.setOrderIndex(orderIndex);

        this.questionRepository.save(increasingQuestion);
        this.questionRepository.save(decreasingQuestion);
    }

    public void decreaseIndex(final Long questionId, final Long quizId) {
        final List<Question> questionList = this.quizService.getObjects(quizId);
        final Question question = this.questionRepository.getOne(questionId);
        final int orderIndex = question.getOrderIndex();

        questionList.sort(Comparator.comparing(Question::getOrderIndex));
        final Question decreasingQuestion = questionList.get(orderIndex);
        final Question increasingQuestion = questionList.get(orderIndex - 1);
        Collections.swap(questionList, orderIndex, orderIndex - 1);
        decreasingQuestion.setOrderIndex(orderIndex - 1);
        increasingQuestion.setOrderIndex(orderIndex);

        this.questionRepository.save(increasingQuestion);
        this.questionRepository.save(decreasingQuestion);
    }

    public void shuffle(final Long questionId) {
        final Question question = this.questionRepository.getOne(questionId);
        final List<Answer> answerList = question.getAnswers();
        Collections.shuffle(answerList);
        for (int i = 0; i < answerList.size(); i++) {
            final Answer reOrderedAnswer = answerList.get(i);
            reOrderedAnswer.setOrderIndex(i);
        }
        this.answerService.insertAll(answerList);
    }
}
