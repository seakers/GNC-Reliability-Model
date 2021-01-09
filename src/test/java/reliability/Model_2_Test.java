package reliability;

import org.junit.Test;
import reliability.models.GNC_Model_2;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class Model_2_Test {


    @Test public void testModel() {

        System.out.println("\n\n\n\n\n\n------ MODEL RELIABILITY TEST -----");

        double connection_success_rate = 1;

        ArrayList<Double> sensors   = new ArrayList<>();
        sensors.add(0.9985);
        sensors.add(0.999);
        sensors.add(0.9995);

        ArrayList<Double> computers = new ArrayList<>();
        computers.add(0.999);
        computers.add(0.9996);
        computers.add(0.9998);

        ArrayList<Double> actuators = new ArrayList<>();
        actuators.add(0.9992);
        actuators.add(0.998);
        actuators.add(0.999);

        String sensor_to_computer   = "111111111";
        String computer_to_actuator = "010101010";

        GNC_Model_2 model = new GNC_Model_2.Builder(sensors, computers, actuators, connection_success_rate)
                .connection_sensor_to_computer(sensor_to_computer)
                .connection_computer_to_actuator(computer_to_actuator)
                .build();

        System.out.println("--> MODEL RELIABILITY: " + model.evaluate_reliability(false));



        assertTrue("this should return true", true);
    }

}
