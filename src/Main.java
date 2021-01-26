import java.util.ArrayList;
import java.util.concurrent.*;

public class Main {

    private static double T;
    static double t_min_val = 0.01;
    static double t_max_val = 2.000;
    static double t_increase = 0.05;
    public static Double getT() {
        return T;
    }
    public void setT(Double T) { this.T= T; }

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
            resultsVar += Math.pow((((cpArr[j] - cp)/cp) - computedRelError), 2);
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
        ArrayList<Double> storeMagnetizationValues = new ArrayList<>();
        ArrayList<Double> storeCorrelationPairValues = new ArrayList<>();
        ArrayList<Double> computedRelativeErrorCpList = new ArrayList<>();
        ArrayList<Double> computedVarianceCpList = new ArrayList<>();

        Main mainVal = new Main();
        MetropolisAlgorithm compute = new MetropolisAlgorithm();
            try {
                for (T = t_min_val; T <= t_max_val; T += t_increase) {
                    mainVal.setT(T);
                    if(!storeValues.isEmpty()) {
                    storeValues.clear();
                    }
                    for (int i = 0; i < N_t; i++) {
                        Future<MetropolisAlgorithm> future = startM(executor);
                        storeValues.add(future);
                    }

                    if(!storeMAValues.isEmpty()) {
                        storeMAValues.clear();
                    }
                    for (int i = 0; i < N_t; i++) {
                        storeMAValues.add(storeValues.get(i).get());
                    }
                    for (int i = 0; i < storeMAValues.size(); i++) {
                        m[i] = storeMAValues.get(i).magnetization;
                        c[i] = storeMAValues.get(i).correlationperpair;
                    }

                    //try to do for loop and set temp somewhere here.
                    try{
                        storeMagnetizationValues.add(sumOfAllThreads(m));
                        storeCorrelationPairValues.add(sumOfAllThreads(c));
                    }catch(Exception err){
                        err.printStackTrace();
                    }

                    double cp = (Math.exp(compute.getC()/getT()) - Math.exp((-1 * compute.getC())/getT()))/(Math.exp(compute.getC()/getT()) + Math.exp((-1 * compute.getC())/getT()));
                    double computedRelativeErrorCp = Math.abs(relativeError(c, cp));
                    double computeVarianceCp = variance(c, cp, computedRelativeErrorCp);
                    computedRelativeErrorCpList.add(computedRelativeErrorCp);
                    computedVarianceCpList.add(computeVarianceCp);
                }

            } catch (Exception err) {
                err.printStackTrace();
            }
            executor.shutdown();

        System.out.println("Sum of Threads in meu " + storeMagnetizationValues);
        System.out.println("Sum of Threads in ceu " + storeCorrelationPairValues);
        System.out.println("computed Relative Error for cp " + computedRelativeErrorCpList);
        System.out.println("computed variance for cp " + computedVarianceCpList);

    }
}
