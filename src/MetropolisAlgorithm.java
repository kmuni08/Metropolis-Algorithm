//compute the initial configuration.

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MetropolisAlgorithm  {

    private int n = 10;
    private int B = 1;
    private int C = 1;
    private int N_f = 4;
    private double T = 1.9;
    private int randomInt;

    CircularArrayList<Integer> sigma0 = new CircularArrayList<>();
    CircularArrayList<Integer> sigma1 = new CircularArrayList<>();
    CircularArrayList<Integer> currentConfiguration;

    public void setInitialSpinConfiguraton_Sigma0 () {
        for (int i = 0; i < n; i++) {
            if ((B>= 0 && C>=0))
            {
                sigma0.add(1);
            }
            else {
                //alternate the signs
                if (i%2 == 0)
                {
                    sigma0.add(1);
                } else {
                    sigma0.add(-1);
                }
            }
        }
        currentConfiguration = sigma0;
//        System.out.println("sigma0: " + sigma0);
        System.out.println("Current Configuration: " + currentConfiguration);
    }

    public int generateRand(int first, int second) {
        return PseudoRandomGenerator.getPseudoRandomNumberInteger(first, second);
    }

    public double generateRandom(double first, double second) {
        return PseudoRandomGenerator.getPseudoRandomNumberDouble(first, second);
    }

    public void createSigma1 () {
        //do the pseudorandom generator
        System.out.println("Current config in sigma 1 creation: " + currentConfiguration);
        if (sigma1.isEmpty()) {
            sigma1.addAll(sigma0);
        }
        sigma1 = changeSigma1(sigma1);
        System.out.println("sigma1: " + sigma1);
//        return sigma1;
    }

    public CircularArrayList<Integer> changeSigma1 (CircularArrayList<Integer> sigma) {
        randomInt = generateRand(1, n);
        System.out.println("Random int in create sigma1: " + randomInt);
        sigma.set(randomInt, (-1)*(sigma1.get(randomInt)));
        System.out.println("sigma1 new : " + sigma);
        return sigma;
    }


    double energyCompute(CircularArrayList<Integer> sigma, int B, int C)
    {
        double sum_of_B = 0;
        double sum_of_C = 0;

        sum_of_B = B * sigma.get(randomInt - 1) + B * sigma.get(randomInt) + B * sigma.get(randomInt + 1);
        sum_of_C = (C * sigma.get(randomInt - 1) * sigma.get(randomInt)) + (C * sigma.get(randomInt) * sigma.get(randomInt + 1)) + (C * sigma.get(randomInt + 1)*sigma.get(randomInt + 2));

        return -1*(sum_of_B + sum_of_C);
    }

    public void replaceConfiguration() {
        System.out.println("Random Integer: " + randomInt);
        System.out.println("Sigma1 in replace config " + sigma1);
        System.out.println("Sigma0 in replace config " + sigma0);
        double deltaE = energyCompute(sigma1, B, C) - energyCompute(sigma0, B, C);
        System.out.println("deltaE " +  deltaE);
        double p = 0.0;
        if(deltaE < 0) {
            currentConfiguration = sigma1;
        } else if (deltaE> 0) {
            //pick a p value.
            p = Math.exp((-1*deltaE) / T);
        }
        double r = generateRandom(0, 1);
        System.out.println("p value: " + p);
        System.out.println("Random double " + r);
        if (r < p) {
            //set current configuration to be new configuration.
            currentConfiguration = sigma1;
        }

        System.out.println("New Current Configuration " + currentConfiguration);
    }

     public void updateSpinGetCurrentConfig()
    {
        CircularArrayList<Integer> temp = new CircularArrayList<>();
        temp.addAll(currentConfiguration);
        for(int i = 0; i < (N_f * n); i++) {
            //call sigma1 and energyCompute.
            System.out.println("i value in updateSpinGetCurrentConfig " + i);
            sigma1 = changeSigma1(temp);
            System.out.println("new sigma1 : " + sigma1);
            replaceConfiguration();
        }
        //return currentConfiguration
    }

    public double computeMagnetizationPerSpin () {
        CircularArrayList<Integer> sigma_star = currentConfiguration;
        //2.3.1
        // 1/n * summation from i = 1 to n of s_i
        //thermodynamic averages for <m>
        if (sigma_star.isEmpty()) {
            return 0.0;
        }

        int summation = 0;
        for (int i = 1; i <= n; i++) {
            summation += sigma_star.get(i);
        }
        System.out.println("Sigma star: " + sigma_star + " Summation: " + summation);
        System.out.println("Magnetization: " + ((1.0/n) * summation));
        return (1.0/n) * summation;
    }

    public double pairCorrelationPerSpin () {
        CircularArrayList<Integer> sigma_star = currentConfiguration;

        //2.3.2
        // 1/n * summation from i = 1 to n of s_i*s_i+1
        //thermodynamic averages for <cp>

        if (sigma_star.isEmpty()) {
            return 0.0;
        }

        int summation = 0;
        for (int i = 1; i <= n; i++) {
            summation += sigma_star.get(i)*sigma_star.get(i+1);
        }

        System.out.println("Sigma star: " + sigma_star + " Summation: " + summation);
//
        System.out.println("Correlation Per Spin Pair : " + ((1.0/n) * summation));
        return (1.0/n) * summation;
    }

//    @Override
//    public void run() {
//        MetropolisAlgorithm ma = new MetropolisAlgorithm();
//
//        for(int i = 0; i < 100; i++) {
//            ma.setInitialSpinConfiguraton_Sigma0();
//            ma.createSigma1();
//            ma.replaceConfiguration();
//            ma.updateSpinGetCurrentConfig();
//            //waits for one thread to be done before another is put.
//            Magnetization.add(ma.computeMagnetizationPerSpin());
//            CorrelationPerSpin.add(ma.pairCorrelationPerSpin());
//        }
//    }

    public static void main(String[] args) {
        int N_t = 1000;
        CircularArrayList<Integer> sigma_star;
        MetropolisAlgorithm ma = new MetropolisAlgorithm();

        //function names to call for the Metropolis Algorithm. They're in the run method, I wrote this for testing.
        ma.setInitialSpinConfiguraton_Sigma0();
        ma.createSigma1();
        ma.replaceConfiguration();
        ma.updateSpinGetCurrentConfig();
        ma.computeMagnetizationPerSpin();
        ma.pairCorrelationPerSpin();

        //Begin threads.
//        ExecutorService executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//        double[] m = new double[N_t];
//        double[] c = new double[N_t];
//        try{
//            for ( int i=0; i < N_t; i++){
//                executor.execute(new MetropolisAlgorithm());
//                m[i] = meanOfEachThread(Magnetization.resultMagnetization);
//                c[i] = meanOfEachThread(CorrelationPerSpin.resultCorrelation);
//            }
//        }catch(Exception err){
//            err.printStackTrace();
//        }
//        executor.shutdown();
//
//        double meu = 0.0;
//        double ceu = 0.0;
//        try{
//            for ( int j=0; j < N_t; j++){
//                meu = sumOfAllThreads(m);
//                ceu = sumOfAllThreads(c);
//            }
//        }catch(Exception err){
//            err.printStackTrace();
//        }
    }
}

