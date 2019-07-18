package de.htw.ai.web.askmystudents.service;

import de.htw.ai.web.askmystudents.models.courses.Lesson;
import de.htw.ai.web.askmystudents.models.feedbacks.Feedback;
import de.htw.ai.web.askmystudents.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService implements IService<Feedback, Object, Lesson> {

    @Autowired
    FeedbackRepository repository;

    @Override
    public List<Feedback> getAllFromParent(final Long id) {
        return this.repository.findAllByLesson(id);
    }

    public Feedback insert(final Feedback feedback) {
        return this.repository.save(feedback);
    }

    @Override
    public Feedback getById(final Long id) {
        return this.repository.getOne(id);
    }

    @Override
    public Feedback update(final Feedback feedback) {
        return this.repository.save(feedback);
    }

    @Override
    public List<Object> getObjects(final Long id) {
        return null;
    }

    @Override
    public Feedback addChildObject(final Long id, final Object o) {
        return null;
    }

    @Override
    public Feedback addParentObject(final Long feedbackId, final Lesson lesson) {
        final Feedback feedbackToUpdate = this.repository.getOne(feedbackId);
        feedbackToUpdate.setLesson(lesson);
        return this.repository.save(feedbackToUpdate);
    }

    @Override
    public Feedback deleteChildObject(final Long id, final Long childId) {
        return null;
    }

    @Override
    public void delete(final Long id) {
        this.repository.deleteById(id);
    }

    @Override
    public Feedback duplicate(final Long id, final boolean withParent) {
        return null;
    }
}