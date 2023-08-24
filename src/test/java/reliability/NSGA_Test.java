package reliability;

import gnc.Design;
import gnc.DesignSpace;
import moea.GNC_GA;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import moea.AlgorithmRun;

public class NSGA_Test {

    @Test public void testModel() {
        System.out.println("--> RUNNING THREADS");

        int num_runs = 2;
        ExecutorService executor = Executors.newFixedThreadPool(num_runs);
        for (int i = 0; i < num_runs; i++) {
            executor.submit(new AlgorithmRun(i));
        }

        // Shutdown the executor after submitting all tasks
        executor.shutdown();
        try {
            // waits for termination, indefinitely
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