class Magnetization {
    public static ArrayList<Double> resultMagnetization = new ArrayList<Double>();

    static synchronized void add(Double d) {

        resultMagnetization.add(d);
    }
}

class CorrelationPerSpin {
    public static ArrayList<Double> resultCorrelation = new ArrayList<Double>();

    static synchronized void add(Double d) {

        resultCorrelation.add(d);
    }
}

class Main {

    public static double meanOfEachThread(ArrayList<Double> sum) {
        double results = 0.0;
        for(int j = 0; j < sum.size(); j++) {
            results += sum.get(j);
        }
        return (1.0/sum.size()) * results;
    }

    public static double sumOfAllThreads(double[] thread_sum) {
        double thread_results = 0.0;
        for(int j = 0; j < thread_sum.length; j++) {
            thread_results += thread_sum[j];
        }
        return (1.0/thread_sum.length) * thread_results;
    }

    public static void main(String[] args) {
        int N_t = 1000;
        CircularArrayList<Integer> sigma_star;
        MetropolisAlgorithm ma = new MetropolisAlgorithm();

        //function names to call for the Metropolis Algorithm. They're in the run method, I wrote this for testing.
        ma.setInitialSpinConfiguraton_Sigma0();
        ma.createSigma1();
        ma.replaceConfiguration();
        ma.updateSpinGetCurrentConfig();
        ma.computeMagnetizationPerSpin();
        ma.pairCorrelationPerSpin();

        //Begin threads.
//        ExecutorService executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//        double[] m = new double[N_t];
//        double[] c = new double[N_t];
//        try{
//            for ( int i=0; i < N_t; i++){
//                executor.execute(new MetropolisAlgorithm());
//                m[i] = meanOfEachThread(Magnetization.resultMagnetization);
//                c[i] = meanOfEachThread(CorrelationPerSpin.resultCorrelation);
//            }
//        }catch(Exception err){
//            err.printStackTrace();
//        }
//        executor.shutdown();
//
//        double meu = 0.0;
//        double ceu = 0.0;
//        try{
//            for ( int j=0; j < N_t; j++){
//                meu = sumOfAllThreads(m);
//                ceu = sumOfAllThreads(c);
//            }
//        }catch(Exception err){
//            err.printStackTrace();
//        }
    }
}


