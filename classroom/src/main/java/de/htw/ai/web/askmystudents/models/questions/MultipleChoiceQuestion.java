package de.htw.ai.web.askmystudents.models.questions;

import de.htw.ai.web.askmystudents.models.courses.Quiz;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("1")
@Data
@NoArgsConstructor
public class MultipleChoiceQuestion extends Question {

    public MultipleChoiceQuestion(final String text, final List<Answer> answers, final int timer) {
        super();
        if (text.length() == 0
                || answers.size() < 2
                || !checkAnswers(answers)
                || timer < 1) {
            this.allowed = false;
            return;
        }
        this.allowed = true;
        this.text = text;
        this.timer = timer;
        this.answers = answers;

    }

    public MultipleChoiceQuestion(final String text, final int timer, final Quiz quiz, final List<Answer> answers, final boolean allowed, final int orderIndex) {
        this.text = text;
        this.timer = timer;
        this.quiz = quiz;
        this.answers = answers;
        this.allowed = allowed;
        this.orderIndex = orderIndex;
    }

    @Override
    boolean checkAnswers(final List<Answer> answers) {
        return answers.stream().anyMatch(x -> x.getIsCorrect());
    }
}
