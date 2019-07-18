package de.htw.ai.web.askmystudents.models.questions;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

class QuestionTest {

    static Question question;
    static Answer answerFalse;
    static Answer answerTrue;
    final static int TIMER = 10;

    @BeforeAll
    static void setup() {
        question = null;
        answerFalse = new Answer();
        answerFalse.setIsCorrect(false);
        answerFalse.setAnswerText("test");
        answerTrue = new Answer();
        answerTrue.setIsCorrect(true);
        answerFalse.setAnswerText("test");
    }

    @Test
    void test_SingleChoiceQuestion_checkAnswers_OneCorrectAnswers() {
        final List<Answer> answers = new LinkedList<>();
        answers.add(answerFalse);
        answers.add(answerTrue);
        this.question = new SingleChoiceQuestion("question", answers, TIMER);
        Assert.assertTrue(question.isAllowed());
    }

    @Test
    void test_SingleChoiceQuestion_checkAnswers_noCorrectAnswer() {
        final List<Answer> answers = new LinkedList<>();
        answers.add(answerFalse);
        answers.add(answerFalse);
        answers.add(answerFalse);
        this.question = new SingleChoiceQuestion("question", answers, TIMER);
        Assert.assertFalse(question.isAllowed());
    }

    @Test
    void test_MultipleChoiceQuestion_checkAnswers_OneCorrectAnswers() {
        final List<Answer> answers = new LinkedList<>();
        answers.add(answerFalse);
        answers.add(answerTrue);
        this.question = new MultipleChoiceQuestion("question", answers, TIMER);
        Assert.assertTrue(question.isAllowed());
    }

    @Test
    void test_MultipleChoiceQuestion_checkAnswers_MoreThanOneCorrectAnswers() {
        final List<Answer> answers = new LinkedList<>();
        answers.add(answerTrue);
        answers.add(answerFalse);
        answers.add(answerTrue);
        this.question = new MultipleChoiceQuestion("question", answers, TIMER);
        Assert.assertTrue(question.isAllowed());
    }

    @Test
    void test_MultipleChoiceQuestion_checkAnswers_noCorrectAnswer() {
        final List<Answer> answers = new LinkedList<>();
        answers.add(answerFalse);
        answers.add(answerFalse);
        answers.add(answerFalse);
        this.question = new MultipleChoiceQuestion("question", answers, TIMER);
        Assert.assertFalse(question.isAllowed());
    }
}