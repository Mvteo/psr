import lombok.*;

import java.util.ArrayList;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Data
public class Lecturer {

    private String _id;
    private String name;
    private String surname;
    private Double salary;
    private ArrayList<String> subjects;

}
