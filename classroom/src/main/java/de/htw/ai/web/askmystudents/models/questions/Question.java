package de.htw.ai.web.askmystudents.models.questions;

import de.htw.ai.web.askmystudents.models.courses.Quiz;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.INTEGER)
@Entity
@NoArgsConstructor
@ToString
public abstract class Question {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "question_sequence"),
                    @Parameter(name = "initial_value", value = "100"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    protected long id;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 500)
    protected String text;

    @NotNull
    @Min(5)
    protected int timer;

    @NotNull
    protected int orderIndex;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    protected Quiz quiz;

    @OneToMany
    protected List<Answer> answers;

    @NotNull
    protected boolean randomOrder;

    @NotNull
    protected boolean allowed;

    abstract boolean checkAnswers(List<Answer> answers);

    public boolean isAllowed() {
        return this.allowed;
    }

    public boolean hasRandomOrder() {
        return this.randomOrder;
    }

    public void addAnswer(final Answer answer) {
        this.answers.add(answer);
    }
}
