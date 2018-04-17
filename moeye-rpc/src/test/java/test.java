import org.junit.Test;

public class test {

    @Test
    public void t(){
        int v = 666;
        System.out.println(Integer.toBinaryString(v));
        v  &= 24  ;

        System.out.println(Integer.toBinaryString(v));


        System.out.println(Integer.toBinaryString(v << 3));

    }
}
