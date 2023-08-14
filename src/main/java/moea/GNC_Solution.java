package moea;

import gnc.Design;
import gnc.DesignSpace;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryIntegerVariable;

public class GNC_Solution extends Solution {

    public Design design;
    public DesignSpace design_space;
    public boolean already_evaluated;


    public GNC_Solution(DesignSpace design_space, Design design){
        super(1, 2, 0);
        this.design = design;
        this.design_space = design_space;
        this.already_evaluated = false;

        int generated_design_id = 1;
        BinaryIntegerVariable var = new BinaryIntegerVariable(generated_design_id, 0, 10000);
        this.setVariable(0, var);
    }

    public GNC_Solution(DesignSpace design_space){
        super(1, 2, 0);
        this.design = new Design(design_space);
        this.already_evaluated = false;

        int generated_design_id = 1;
        BinaryIntegerVariable var = new BinaryIntegerVariable(generated_design_id, 0, 10000);
        this.setVariable(0, var);
    }

    protected GNC_Solution(Solution solution){
        super(solution);

        GNC_Solution gnc_sol = (GNC_Solution) solution;
        this.design = new Design(gnc_sol.design);
        this.already_evaluated = gnc_sol.already_evaluated;
    }


    public void print(){
        this.design.print();
    }












    @Override
    public Solution copy(){
        return new GNC_Solution(this);
    }
}
