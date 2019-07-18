package de.htw.ai.web.askmystudents.models.courses;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
public class Course {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "course_sequence"),
                    @Parameter(name = "initial_value", value = "100"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    @NotNull
    @NotBlank
    String username;

    @OneToMany
    List<Lesson> lessons;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 70)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 500)
    private String description;

    public void addLesson(final Lesson lesson) {
        this.lessons.add(lesson);
    }
}
