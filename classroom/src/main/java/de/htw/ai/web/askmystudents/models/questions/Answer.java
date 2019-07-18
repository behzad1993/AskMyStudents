package de.htw.ai.web.askmystudents.models.questions;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Answer {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "answer_sequence"),
                    @Parameter(name = "initial_value", value = "100"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    private int orderIndex;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 200)
    private String answerText;

    @Column(nullable = false)
    private Boolean isCorrect;
}
