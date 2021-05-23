import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String surname;
    private String position;
    private double salary;
}
