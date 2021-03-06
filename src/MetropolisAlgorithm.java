//compute the initial configuration.

import java.util.ArrayList;

public class MetropolisAlgorithm {

    public double magnetization;
    public double correlationperpair;
    private int randomInt;

    CircularArrayList<Integer> sigma0 = new CircularArrayList<>();
    CircularArrayList<Integer> sigma1 = new CircularArrayList<>();
    CircularArrayList<Integer> currentConfiguration = new CircularArrayList<>();

    public void setInitialSpinConfiguraton_Sigma0 () {
        for (int i = 0; i < Computations.getN(); i++) {
            if (Computations.getC()>=0)
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
        if (currentConfiguration.isEmpty()) {
            currentConfiguration.addAll(sigma0);
        }
    }

    public int generateRand(int first, int second)
    {
        return PseudoRandomGenerator.getPseudoRandomNumberInteger(first, second);
    }

    public double generateRandom(double first, double second)
    {
        return PseudoRandomGenerator.getPseudoRandomNumberDouble(first, second);
    }

    public void createSigma1 ()
    {
        if (sigma1.isEmpty()) {
            sigma1.addAll(sigma0);
        }
        sigma1 = changeSigma1(sigma1);
    }

    public CircularArrayList<Integer> changeSigma1 (CircularArrayList<Integer> sigma)
    {
        randomInt = generateRand(1, Computations.getN());
        sigma.set(randomInt, (-1)*(sigma.get(randomInt)));
        return sigma;
    }


    double energyCompute(CircularArrayList<Integer> sigma, double B, double C)
    {
        double sum_of_B = 0.0;
        double sum_of_C = 0.0;

        sum_of_B = B * sigma.get(randomInt - 1) + B * sigma.get(randomInt) + B * sigma.get(randomInt + 1);
        sum_of_C = (C * sigma.get(randomInt - 1) * sigma.get(randomInt)) + (C * sigma.get(randomInt) * sigma.get(randomInt + 1)) + (C * sigma.get(randomInt + 1)*sigma.get(randomInt + 2));

        return -1.0*(sum_of_B + sum_of_C);
    }

    public CircularArrayList<Integer> replaceConfiguration() {
        double deltaE = energyCompute(sigma1, Computations.getB(), Computations.getC()) - energyCompute(currentConfiguration, Computations.getB(), Computations.getC());
        double p = 0.0;
        if(deltaE < 0) {
            currentConfiguration.clear();
            currentConfiguration.addAll(sigma1);
        } else {
            //pick a p value.
            p = Math.exp((-1.0*deltaE) / Computations.getT());
            double r = generateRandom(0, 1);
            if (r < p) {
                //set current configuration to be new configuration.
                currentConfiguration.clear();
                currentConfiguration.addAll(sigma1);
            }
        }
        return currentConfiguration;
    }

    public void updateSpinGetCurrentConfig()
    {
        CircularArrayList<Integer> temp = new CircularArrayList<>();
        temp.addAll(currentConfiguration);
        for(int i = 0; i < (Computations.getN_f() * Computations.getN()); i++) {
            //call sigma1 and energyCompute.
            sigma1.clear();
            sigma1.addAll(changeSigma1(temp));
            temp.clear();
            temp.addAll(replaceConfiguration());
        }
    }

    public double computeMagnetizationPerSpin ()
    {
        CircularArrayList<Integer> sigma_star = currentConfiguration;
        if (sigma_star.isEmpty()) {
            return 0.0;
        }
        double summation = 0.0;
        for (int i = 0; i < sigma_star.size(); i++) {
            summation += sigma_star.get(i);
        }

        return summation/sigma_star.size();
    }

    public double pairCorrelationPerSpin ()
    {
        CircularArrayList<Integer> sigma_star = currentConfiguration;
        if (sigma_star.isEmpty()) {
            return 0.0;
        }
        double summation = 0.;
        for (int i = 0; i < sigma_star.size(); i++) {
            summation += sigma_star.get(i)*sigma_star.get(i+1);
        }
        return summation/sigma_star.size();
    }

    public void computationInThreads()
    {
        MetropolisAlgorithm ma = new MetropolisAlgorithm();
        ArrayList<Double> resultMagnetization = new ArrayList<Double>();
        ArrayList<Double> resultCorrelation = new ArrayList<Double>();

        for(int i = 0; i < Computations.getN_m(); i++) {
            ma.setInitialSpinConfiguraton_Sigma0();
            ma.updateSpinGetCurrentConfig();
            //waits for one thread to be done before another is put.
            resultMagnetization.add(ma.computeMagnetizationPerSpin());
            resultCorrelation.add(ma.pairCorrelationPerSpin());
        }
        magnetization = Computations.meanOfEachThread(resultMagnetization);
        correlationperpair = Computations.meanOfEachThread(resultCorrelation);
    }
}



