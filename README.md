## Title
Car Rental Business Simulator - Project 3 of OOAD

## Team Members
1. Jitendra Marndi
2. Madhusudhan Aithal Mahabhaleshwara
3. Raj Chandak

## Project Description
Aim here is to simulate a Car Rental Business. The rental store has certain number of cars spread across different categories available for renting. The customer can customize the cars with options such as addition of child car seats etc. before they rent. There are three types of customer, each customer type has its own limits on the number of cars and number of days the customer can rent the car.

## Build Instructions
Project is completely written in Java as per the problem requirement. The source code has been organized as per [Maven Standard](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html). This repository has been provided with a pom.xml file which the configuration file required by [Maven](https://maven.apache.org/) to compile the code. Building, testing and running the project is very easy with Maven:
1. First clone this repository to your local development environment. If you have access to a terminal, type in below command to get a local copy of the repository:
`git clone git@github.com:MasterJEET/car-rental.git`
2. If you are using an IDE such IntelliJ, load this project into it. The IDE should automatically pick up the pom.xml configuration file and create its own configurations. After this you should be able to build and test the project the usual way it's done in the IDE. Note: this works only if your IDE supports Maven integration.
3. Alternatively, if you are on a terminal and just cloned the repository, make sure you have Maven installed. In CentOS, or any other Linux based OS with yum tool in it, one could to
`sudo yum install maven`
to install Maven. Then, go to project directory and execute
`mvn package`
to build the project.
A sample run of the simulation has been provided in the file 'car-rental-output.txt'.
## Implementation Details
Source code has been organized as per Maven Standard. All the project source files reside in the directory `src/main/java` and `MyUnitTest.java` for testing the simulator is kept in `src/test/java` directory.
The class `CarRental` acts as the interface between the store and the customer. When new rental requests need to be made, just call the `addNewRental(Transaction)` method of`'CarRental` class and when car needs to be returned, just call `completeRental(Transaction)`. The `CarRental` class delegates the task to `Recorder` which has an instance of `BusinessRule`. The `Recorder` in turn delegates all the validation task to `BusinessRule`. The `BusinessRule` verifies the data coherency and validates the request against the limits set on customer types and car availability. If all is good, the `Recorder` stores the transaction in its internal maps.
We've used four design patterns in this project, each has been briefly described below.
Check the `docs` to know more about the classes involved.
### Factory Method Pattern
We've used Factory Method Pattern to create concrete `Car`s. The abstract class `Store` has the parameterized abstract method `getNewCar(Car.Type carType)` which takes in the required type of the `Car` as an argument. This method is implemented by `CarRental` which extends `Car`. Argument is checked in a switch case statement and appropriate concrete `Car` is created and returned.
### Decorator Pattern
Decorator pattern has been used to add variable number of options to the `Car`. The `Car` has the abstract method `getRentalCost(Integer numOfDays)`. The abstract class  `CarOption`  extends the `Car`  and has an instance variable of type `Car`. The concrete car options class such as `ChildSeat` override the `getRentalCost` method appropriately to get correct cost. The concrete `Car` classes also override `getRentalCost`. `Car`s are decorated inside a method `decorateCar()` of class `CarRental` with specified number of each options (child seats, GPS modules and radio packages).
### Observer Pattern
This pattern has been used to create report at the end of each day. The `Observable` and the `Observer` interface has been created instead of using the inbuilt ones provided by Java. The `Store` implements the `Observable` and the `Summarizer` implements the `Observer` interface. Whenever there's a need to create a report, the `Store` calls `update(Object)` on `Summarizer`. The `Summarizer` then generates the report.
### Singleton Pattern
In addition to above three, the Singleton Pattern has also been implemented. The `UniqueIDGenerator` class has the responsibility of generating unique strings which can be used as identifier for all sorts of objects. Specifically it has been used to get transaction ID, customer ID, license plate number, report ID.

## Test Cases
`MyUnitTest.java` class for unit test has been provided which contain 11 simple test methods. If your IDE supports Maven integration then you should be able to run your tests from the IDE itself. If you're using a terminal, in the project directory just type
`mvn test`
to verify all is well.  Sample output of tests done using Maven has also been provided in the file 'maven-test.txt'.
