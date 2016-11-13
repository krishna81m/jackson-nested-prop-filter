package rk.prod.jackson.datatype;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * Created by igreenfi on 11/13/2016.
 */
@JsonFilter("nestedPropertyFilter")
public class Pojo {
    private String a;
    private Integer b;
    private Pojo2 c;

    public Pojo(String a, Integer b, Pojo2 c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public Integer getB() {
        return b;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    public Pojo2 getC() {
        return c;
    }

    public void setC(Pojo2 c) {
        this.c = c;
    }
}
