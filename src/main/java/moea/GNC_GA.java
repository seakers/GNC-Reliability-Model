package moea;

import gnc.Design;
import gnc.DesignSpace;
import org.moeaframework.algorithm.EpsilonMOEA;
import org.moeaframework.core.*;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.ParetoObjectiveComparator;
import org.moeaframework.core.operator.InjectedInitialization;
import org.moeaframework.core.operator.TournamentSelection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GNC_GA implements Runnable{


    public Problem problem;
    public double mutation_probability;
    public int num_evaluations;
    public List<Solution> solutions;
    public EpsilonMOEA eMOEA;
    public DesignSpace design_space;
    public int run_number;

    public GNC_GA(DesignSpace design_space, int initial_pop_size, int num_evaluations, double mutation_probability, int run_number){
        this.mutation_probability = mutation_probability;
        this.num_evaluations = num_evaluations;
        this.design_space = design_space;

        // CREATE PROBLEM
        this.problem = new GNC_Problem(this.design_space, run_number);

        // INITIALIZE SOLUTIONS
        this.design_space.initialize_population(initial_pop_size);
        this.solutions = new ArrayList<>(initial_pop_size);
        for(Design design: this.design_space.population){
            GNC_Solution gnc_solution = new GNC_Solution(this.design_space, design);
            gnc_solution.already_evaluated = false;
            this.solutions.add(gnc_solution);
        }
        this.run_number = run_number;
    }





    public void print_solutions(){
        for(Solution solution: this.solutions){
            ((GNC_Solution) solution).print();
        }
    }

    public void initialize(){

        InjectedInitialization initialization = new InjectedInitialization(this.problem, this.solutions.size(), this.solutions);

        double[]                   epsilonDouble = new double[]{0.001, 1};
        Population population    = new Population();
        EpsilonBoxDominanceArchive archive       = new EpsilonBoxDominanceArchive(epsilonDouble);

        ChainedComparator comp      = new ChainedComparator(new ParetoObjectiveComparator());
        TournamentSelection selection = new TournamentSelection(2, comp);

        GNC_Crossover crossover = new GNC_Crossover(this.design_space, this.mutation_probability);

        this.eMOEA = new EpsilonMOEA(this.problem, population, archive, selection, crossover, initialization);
    }



    public void run(){

        this.initialize();

        // SUBMIT MOEA
        ExecutorService pool   = Executors.newFixedThreadPool(1);
        CompletionService<Algorithm> ecs    = new ExecutorCompletionService<>(pool);
        ecs.submit(new GNC_Search(this.eMOEA, this.num_evaluations, this.run_number));


        try {
            org.moeaframework.core.Algorithm alg = ecs.take().get();
            NondominatedPopulation result = alg.getResult();

        }
        catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        ((GNC_Problem) this.problem).write_designs();

        pool.shutdown();
        System.out.println("--> FINISHED");

    }

}
