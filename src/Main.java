import java.util.ArrayList;
import java.util.concurrent.*;

public class Main {

    private static double B;
    private static double C;
    private static double T;
    private static int n = 100;
    private static int N_f = 10;
    private static int N_m = 50;
    private static int N_t = 1000;
    static double t_min_val = 0.001;
    static double t_max_val = 2.00;
    static double t_increase = 0.05;
    public static Double getT() {
        return T;
    }
    public void setT(Double T) { Main.T = T; }
    public static Double getB() {
        return B;
    }
    public void setB(Double B) {
        Main.B = B;
    }
    public static Double getC() {
        return C;
    }
    public void setC(Double C) {
        Main.C = C;
    }
    public static Integer getN_f() {
        return N_f;
    }
    public void setN_f(Integer N_f) {
        Main.N_f = N_f;
    }
    public static Integer getN_m() {
        return N_m;
    }
    public void setN_m(Integer N_m) {
        Main.N_m = N_m;
    }
    public static Integer getN() {
        return n;
    }
    public void setN(Integer n) {
        Main.n = n;
    }
    public static Integer getN_t() {
        return N_t;
    }
    public void setN_t(Integer N_t) {
        Main.N_t = N_t;
    }

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

    public static void computationChallenge(int N_t, Double B, Double C) {
        double[] m = new double[N_t];
        double[] c = new double[N_t];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ArrayList<Future<MetropolisAlgorithm>> storeValues = new ArrayList<>();
        ArrayList<MetropolisAlgorithm> storeMAValues = new ArrayList<>();
        ArrayList<Double> storeMagnetizationValues = new ArrayList<>();
        ArrayList<Double> storeCorrelationPairValues = new ArrayList<>();
        ArrayList<Double> computedRelativeErrorCpList = new ArrayList<>();
        ArrayList<Double> computedVarianceCpList = new ArrayList<>();
        Main mainVal = new Main();
        try {
            for (T = t_min_val; T <= t_max_val; T += t_increase) {
                mainVal.setT(T);
                mainVal.setB(B);
                mainVal.setC(C);
                if(!storeValues.isEmpty()) { storeValues.clear(); }
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
                } catch(Exception err){
                    err.printStackTrace();
                }
                double cp = (Math.exp(getC()/getT()) - Math.exp((-1 * getC())/getT()))/(Math.exp(getC()/getT()) + Math.exp((-1 * getC())/getT()));
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

        //Challenge 1
        computationChallenge(N_t, 0.51, -0.51);

        //Challenge 2
//        computationChallenge(N_t, 0.25, -0.12);

        //Challenge 3
//        computationChallenge(N_t, 0.30, -0.15);

    }
}
