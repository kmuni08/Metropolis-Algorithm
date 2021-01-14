//compute the initial configuration.

import java.util.ArrayList;

public class MetropolisAlgorithm  {

    private int n = 4;
    private int B = 1;
    private int C = 1;
    private int N_f = 2;
    private double T = 1.9;
    private int randomInt;

    CircularArrayList<Integer> sigma0 = new CircularArrayList<>();
    CircularArrayList<Integer> sigma1 = new CircularArrayList<>();
    CircularArrayList<Integer> currentConfiguration;
    ArrayList<Double> resultMagnetization = new ArrayList<Double>();

    public CircularArrayList<Integer> setInitialSpinConfiguraton_Sigma0 () {
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
//        System.out.println("Current Configuration: " + currentConfiguration);
        return currentConfiguration;
    }

    public int generateRand(int first, int second) {
        return PseudoRandomGenerator.getPseudoRandomNumberInteger(first, second);
    }

    public double generateRandom(double first, double second) {
        return PseudoRandomGenerator.getPseudoRandomNumberDouble(first, second);
    }

    public CircularArrayList<Integer> createSigma1 () {
        //do the pseudorandom generator
        if (sigma1.isEmpty()) {
            sigma1.addAll(sigma0);
        }
        sigma1 = changeSigma1(sigma1);
//        System.out.println("sigma1: " + sigma1);
        return sigma1;
    }

    public CircularArrayList<Integer> changeSigma1 (CircularArrayList<Integer> sigma) {
        randomInt = generateRand(1, n);
//        System.out.println("Random int in create sigma1: " + randomInt);
        sigma.set(randomInt, (-1)*(sigma1.get(randomInt)));
//        System.out.println("sigma1 new : " + sigma);
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

    public CircularArrayList<Integer> replaceConfiguration() {
//        System.out.println("Random Integer: " + randomInt);
        double deltaE = energyCompute(sigma1, B, C) - energyCompute(currentConfiguration, B, C);
//        System.out.println("deltaE " +  deltaE);
        double p = 0.0;
        if(deltaE < 0) {
            currentConfiguration = sigma1;
        } else if (deltaE> 0) {
            //pick a p value.
            p = Math.exp((-1*deltaE) / T);
        }
        double r = generateRandom(0, 1);
//        System.out.println("p value: " + p);
//        System.out.println("Random double " + r);
        if (r < p) {
            //set current configuration to be new configuration.
            currentConfiguration = sigma1;
        } else {
            currentConfiguration = sigma0;
        }

//        System.out.println("New Current Configuration " + currentConfiguration);
        return currentConfiguration;
    }

     public CircularArrayList<Integer> updateSpinGetCurrentConfig()
    {
        for(int i = 0; i < (N_f * n); i++) {
            //call sigma1 and energyCompute.
//            System.out.println("i value in updateSpingGetCurrentConfig " + i);
            sigma1 = changeSigma1(sigma1);
//            System.out.println("Changed Sigma 1: " + sigma1);
            currentConfiguration = replaceConfiguration();

        }
        return currentConfiguration;
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
//        System.out.println("Sigma star: " + sigma_star + " Summation: " + summation);
//        System.out.println("Magnetization: " + ((1.0/n) * summation));
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

//        System.out.println("Sigma star: " + sigma_star + " Summation: " + summation);
//
//        System.out.println("Correlation Per Spin Pair : " + ((1.0/n) * summation));
        return (1.0/n) * summation;
    }


    public static void main(String[] args) {
        int N_t = 1000;

//        MetropolisAlgorithm [] ma = new MetropolisAlgorithm[N_t];
        CircularArrayList<Integer> sigma_star;

        MetropolisAlgorithm ma = new MetropolisAlgorithm();

        ma.setInitialSpinConfiguraton_Sigma0();
        ma.createSigma1();
        ma.replaceConfiguration();
        ma.updateSpinGetCurrentConfig();

        ma.computeMagnetizationPerSpin();
        ma.pairCorrelationPerSpin();
//        ma.resultMagnetization.add(ma.computeMagnetizationPerSpin());

//        Thread []thread = new Thread[N_t];
//        for (int i = 0; i < N_t; i++) {
//            thread[i] = new Thread(ma[i]);
//            thread[i].start();
//        }
//
//        try {
//            for (int i = 0; i < N_t; i++) {
//                thread[i].join();
////                ma[i].resultMagnetization;
//            }
//        } catch (Exception e) {
//
//        }
//
//        //put objects in array index.
//
//    }

//    @Override
//    public void run() {
//        MetropolisAlgorithm ma = new MetropolisAlgorithm();
//        CircularArrayList<Integer> sigma_star;
//
//        for(int i = 0; i < 100; i++) {
//            ma.setInitialSpinConfiguraton_Sigma0();
//            ma.createSigma1();
//            ma.replaceConfiguration();
//            ma.updateSpinGetCurrentConfig();
//            ma.resultMagnetization.add(ma.computeMagnetizationPerSpin());
//        }
    }
}


