import lombok.*;

import java.util.Map;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Data
public class Student {
    private String _id;
    private String name;
    private String surname;
    private Map<String, Double> grade;
}
