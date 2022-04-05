package bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Depot implements Serializable {
    private Integer id;
    private Float latitude;
    private Float longitude;
    private Float radius;
}