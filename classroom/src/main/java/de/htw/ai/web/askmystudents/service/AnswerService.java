package de.htw.ai.web.askmystudents.service;

import de.htw.ai.web.askmystudents.models.questions.Answer;
import de.htw.ai.web.askmystudents.models.questions.Question;
import de.htw.ai.web.askmystudents.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AnswerService implements IService<Answer, Object, Question> {

    private final AnswerRepository repository;
    private QuestionService questionService;


    @Autowired
    public AnswerService(final AnswerRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setService(final QuestionService questionService) {
        this.questionService = questionService;
    }


    @Override

    public List<Answer> getAllFromParent(final Long id) {
        return this.repository.findAll();
    }

    @Override
    public Answer insert(final Answer answer) {
        return this.repository.save(answer);
    }

    public List<Answer> insertAll(final List<Answer> answerList) {
        return this.repository.saveAll(answerList);
    }

    @Override
    public Answer getById(final Long id) {
        return this.repository.getOne(id);
    }

    @Override
    public Answer update(final Answer answer) {
        return this.repository.save(answer);
    }

    @Override
    public List<Object> getObjects(final Long id) {
        return null;
    }

    @Override
    public Answer addChildObject(final Long id, final Object o) {
        return null;
    }

    @Override
    public Answer addParentObject(final Long answerId, final Question question) {
        final Answer answerToUpdate = this.repository.getOne(answerId);
        answerToUpdate.setQuestion(question);
        return this.repository.save(answerToUpdate);
    }

    @Override
    public Answer deleteChildObject(final Long id, final Long childId) {
        return null;
    }

    @Override
    public void delete(final Long id) {
        this.repository.deleteById(id);
    }

    @Override
    public Answer duplicate(final Long id, final boolean withParent) {
        final Answer answer = this.repository.getOne(id);
        final Answer.AnswerBuilder builder = new Answer().builder();
        Answer answerDuplicate = builder.isCorrect(answer.getIsCorrect())
                .question(answer.getQuestion())
                .answerText(answer.getAnswerText())
                .build();

        answerDuplicate = this.repository.save(answerDuplicate);
        if (withParent) {
            this.questionService.addChildObject(answerDuplicate.getQuestion().getId(), answerDuplicate);
        }

        return answerDuplicate;
    }

    public void increaseIndex(final Long answerid, final Long questionId) {
        final List<Answer> answerList = this.questionService.getObjects(questionId);
        final Answer answer = this.repository.getOne(answerid);
        final int orderIndex = answer.getOrderIndex();

        answerList.sort(Comparator.comparing(Answer::getOrderIndex));
        final Answer increasingAnswer = answerList.get(orderIndex);
        final Answer decreasingAnswer = answerList.get(orderIndex + 1);
        Collections.swap(answerList, orderIndex, orderIndex + 1);
        increasingAnswer.setOrderIndex(orderIndex + 1);
        decreasingAnswer.setOrderIndex(orderIndex);

        this.repository.save(increasingAnswer);
        this.repository.save(decreasingAnswer);
    }

    public void decreaseIndex(final Long answerId, final Long questionId) {
        final List<Answer> answerList = this.questionService.getObjects(questionId);
        final Answer answer = this.repository.getOne(answerId);
        final int orderIndex = answer.getOrderIndex();

        answerList.sort(Comparator.comparing(Answer::getOrderIndex));
        final Answer decreasingAnswer = answerList.get(orderIndex);
        final Answer increasingAnswer = answerList.get(orderIndex - 1);
        Collections.swap(answerList, orderIndex, orderIndex - 1);
        decreasingAnswer.setOrderIndex(orderIndex - 1);
        increasingAnswer.setOrderIndex(orderIndex);

        this.repository.save(increasingAnswer);
        this.repository.save(decreasingAnswer);
    }
}
