package moea;

import gnc.Design;
import gnc.DesignSpace;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import java.util.Random;

public class GNC_Crossover implements Variation {

    public double mutation_probability;
    public DesignSpace design_space;
    public Random rand;

    public GNC_Crossover(DesignSpace design_space, double mutation_probability){
        this.rand = new Random();
        this.mutation_probability = mutation_probability;
        this.design_space = design_space;
    }


    @Override
    public Solution[] evolve(Solution[] parents){

//        System.out.println("\n\n----> GNC CROSSOVER OPERATION: " + parents.length);
//        System.out.println("--> " + parents[0]);

        // TWO PARENTS FOR CROSSOVER
        Solution parent1 = parents[0].copy();
        Solution parent2 = parents[1].copy();

        // CAST APPROPRIATELY
        GNC_Solution papa = (GNC_Solution) parent1;
        GNC_Solution mama = (GNC_Solution) parent2;

        Design child = this.crossover(papa.design, mama.design);

        if(this.getProbabilityResult(this.mutation_probability)){
            child.mutate();
        }

        GNC_Solution child_soln = new GNC_Solution(this.design_space, child);
        Solution[] soln = new Solution[] { child_soln };
        return soln;
    }




    public Design crossover(Design papa, Design mama){

        // CROSSOVER
        Design child = new Design(papa, mama);

        // RANDOM DESIGN
//        Design child = new Design(this.design_space);

        return child;
    }








    // NUM PARENTS REQUIRED
    @Override
    public int getArity(){
        return 2;
    }

    public boolean getProbabilityResult(double probability){
        return (this.rand.nextDouble() <= probability);
    }
}
