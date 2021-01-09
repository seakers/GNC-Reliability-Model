# Guidance Navigation and Control Model

Java library capable of evaluating the mass and reliability of a guidance navigation and control system given a topology

## Commands
`./gradlew javadoc` - build javadocs

`./gradlew jar` - build jar

`./gradlew test` - run unit tests


## Models

This library contains two different models, each representing a guidance navigation and control system.


#### GNC_Model_1

This model represents sensors and computers interconnected in a certain topology.
It can calculate the mass and reliability of a system given the appropriate parameters. 

An example use of this model can be seen in unit test `Model_1_Test`


#### GNC_Model_2

This model represents sensors, computers, and actuators all interconnected in a certain topology. 
It can calculate the mass and reliability of a system given the appropriate parameters. 

An example use of this model can be seen in unit test `Model_2_Test`
