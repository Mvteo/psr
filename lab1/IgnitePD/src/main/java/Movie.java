import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String director;
    private String description;
    private double duration;
}
