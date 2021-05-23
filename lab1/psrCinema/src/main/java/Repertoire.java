import lombok.*;

        import java.io.Serializable;
        import java.time.LocalDateTime;
        import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Repertoire implements Serializable {

    private String hall;
    private String movieTitle;
    private Date dataTime;
    private double price;
}
