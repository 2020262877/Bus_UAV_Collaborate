package bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WareHouse implements Serializable {
    private Float latitude;
    private Float longitude;
    private Float radius;
}
