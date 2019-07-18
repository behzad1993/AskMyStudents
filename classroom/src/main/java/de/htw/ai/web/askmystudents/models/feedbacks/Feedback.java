package de.htw.ai.web.askmystudents.models.feedbacks;

import de.htw.ai.web.askmystudents.models.courses.Lesson;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@ToString
public class Feedback {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "feedback_sequence"),
                    @Parameter(name = "initial_value", value = "100"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    @NotNull
    private LocalDate created;

    @NotNull
    private LocalDate modified;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 20)
    private String studentName;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 1000)
    private String text;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Lesson lesson;

    @PrePersist
    void onCreate() {
        this.setCreated(LocalDate.now());
        this.setModified(LocalDate.now());
    }

    @PreUpdate
    void onUpdate() {
        this.setModified(LocalDate.now());
    }

}
