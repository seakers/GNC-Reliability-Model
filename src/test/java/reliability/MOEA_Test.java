package reliability;

import gnc.Design;
import gnc.DesignSpace;
import moea.GNC_GA;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;


public class MOEA_Test {

    @Test public void testModel() {



        int number_runs = 1;
        for(int run = 0; run < number_runs; run++){
            System.gc();

            DesignSpace design_space = this.create_design_space();

            this.set_mass_properties(design_space);

            int initial_pop_size = 30;
            int num_evaluations = 10000;
            double mutation_probability = 0.6; // strongest seems to be 0.6

            GNC_GA algorithm = new GNC_GA(design_space, initial_pop_size, num_evaluations, mutation_probability, run);

            algorithm.print_solutions();

            algorithm.run();

        }

    }




    public void set_mass_properties(DesignSpace design_space){

        HashMap<String, Double> mass_properties = new HashMap<>();

        mass_properties.put("s1", 3.0);
        mass_properties.put("s2", 6.0);
        mass_properties.put("s3", 9.0);

        mass_properties.put("c1", 3.0);
        mass_properties.put("c2", 5.0);
        mass_properties.put("c3", 10.0);

        mass_properties.put("a1", 3.5);
        mass_properties.put("a2", 5.5);
        mass_properties.put("a3", 9.5);

        design_space.set_mass_properties(mass_properties);
    }

    public DesignSpace create_design_space(){

        HashMap<String, Double> sensor_types = new HashMap<>();
        sensor_types.put("s1", 0.9985);
        sensor_types.put("s2", 0.999);
        sensor_types.put("s3", 0.9995);

        HashMap<String, Double> computer_types = new HashMap<>();
        computer_types.put("c1", 0.999);
        computer_types.put("c2", 0.9996);
        computer_types.put("c3", 0.9998);

        HashMap<String, Double> actuator_types = new HashMap<>();
        actuator_types.put("a1", 0.9992);
        actuator_types.put("a2", 0.998);
        actuator_types.put("a3", 0.999);

        int max_sensors   = 3;
        int max_computers = 3;
        int max_actuators = 3;

        DesignSpace design_space = new DesignSpace(sensor_types, computer_types, actuator_types, max_sensors, max_computers, max_actuators);
        return design_space;
    }



}
