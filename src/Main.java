import java.util.ArrayList;
import java.util.concurrent.*;

public class Main {
    public static double meanOfEachThread(ArrayList<Double> sum)
    {
        double results = 0.0;
        for(int j = 0; j < sum.size(); j++) {
            results += sum.get(j);
        }
        return (1.0/sum.size()) * results;
    }

    public static double sumOfAllThreads(double[] thread_sum)
    {
        double thread_results = 0.0;
        for(int j = 0; j < thread_sum.length; j++) {
            thread_results += thread_sum[j];
        }
        return (1.0/thread_sum.length) * thread_results;
    }

    public static double relativeError(double[] cpArr, double cp)
    {
        double results = 0.0;
        for (int j = 0; j < cpArr.length; j++) {
            results += ((cpArr[j] - cp)/cp);
        }
        return results/cpArr.length;
    }

    public static double variance(double[] cpArr, double cp, double computedRelError)
    {
        double resultsVar = 0.0;
        for (int j = 0; j < cpArr.length; j++) {
            resultsVar += ((((cpArr[j] - cp)/cp) - computedRelError) * (((cpArr[j] - cp)/cp) - computedRelError));
        }
        return resultsVar/cpArr.length;
    }

    private static Future<MetropolisAlgorithm> startM(ExecutorService executor) {
        Future<MetropolisAlgorithm> future = executor.submit(new Callable<MetropolisAlgorithm>() {
            @Override
            public MetropolisAlgorithm call() {
                MetropolisAlgorithm a = new MetropolisAlgorithm();
                a.computationInThreads();
                return a;
            }
        });
        return future;
    }

    public static void main(String[] args)
    {
        int N_t = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        double[] m = new double[N_t];
        double[] c = new double[N_t];
        ArrayList<Future<MetropolisAlgorithm>> storeValues = new ArrayList<>();
        ArrayList<MetropolisAlgorithm> storeMAValues = new ArrayList<>();

        try {
            for (int i = 0; i < N_t; i++) {
                Future<MetropolisAlgorithm> future = startM(executor);
                storeValues.add(future);
            }

            for (int i = 0; i < N_t; i++) {
                storeMAValues.add(storeValues.get(i).get());
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        executor.shutdown();

        for (int i = 0; i < storeValues.size(); i++) {
            m[i] = storeMAValues.get(i).magnetization;
            c[i] = storeMAValues.get(i).correlationperpair;
        }

        for (int i = 0; i < c.length; i++) {
            System.out.println( "i" + " " + i + " " + c[i]);
        }

        double meu = 0.0;
        double ceu = 0.0;
        try{
            meu = sumOfAllThreads(m);
            System.out.println(meu);
            ceu = sumOfAllThreads(c);
            System.out.println(ceu);
        }catch(Exception err){
            err.printStackTrace();
        }

        MetropolisAlgorithm computeC = new MetropolisAlgorithm();
        //compute theoretical values
        //cp = ((e ^ C/T) - e ^ (-C/T))/((e ^ C/T) + e ^ (-C/T))
        double cp = (Math.exp(computeC.getC()/computeC.getT()) - Math.exp((-1 * computeC.getC())/computeC.getT()))
                /(Math.exp(computeC.getC()/computeC.getT()) + Math.exp((-1 * computeC.getC())/computeC.getT()));

        double computedRelativeError = relativeError(c, cp);
        double computeVariance = variance(c, cp, computedRelativeError);

        System.out.println("computed Relative Error " + computedRelativeError);
        System.out.println("computed variance " + computeVariance);

    }
}
