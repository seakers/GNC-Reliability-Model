package gnc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Design {

    public ArrayList<String> sensors;
    public ArrayList<String> computers;
    public ArrayList<String> actuators;

    public ArrayList<Integer> sensor_to_computer;
    public ArrayList<Integer> computer_to_actuator;

    public Random rand;
    public DesignSpace design_space;


    // CROSSOVER OPERATOR
    public Design(Design papa, Design mama){
        this.rand = new Random();

        this.design_space = papa.design_space;


        this.sensors = this.crossover_components2(papa.sensors, mama.sensors);
        this.computers = this.crossover_components2(papa.computers, mama.computers);
        this.actuators = this.crossover_components2(papa.actuators, mama.actuators);

        ArrayList<ArrayList<String>> papa_chains = this.derive_chains(papa);
        ArrayList<ArrayList<String>> mama_chains = this.derive_chains(mama);

        this.initialize_topology();

        this.apply_chains(papa_chains, mama_chains);

        this.sensor_to_computer = this.enforce_min_assignation(this.sensor_to_computer, this.computers.size(), this.sensors.size());
        this.computer_to_actuator = this.enforce_min_assignation(this.computer_to_actuator, this.actuators.size(), this.computers.size());

    }

    public void initialize_topology(){
        this.sensor_to_computer = new ArrayList<>();
        this.computer_to_actuator = new ArrayList<>();

        for(int x = 0; x < (this.sensors.size() * this.computers.size()); x++){
            this.sensor_to_computer.add(0);
        }
        for(int x = 0; x < (this.computers.size() * this.actuators.size()); x++){
            this.computer_to_actuator.add(0);
        }
    }



    public void apply_chains(ArrayList<ArrayList<String>> papa_chains, ArrayList<ArrayList<String>> mama_chains){
        int num_papa_chains = papa_chains.size();
        int num_mama_chains = mama_chains.size();

        int num_child_chains = 0;
        if(this.rand.nextBoolean()){
            num_child_chains = num_papa_chains;
        }
        else{
            num_child_chains = num_mama_chains;
        }

        ArrayList<ArrayList<String>> potential_child_chains = new ArrayList<>();
        potential_child_chains.addAll(papa_chains);
        potential_child_chains.addAll(mama_chains);
        Collections.shuffle(potential_child_chains);
        int chains_applied = 0;
        for(ArrayList<String> chain: potential_child_chains){
            if(chains_applied >= num_child_chains){
                break;
            }
            if(this.check_chain_feasibility(chain)){
                this.implement_chain(chain);
                chains_applied++;
            }
        }




    }

    public void implement_chain(ArrayList<String> chain){

        int sensor_idx = 0;
        int computer_idx = 0;
        int actuator_idx = 0;

        if(this.rand.nextBoolean()){
            sensor_idx = this.sensors.indexOf(chain.get(0));
        }
        else{
            sensor_idx = this.sensors.lastIndexOf(chain.get(0));
        }
        if(this.rand.nextBoolean()){
            computer_idx = this.computers.indexOf(chain.get(1));
        }
        else{
            computer_idx = this.computers.lastIndexOf(chain.get(1));
        }
        if(this.rand.nextBoolean()){
            actuator_idx = this.actuators.indexOf(chain.get(2));
        }
        else{
            actuator_idx = this.actuators.lastIndexOf(chain.get(2));
        }

//        this.print();
//        System.out.println("--> CHAIN: " + chain);
//        System.out.println("--> SENSOR IDX: " + sensor_idx);
//        System.out.println("--> COMPUTER IDX: " + sensor_idx);
//        System.out.println("--> ACTUATOR IDX: " + sensor_idx);

        // 1. implement sensor -> computer
        int sensor_computer_idx = (this.sensors.size() * computer_idx) + sensor_idx;
        this.sensor_to_computer.set(sensor_computer_idx, 1);

        // 2. implement computer -> actuator
        int computer_actuator_idx = (this.computers.size() * actuator_idx) + computer_idx;
        this.computer_to_actuator.set(computer_actuator_idx, 1);
    }

    public boolean check_chain_feasibility(ArrayList<String> chain){
        if(this.sensors.contains(chain.get(0)) && this.computers.contains(chain.get(1)) && this.actuators.contains(chain.get(2))){
            return true;
        }
        return false;
    }


    public ArrayList<ArrayList<String>> derive_chains(Design parent){

        // Each ArrayList<String> is a chain
        ArrayList<ArrayList<String>> chains = new ArrayList<>();

        // SENSOR -> COMPUTER
        HashMap<String, ArrayList<String>> sensor_computer = new HashMap<>();
        for(String sensor: parent.sensors){
            sensor_computer.put(sensor, new ArrayList<>());
        }
        int counter = 0;
        for(String computer: parent.computers){
            for(String sensor: parent.sensors){
                if(parent.sensor_to_computer.get(counter) == 1){
                       sensor_computer.get(sensor).add(computer);
                }
                counter++;
            }
        }

        // COMPUTER -> ACTUATOR
        counter = 0;
        HashMap<String, ArrayList<String>> computer_actuator = new HashMap<>();
        for(String computer: parent.computers){
            computer_actuator.put(computer, new ArrayList<>());
        }
        for(String actuator: parent.actuators){
            for(String computer: parent.computers){
                if(parent.computer_to_actuator.get(counter) == 1){
                    computer_actuator.get(computer).add(actuator);
                }
                counter++;
            }
        }

        // FLATTEN MAPS INTO CHAINS
        for(String sensor: sensor_computer.keySet()){
            ArrayList<String> computers = sensor_computer.get(sensor);
            for(String computer: computers){
                ArrayList<String> actuators = computer_actuator.get(computer);
                for(String actuator: actuators){
                    ArrayList<String> chain = new ArrayList<>();
                    chain.add(sensor);
                    chain.add(computer);
                    chain.add(actuator);
                    chains.add(chain);
                }
            }
        }

        return chains;
    }


    public ArrayList<String> crossover_components(ArrayList<String> papa, ArrayList<String> mama){
        if(this.rand.nextInt(2) == 0){
            return papa;
        }
        else{
            return mama;
        }
    }


    public ArrayList<String> crossover_components2(ArrayList<String> papa, ArrayList<String> mama){
        ArrayList<String> child_components = new ArrayList<>();

        // 1. Choose which # of components to keep from a parent
        int num_papa = papa.size();
        int num_mama = mama.size();
        int num_child = 0;
        if(this.rand.nextBoolean()){
            num_child = num_papa;
        }
        else{
            num_child = num_mama;
        }

        // 2. Randomly assign
        for(int x = 0; x < num_child; x++){
            if(this.rand.nextBoolean()){
                if(x >= papa.size()){
                    child_components.add(mama.get(x));
                }
                else{
                    child_components.add(papa.get(x));
                }
            }
            else{
                if(x >= mama.size()){
                    child_components.add(papa.get(x));
                }
                else{
                    child_components.add(mama.get(x));
                }
            }
        }

        return child_components;
    }

















    // COPY CONSTRUCTOR
    public Design(Design copy){
        this.sensors = copy.sensors;
        this.computers = copy.computers;
        this.actuators = copy.actuators;
        this.sensor_to_computer = copy.sensor_to_computer;
        this.computer_to_actuator = copy.computer_to_actuator;
        this.rand = copy.rand;
        this.design_space = copy.design_space;
    }


    // CONSTRUCTS A RANDOM DESIGN
    public Design(DesignSpace design_space){
        this.rand = new Random();

        this.sensors = new ArrayList<>();
        this.computers = new ArrayList<>();
        this.actuators = new ArrayList<>();


        this.sensors   = this.random_component_keys(design_space.max_sensors, design_space.sensor_types);
        this.computers = this.random_component_keys(design_space.max_computers, design_space.computer_types);
        this.actuators = this.random_component_keys(design_space.max_actuators, design_space.actuator_types);

        this.sensor_to_computer = this.random_bit_string_min_assignation(this.computers.size(), this.sensors.size());
        this.computer_to_actuator = this.random_bit_string_min_assignation(this.actuators.size(), this.computers.size());

        this.design_space = design_space;
    }


    public ArrayList<Integer> enforce_min_assignation(ArrayList<Integer> chromosome, int num_assign_to, int num_assign_from){

        // 1. Enforce first constraint
        int idx = 0;
        for(int x = 0; x < num_assign_to; x++){

            boolean assign_to_sat = false;
            ArrayList<Integer> assign_to_indices = new ArrayList<>();
            for(int y = 0; y < num_assign_from; y++){
                Integer bit = chromosome.get(idx);
                if(bit.equals(1)){
                    // This assign_to element has at least one assign_from element assigned to it
                    assign_to_sat = true;
                }
                assign_to_indices.add(idx);
                idx++;
            }
            // Assign 1 to a random bit
            if(!assign_to_sat){
                int rand_idx = assign_to_indices.get(this.rand.nextInt(assign_to_indices.size()));
                chromosome.set(rand_idx, 1);
            }
        }

        // 2. Enforce second constraint
        for(int x = 0; x < num_assign_from; x++){

            // For each assign_from element, find all its corresponding bit positions in the chromosome
            ArrayList<Integer> bit_positions = new ArrayList<>();
            for(int y = 0; y < num_assign_to; y++){
                int pos = x + (num_assign_from * y);
                bit_positions.add(pos);
            }

            // Check if each assign_from element is assigned to at least one assign_to element
            boolean assign_from_sat = false;
            for(Integer pos: bit_positions){
                Integer bit = chromosome.get(pos);
                if(bit.equals(1)){
                    assign_from_sat = true;
                }
            }

            // Assign 1 to a random bit pos if the constraint isn't satisfied
            if(!assign_from_sat){
                int rand_idx = bit_positions.get(this.rand.nextInt(bit_positions.size()));
                chromosome.set(rand_idx, 1);
            }
        }

        return chromosome;
    }


    public ArrayList<Integer> random_bit_string_min_assignation(int num_assign_to, int num_assign_from){
        ArrayList<Integer> chromosome = this.random_bit_string(num_assign_to * num_assign_from);

        // 1. Enforce first constraint
        int idx = 0;
        for(int x = 0; x < num_assign_to; x++){

            boolean assign_to_sat = false;
            ArrayList<Integer> assign_to_indices = new ArrayList<>();
            for(int y = 0; y < num_assign_from; y++){
                Integer bit = chromosome.get(idx);
                if(bit.equals(1)){
                    // This assign_to element has at least one assign_from element assigned to it
                    assign_to_sat = true;
                }
                assign_to_indices.add(idx);
                idx++;
            }
            // Assign 1 to a random bit
            if(!assign_to_sat){
                int rand_idx = assign_to_indices.get(this.rand.nextInt(assign_to_indices.size()));
                chromosome.set(rand_idx, 1);
            }
        }

        // 2. Enforce second constraint
        for(int x = 0; x < num_assign_from; x++){

            // For each assign_from element, find all its corresponding bit positions in the chromosome
            ArrayList<Integer> bit_positions = new ArrayList<>();
            for(int y = 0; y < num_assign_to; y++){
                int pos = x + (num_assign_from * y);
                bit_positions.add(pos);
            }

            // Check if each assign_from element is assigned to at least one assign_to element
            boolean assign_from_sat = false;
            for(Integer pos: bit_positions){
                Integer bit = chromosome.get(pos);
                if(bit.equals(1)){
                    assign_from_sat = true;
                }
            }

            // Assign 1 to a random bit pos if the constraint isn't satisfied
            if(!assign_from_sat){
                int rand_idx = bit_positions.get(this.rand.nextInt(bit_positions.size()));
                chromosome.set(rand_idx, 1);
            }
        }

        return chromosome;
    }

    public ArrayList<Integer> random_bit_string(int length){
        ArrayList<Integer> bits = new ArrayList<>();
        for(int x = 0; x < length; x++){
            int bit = this.rand.nextInt(2);
            bits.add(bit);
        }
        return bits;
    }

    public ArrayList<String> random_component_keys(int max_component, HashMap<String, Double> component_map){
        // DETERMINE NUMBER OF SENSORS
        int num_sensors = this.get_random_component_num(max_component);

        // GET LIST OF SENSOR TYPES
        ArrayList<String> sensor_keys = new ArrayList<>();
        for(int x = 0; x < num_sensors; x++){
            sensor_keys.add(this.get_random_component_type(component_map));
        }

        Collections.sort(sensor_keys);
        return sensor_keys;
    }

    /*
        Returns: 1 / 2 / 3 / ... / n
     */
    public int get_random_component_num(int max_components){
        int num = this.rand.nextInt(max_components) + 1;
        return num;
    }

    public String get_random_component_type(HashMap<String, Double> component_map){
        ArrayList<String> keys = new ArrayList<>(component_map.keySet());
        return keys.get(this.rand.nextInt(keys.size()));
    }



    public String get_sensor_computer_string(){
        String topology = "";
        for(Integer bit: this.sensor_to_computer){
            topology += Integer.toString(bit);
        }
        return topology;
    }

    public String get_computer_actuator_string(){
        String topology = "";
        for(Integer bit: this.computer_to_actuator){
            topology += Integer.toString(bit);
        }
        return topology;
    }

    public void print(){
        System.out.println("\n-------- DESIGN --------");
        System.out.println("--- SENSORS: " + this.sensors);
        System.out.println("--- COMPUTERS: " + this.computers);
        System.out.println("--- ACTUATORS: " + this.actuators);
        System.out.println("--- SENSORS TO COMPUTERS: " + this.sensor_to_computer);
        System.out.println("--- COMPUTERS TO ACTUATORS: " + this.computer_to_actuator);
    }









    public void mutate(){
        if(this.get_probability_result(0.5)){
            this.mutate_component();
        }
        else{
            this.mutate_topology();
        }
    }


    public void mutate_topology(){
        if(this.rand.nextBoolean()){
            int rand_idx = this.rand.nextInt(this.sensor_to_computer.size());
            if(this.sensor_to_computer.get(rand_idx) == 0){
                this.sensor_to_computer.set(rand_idx, 1);
            }
            else{
                this.sensor_to_computer.set(rand_idx, 0);
            }
        }
        else{
            int rand_idx = this.rand.nextInt(this.computer_to_actuator.size());
            if(this.computer_to_actuator.get(rand_idx) == 0){
                this.computer_to_actuator.set(rand_idx, 1);
            }
            else{
                this.computer_to_actuator.set(rand_idx, 0);
            }
        }
    }


    public void mutate_component(){
        if(this.rand.nextBoolean()){
            this.add_component();
        }
        else{
            this.remove_component();
        }
    }




    public void add_component(){

        ArrayList<String> fesiable_components = new ArrayList<>();
        if(this.sensors.size() < this.design_space.max_sensors){
            fesiable_components.add("sensors");
        }
        if(this.computers.size() < this.design_space.max_computers){
            fesiable_components.add("computers");
        }
        if(this.actuators.size() < this.design_space.max_actuators){
            fesiable_components.add("actuators");
        }

        if(fesiable_components.isEmpty()){
            this.remove_component();
        }
        else{
            String to_add = fesiable_components.get(this.rand.nextInt(fesiable_components.size()));
            if(to_add == "sensors"){
                this.add_sensor();
            }
            if(to_add == "computers"){
                this.add_computer();
            }
            if(to_add == "actuators"){
                this.add_actuator();
            }
        }
    }

    public void add_sensor(){
        String new_component = this.get_random_component_type(this.design_space.sensor_types);

        int num_computers = this.computers.size();
        int num_sensors = this.sensors.size();

        boolean first = true;
        for(int x = num_computers; x > 0; x--){
            if(first){
                this.sensor_to_computer.add(this.get_random_bit());
                first = false;
                continue;
            }
            this.sensor_to_computer.add(num_sensors*(x), this.get_random_bit());
        }
        this.sensors.add(new_component);
    }

    public void add_actuator(){
        String new_component = this.get_random_component_type(this.design_space.actuator_types);

        int num_computers = this.computers.size();

        for(int x = 0; x < num_computers; x++){
            this.computer_to_actuator.add(this.get_random_bit());
        }

        this.actuators.add(new_component);
    }

    public void add_computer(){
        String new_component = this.get_random_component_type(this.design_space.computer_types);

        int num_computers = this.computers.size();
        int num_sensors = this.sensors.size();
        int num_actuators = this.actuators.size();

        for(int x = 0; x < num_sensors; x++){
            this.sensor_to_computer.add(this.get_random_bit());
        }

        boolean first = true;
        for(int x = num_actuators; x > 0; x--){
            if(first){
                this.computer_to_actuator.add(this.get_random_bit());
                first = false;
                continue;
            }
            this.computer_to_actuator.add(num_computers*(x), this.get_random_bit());
        }
        this.computers.add(new_component);
    }



    public void remove_component(){

        ArrayList<String> fesiable_components = new ArrayList<>();
        if(this.sensors.size() > 1){
            fesiable_components.add("sensors");
        }
        if(this.computers.size() > 1){
            fesiable_components.add("computers");
        }
        if(this.actuators.size() > 1){
            fesiable_components.add("actuators");
        }
        if(fesiable_components.isEmpty()){
            return;
        }
        else{
            String to_add = fesiable_components.get(this.rand.nextInt(fesiable_components.size()));
            if(to_add == "sensors"){
                this.remove_sensor();
            }
            if(to_add == "computers"){
                this.remove_computer();
            }
            if(to_add == "actuators"){
                this.remove_actuator();
            }
        }
    }

    public void remove_sensor(){
        // For now, just remove the last sensor
        int num_sensors = this.sensors.size();
        int num_computers = this.computers.size();

        for(int x = num_computers; x > 0; x--){
            this.sensor_to_computer.remove((num_sensors*x)-1);
        }
        this.sensors.remove(this.sensors.size()-1);
    }

    public void remove_computer(){
        // For now, remove the last computer

        int num_computers = this.computers.size();
        int num_sensors = this.sensors.size();
        int num_actuators = this.actuators.size();

        for(int x = 0; x < num_sensors; x++){
            this.sensor_to_computer.remove(this.sensor_to_computer.size()-1);
        }

        for(int x = num_actuators; x > 0; x--){
            this.computer_to_actuator.remove((num_computers*x)-1);
        }

        this.computers.remove(this.computers.size()-1);
    }

    public void remove_actuator(){

        int num_computers = this.computers.size();
        for(int x = 0; x < num_computers; x++){
            this.computer_to_actuator.remove(this.computer_to_actuator.size()-1);
        }
        this.actuators.remove(this.actuators.size()-1);
    }








    public void change_component(){
        int rand_num = this.rand.nextInt(3);

        // SENSOR
        if(rand_num == 0){
            int rand_idx = this.rand.nextInt(this.sensors.size());
            this.sensors.set(rand_idx, this.get_random_component_type(this.design_space.sensor_types));
        }
        // COMPUTER
        else if(rand_num == 1){
            int rand_idx = this.rand.nextInt(this.computers.size());
            this.computers.set(rand_idx, this.get_random_component_type(this.design_space.computer_types));
        }
        // ACTUATOR
        else{
            int rand_idx = this.rand.nextInt(this.actuators.size());
            this.actuators.set(rand_idx, this.get_random_component_type(this.design_space.actuator_types));
        }
    }


    public boolean get_probability_result(double probability){
        return (this.rand.nextDouble() < probability);
    }



    public int get_random_bit(){
        if(this.rand.nextBoolean()){
            return 1;
        }
        return 0;
    }

    public JsonObject get_design_object(){
        JsonObject design_obj = new JsonObject();

        design_obj.addProperty("sensors", (new Gson().toJson(this.sensors)));
        design_obj.addProperty("computers", (new Gson().toJson(this.computers)));
        design_obj.addProperty("actuators", (new Gson().toJson(this.actuators)));
        design_obj.addProperty("sensors to computers", this.get_sensor_computer_string());
        design_obj.addProperty("computers to actuators", this.get_computer_actuator_string());

        return design_obj;
    }




}
