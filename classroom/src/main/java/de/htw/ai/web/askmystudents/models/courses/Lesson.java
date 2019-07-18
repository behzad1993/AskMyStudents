package de.htw.ai.web.askmystudents.models.courses;

import de.htw.ai.web.askmystudents.models.feedbacks.Feedback;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Lesson {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "lesson_generator"),
                    @Parameter(name = "initial_value", value = "100"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 70)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 500)
    private String description;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    @Column(nullable = false, unique = true)
    private int pin;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany
    private List<Quiz> quizzes;

    @OneToMany
    private List<Feedback> feedbacks;


    public void addQuiz(final Quiz quiz) {
        this.quizzes.add(quiz);
    }

    public void addFeedback(final Feedback feedback) {
        this.feedbacks.add(feedback);
    }
}
