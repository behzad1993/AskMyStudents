package de.htw.ai.web.askmystudents.models.courses;

import de.htw.ai.web.askmystudents.models.questions.Question;
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
public class Quiz {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "quiz_sequence"),
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
    private Lesson lesson;

    @OneToMany
    private List<Question> questions;

    public void addQuestion(final Question question) {
        this.questions.add(question);
    }
}
