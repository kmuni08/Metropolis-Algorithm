//compute the initial configuration.

import java.util.ArrayList;

public class MetropolisAlgorithm {

    private int n = 4;
    private int B = 1;
    private int C = 1;
    private int N_f = 10;

    CircularArrayList<Integer> sigma0 = new CircularArrayList<>();
    CircularArrayList<Integer> sigma1 = new CircularArrayList<>();
    CircularArrayList<Integer> currentConfiguration;

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
        System.out.println("sigma0: " + sigma0);
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
        int randomInt = generateRand(1, n);

        System.out.println("sigma1: " + sigma1);
        System.out.println(randomInt);

        sigma1.set(randomInt, (-1)*(sigma1.get(randomInt)));
        System.out.println("sigma1 new : " + sigma1);
        return sigma1;
    }

    double energyCompute(CircularArrayList<Integer> sigma, int B, int C, int randomInteger)
    {
        double sum_of_B = 0;
        double sum_of_C = 0;

        sum_of_B = B * sigma.get(randomInteger - 1) + B * sigma.get(randomInteger) + B * sigma.get(randomInteger + 1);
        sum_of_C = (C * sigma.get(randomInteger - 1) * sigma.get(randomInteger)) + (C * sigma.get(randomInteger) * sigma.get(randomInteger+ 1)) + (C * sigma.get(randomInteger + 1)*sigma.get(randomInteger + 2));

        return -1*(sum_of_B + sum_of_C);
    }

    public CircularArrayList<Integer> replaceConfiguration(double T) {
        int randomInt = generateRand(1, n);
        double deltaE = energyCompute(sigma1, B, C, randomInt)-energyCompute(sigma0, B, C, randomInt);
        System.out.println("deltaE " +  deltaE);
        double p = 0.0;
        if(deltaE < 0) {
            currentConfiguration = sigma1;
        } else if (deltaE> 0) {
            //pick a p value.
            p = Math.exp((-1*deltaE) / T);
        }
        double r = generateRandom(0, 1);
        if (r < p) {
            //set current configuration to be new configuration.
            currentConfiguration = sigma1;
        } else {
            currentConfiguration = sigma0;
        }
        return currentConfiguration;
    }

     public CircularArrayList<Integer> updateSpinGetCurrentConfig(double T)
    {
        for(int i = 0; i < N_f * n; i++) {
            currentConfiguration = replaceConfiguration(T);
        }
        return currentConfiguration;
    }

    public double computeMagnetizationPerSpin () {
        CircularArrayList<Integer> sigma_star = updateSpinGetCurrentConfig(1.9);
        //2.3.1
        // 1/n * summation from i = 1 to n of s_i
        //thermodynamic averages for <m>
        if (sigma_star.isEmpty()) {
            return 0.0;
        }

        int summation = 0;
        for (int i = 1; i < n; i++) {
            summation += sigma_star.get(i);
        }
        return (1.0/n) * summation;
    }

    public double pairCorrelationPerSpin () {
        CircularArrayList<Integer> sigma_star = updateSpinGetCurrentConfig(1.9);

        //2.3.2
        // 1/n * summation from i = 1 to n of s_i*s_i+1
        //thermodynamic averages for <cp>

        if (sigma_star.isEmpty()) {
            return 0.0;
        }

        int summation = 0;
        for (int i = 1; i < n; i++) {
            summation += sigma_star.get(i)*sigma_star.get(i+1);
        }
        return (1.0/n) * summation;
    }


    public static void main(String[] args) {

        int N_f = 100;
        int n = 100;

        MetropolisAlgorithm ma = new MetropolisAlgorithm();
        CircularArrayList<Integer> sigma_star;
        ma.setInitialSpinConfiguraton_Sigma0();
        ma.createSigma1();

//        for (int i = 0; i < n*N_f; i++) {
//            sigma_star = ma.replace_current_config();
////            System.out.println("Current configuration is " + sigma_star);
//        }

    }
}


