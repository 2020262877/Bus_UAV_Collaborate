package bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trip implements Serializable {

    //trip 起始点
    private Bus start;

    //trip 长度
    private int length;

    //trip 是否载客 {true: 载客, false: 空载}
    public boolean isLoad() {
        return start.getState() == 1 ? true : false;
    }

    public void incLen() {
        length++;
    }
}
