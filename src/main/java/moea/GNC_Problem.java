package moea;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gnc.Design;
import gnc.DesignSpace;
import org.fife.ui.rsyntaxtextarea.folding.JsonFoldParser;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;
import reliability.evaluation.Evaluation_Model_2;

import javax.print.attribute.standard.JobName;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GNC_Problem extends AbstractProblem {

    public DesignSpace design_space;
    public int run_number;
    public JsonArray designs;


    public GNC_Problem(DesignSpace design_space, int run_number){
        super(1, 2);

        this.design_space = design_space;
        this.run_number = run_number;
        this.designs = new JsonArray();
    }



    @Override
    public void evaluate(Solution solution){

        // CAST
        GNC_Solution gnc_solution = (GNC_Solution) solution;

        if(!gnc_solution.already_evaluated){

            // EVALUATION

            System.out.println("\n--------------- EVALUATING DESIGN");
            ArrayList<Double> results = this.evaluate_gnc(gnc_solution.design);
            double reliability = results.get(0);
            double mass = results.get(1);
            System.out.println("--> RELIABILITY: " + reliability);
            System.out.println("---------> MASS: " + mass);

            // SET OBJECTIVE VALUES
            gnc_solution.setObjective(0, -reliability);
            gnc_solution.setObjective(1, mass);
            gnc_solution.already_evaluated = true;
        }
    }



    public ArrayList<Double> evaluate_gnc(Design design){
        ArrayList<Double> results = new ArrayList<>();


        double connection_success_rate = 1;

        ArrayList<Double> sensors = new ArrayList<>();
        for(String sensor: design.sensors){
            sensors.add(this.design_space.sensor_types.get(sensor));
        }

        ArrayList<Double> computers = new ArrayList<>();
        for(String computer: design.computers){
            computers.add(this.design_space.computer_types.get(computer));
        }

        ArrayList<Double> actuators = new ArrayList<>();
        for(String actuator: design.actuators){
            actuators.add(this.design_space.actuator_types.get(actuator));
        }

        String sensor_to_computer   = design.get_sensor_computer_string();
        String computer_to_actuator = design.get_computer_actuator_string();

        Evaluation_Model_2 model = new Evaluation_Model_2.Builder(sensors, computers, actuators, connection_success_rate)
                .connection_sensor_to_computer(sensor_to_computer)
                .connection_computer_to_actuator(computer_to_actuator)
                .build();

        double reliability = model.evaluate_reliability(true);
        double mass = this.evaluate_mass(design);

        results.add(reliability);
        results.add(mass);

        this.record_design(reliability, mass, design.get_design_object());

        return results;
    }

    public void record_design(double reliability, double mass, JsonObject design_obj){
        JsonObject design = new JsonObject();
        design.addProperty("mass", mass);
        design.addProperty("reliability", reliability);
        design.add("design", design_obj);
        this.designs.add(design);
    }

    public void write_designs(){
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter outputfile = new FileWriter("/app/results/design/designs_paper_"+this.run_number+".json");
            gson.toJson(this.designs, outputfile);
            outputfile.flush();
            outputfile.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }




    public double evaluate_mass(Design design){
        double dissimilar_component_penalty = 5/3;
        double mass = 0;

        // SUMMATE MASSES
        for(String sensor: design.sensors){
            mass += this.design_space.component_masses.get(sensor);
        }
        for(String computer: design.computers){
            mass += this.design_space.component_masses.get(computer);
        }
        for(String actuator: design.actuators){
            mass += this.design_space.component_masses.get(actuator);
        }

        if(this.is_heterogeneous(design)){
            mass += dissimilar_component_penalty;
        }

        return mass;
    }

    public boolean is_heterogeneous(Design design){

        // SENSORS
        Set<String> sensors_shrt = new HashSet<>(design.sensors);
        if(sensors_shrt.size() == 1){
            Set<String> computers_shrt = new HashSet<>(design.computers);
            if(computers_shrt.size() == 1){
                Set<String> actuators_shrt = new HashSet<>(design.actuators);
                if(actuators_shrt.size() == 1){
                    return false;
                }
            }
        }
        return true;
    }




    @Override
    public Solution newSolution(){
        return new GNC_Solution(this.design_space);
    }
}
