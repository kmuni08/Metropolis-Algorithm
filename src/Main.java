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

    public static void main(String[] args)
    {

        int N_t = 1000;
        //Begin threads.
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        double[] m = new double[N_t];
        double[] c = new double[N_t];
        ArrayList<MetropolisAlgorithm> storeValues = new ArrayList<>();

        try {
            for (int i = 0; i < N_t; i++) {
                MetropolisAlgorithm a = new MetropolisAlgorithm();
                storeValues.add(a);
                executor.execute(a); // Thread start
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        Future<ArrayList <MetropolisAlgorithm>> future = executor.submit(new Callable<ArrayList <MetropolisAlgorithm>>() {
            @Override
            public ArrayList <MetropolisAlgorithm> call() throws Exception {
                try {
                    System.out.print("Values in the arrays after computing the mean ");
                }catch (Exception err) {
                    err.printStackTrace();
                }
                return storeValues;
            }
        });

        try {
            System.out.println(future.get());
        } catch(ExecutionException e) {
            System.out.println("Something went wrong");
        } catch (InterruptedException e) {
            System.out.println("Thread running the task was interrupted");
        }

        executor.shutdown();


        for (int i = 0; i < storeValues.size(); i++) {
            m[i] = storeValues.get(i).magnetization;
            c[i] = storeValues.get(i).correlationperpair;
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
    }
}
