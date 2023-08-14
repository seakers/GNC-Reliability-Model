package gnc;

import java.util.ArrayList;
import java.util.HashMap;

public class DesignSpace {


    public HashMap<String, Double> sensor_types;
    public HashMap<String, Double> computer_types;
    public HashMap<String, Double> actuator_types;

    public HashMap<String, Double> component_masses;

    public int max_sensors;
    public int max_computers;
    public int max_actuators;

    public ArrayList<Design> population;


    public DesignSpace(HashMap<String, Double> sensor_types, HashMap<String, Double> computer_types, HashMap<String, Double> actuator_types, int max_sensors, int max_computers, int max_actuators){
        this.sensor_types = sensor_types;
        this.computer_types = computer_types;
        this.actuator_types = actuator_types;

        this.max_sensors = max_sensors;
        this.max_actuators = max_actuators;
        this.max_computers = max_computers;

        this.component_masses = new HashMap<>();

        this.population = new ArrayList<>();
    }



    public void set_mass_properties(HashMap<String, Double> component_masses){
        this.component_masses = component_masses;
    }




    public void initialize_population(int size){
        for(int x = 0; x < size; x++){
            this.population.add(new Design(this));
        }
    }

    public void print_population(){
        for(Design des: this.population){
            des.print();
        }


    }


























}
