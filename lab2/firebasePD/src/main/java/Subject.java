import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Data
public class Subject {
    private String _id;
    private String name;
    private int ETCS;
}
