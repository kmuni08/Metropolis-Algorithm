import java.util.concurrent.ThreadLocalRandom;

//choosing i using a uniform pseudorandom number distribution. 1 <= i < = n
public class PseudoRandomGenerator {
    public static int getPseudoRandomNumber(int begin, int end) {
        return ThreadLocalRandom.current().nextInt(begin, end);
    }
}
