package bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bus implements Serializable {
    private Integer Id;
    private Float latitude;
    private Float longitude;
    private Integer state;
    private String time;
}
