package de.htw.ai.web.askmystudents.models.questions;

import de.htw.ai.web.askmystudents.models.courses.Quiz;

import java.util.List;

public class QuestionBuilder {

    private String text;
    private int timer;
    private Quiz quiz;
    private List<Answer> answers;
    private boolean allowed;
    private int orderIndex;

    public QuestionBuilder() {

    }

    public QuestionBuilder text(final String text) {
        this.text = text;
        return this;
    }

    public QuestionBuilder timer(final int timer) {
        this.timer = timer;
        return this;
    }

    public QuestionBuilder quiz(final Quiz quiz) {
        this.quiz = quiz;
        return this;
    }

    public QuestionBuilder answers(final List<Answer> answers) {
        this.answers = answers;
        return this;
    }

    public QuestionBuilder allowed(final boolean allowed) {
        this.allowed = allowed;
        return this;
    }

    public QuestionBuilder orderIndex(final int orderIndex) {
        this.orderIndex = orderIndex;
        return this;
    }

    public MultipleChoiceQuestion buildSCQ() {
        return new MultipleChoiceQuestion(this.text, this.timer, this.quiz, this.answers, this.allowed, this.orderIndex);
    }

    public SingleChoiceQuestion buildMCQ() {
        return new SingleChoiceQuestion(this.text, this.timer, this.quiz, this.answers, this.allowed, this.orderIndex);
    }

}
