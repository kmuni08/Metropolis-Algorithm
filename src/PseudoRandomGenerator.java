import java.util.concurrent.ThreadLocalRandom;

//choosing i using a uniform pseudorandom number distribution. 1 <= i < = n
public class PseudoRandomGenerator {
    public static int getPseudoRandomNumberInteger(int begin, int end) {
        return ThreadLocalRandom.current().nextInt(begin, end);
    }

    public static double getPseudoRandomNumberDouble(double begin, double end) {
        return ThreadLocalRandom.current().nextDouble(begin, end);
    }
}
