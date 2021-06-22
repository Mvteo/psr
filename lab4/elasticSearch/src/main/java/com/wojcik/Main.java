package com.wojcik;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Random;
import java.util.Scanner;

public class Main {


    private static Random random = new Random(System.currentTimeMillis());
    private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final int PORT_TWO = 9201;
    private static final String SCHEME = "http";

    private static RestHighLevelClient client;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {

        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(HOST, PORT_ONE, HttpHost.DEFAULT_SCHEME_NAME))
        );


            int select;
            Scanner scanner = new Scanner(System.in);

            do {
                System.out.println("Welcome to CarService");
                System.out.println("Enter value to select menu");
                System.out.println("1 - create , 2 - update, 3 - delete, 4 - get, 5 - processing, 6 - exit");

                select = scanner.nextInt();
                switch (select) {
                    case 1:
                        create(client);
                        break;
                    case 2:
                        update(client);
                        break;
                    case 3:
                        delete(client);
                        break;
                    case 4:
                        get(client);
                        break;
                    case 5:
                        procesingEmployees(client);
                        break;
                    case 6:
                        break;
                }

            } while (select != 6);

        }

        @SneakyThrows
        private static void create(RestHighLevelClient client)  {
            ObjectMapper objectMapper = new ObjectMapper();
            WorkstationService workstationService = new WorkstationService(client,objectMapper);
            CarService carService = new CarService(client,objectMapper);

            Employee employee = new Employee(Math.abs(random.nextLong()),"Marcin","Kowalski",3000.00);
            Employee employee1 = new Employee(Math.abs(random.nextLong()),"Adrian","Kolczyk",4000.00);
            Employee employee2 = new Employee(Math.abs(random.nextLong()),"Gabriel","Wilk",3160.00);
            Employee employee3 = new Employee(Math.abs(random.nextLong()),"Alan","Piotrowski",3230.00);
            Employee employee4 = new Employee(Math.abs(random.nextLong()),"Florian","Kołodziej",4100.00);
            Employee employee5 = new Employee(Math.abs(random.nextLong()),"Ignacy","Nawrocki",2800.00);
            Employee employee6 = new Employee(Math.abs(random.nextLong()),"Gabriel","Kurek",3160.00);
            Employee employee7 = new Employee(Math.abs(random.nextLong()),"Kacper","Pawłowski",3680.00);
            Employee employee8 = new Employee(Math.abs(random.nextLong()),"Hubert","Sikorski",5430.00);
            Employee employee9 = new Employee(Math.abs(random.nextLong()),"Janusz","Chrzanowski",3400.00);

            EmployeeService employeeService = new EmployeeService(client,objectMapper);

            Car car1 = new Car(Math.abs(random.nextLong()),"Toyota");
            Car car2 = new Car(Math.abs(random.nextLong()),"BMW");
            Car car3 = new Car(Math.abs(random.nextLong()),"Alfa Romeo");

            Workstation workstation = new Workstation(Math.abs(random.nextLong()),"stacja1");
            Workstation workstation1 = new Workstation(Math.abs(random.nextLong()),"stacja2");
            Workstation workstation2 = new Workstation(Math.abs(random.nextLong()),"stacja3");
            Workstation workstation3 = new Workstation(Math.abs(random.nextLong()),"stacja4");
            workstation.addWorker(employee);
            workstation.addWorker(employee1);

            workstation1.addWorker(employee1);
            workstation1.addWorker(employee2);
            workstation1.addWorker(employee3);
            workstation1.addWorker(employee4);

            workstation2.addWorker(employee5);
            workstation2.addWorker(employee6);
            workstation2.addWorker(employee8);

            workstation3.addWorker(employee7);
            workstation3.addWorker(employee8);
            workstation3.addWorker(employee9);

            workstationService.add(workstation, workstation.getId());
            workstationService.add(workstation1, workstation1.getId());
            workstationService.add(workstation2, workstation2.getId());
            workstationService.add(workstation3, workstation3.getId());

            car1.addWorkstation(workstation);
            car2.addWorkstation(workstation1);
            car3.addWorkstation(workstation2);

            carService.add(car1, car1.getId());
            carService.add(car2, car2.getId());
            carService.add(car3, car3.getId());

            employeeService.add(employee, employee.getId());
            employeeService.add(employee1, employee1.getId());
            employeeService.add(employee2, employee2.getId());
            employeeService.add(employee3, employee3.getId());
            employeeService.add(employee4, employee4.getId());
            employeeService.add(employee5, employee5.getId());
            employeeService.add(employee6, employee6.getId());
            employeeService.add(employee7, employee7.getId());
            employeeService.add(employee8, employee8.getId());
            employeeService.add(employee9, employee9.getId());

            for(Car c : carService.getAll()){
                System.out.println(c.toString());
            }

            for(Workstation w : workstationService.getAll()){
                System.out.println(w.toString());
            }

        }

        @SneakyThrows
        static void update(RestHighLevelClient client)   {
            System.out.println("Enter id of workstation:");
            ObjectMapper objectMapper = new ObjectMapper();
            String workstationID;
            String newName;
            Scanner scanner = new Scanner(System.in);

            workstationID = scanner.nextLine();

            System.out.println("Enter new name");

            newName = scanner.nextLine();


            WorkstationService workstationService = new WorkstationService(client,objectMapper);
            Workstation workstation =  workstationService.getById(Long.parseLong(workstationID)).get();

            workstation.setName(newName);

            System.out.println(workstation.getName());

             workstationService.add(workstation, workstation.getId());

        }



        @SneakyThrows
        static void delete(RestHighLevelClient client ){
            System.out.println("Enter id of car to delete:");
            ObjectMapper objectMapper = new ObjectMapper();
            Long id;
            Scanner scanner = new Scanner(System.in);
            id = scanner.nextLong();

            CarService carService = new CarService(client,objectMapper);

            carService.deleteById(id);

        }


        static void get(RestHighLevelClient client ){
            System.out.println("What u want to get:");
            System.out.println("1 - car, 2 - workstation, 3 - employees, 4 - exit");
            int select;
            Scanner scanner = new Scanner(System.in);
            select = scanner.nextInt();
            switch (select) {
                case 1:
                    getCar(client);
                    break;
                case 2:
                    getWorkStation(client);
                    break;
                case 3:
                    getEmployee(client);
                    break;
                case 4:
                    break;
            }

        }

        private static void getWorkStation(RestHighLevelClient client ) {

            System.out.println("What you want to do:");
            System.out.println("1 - get all, 2 - get by ID, 3 - get employee from station, 4 - exit");
            Scanner scanner = new Scanner(System.in);
            int select = scanner.nextInt();

            switch (select){
                case 1:
                    getWorkstationAll(client);
                    break;
                case 2:
                    getWorkstationById(client);
                    break;
                case 3:
                    getEmployeFromStation(client);
                    break;
                case 4:
                    break;
            }

        }

        @SneakyThrows
        private static void getWorkstationAll(RestHighLevelClient client ){
        ObjectMapper objectMapper = new ObjectMapper();
            WorkstationService workstationService = new WorkstationService(client,objectMapper);

            for(Workstation w : workstationService.getAll()){
                System.out.println(w.toString());
            }
        }

        @SneakyThrows
        private static void getWorkstationById(RestHighLevelClient client ){
            ObjectMapper objectMapper = new ObjectMapper();
            WorkstationService workstationService = new WorkstationService(client,objectMapper);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter ID");
            Long workstationID = scanner.nextLong();

            var work = workstationService.getById(workstationID).get();

            work.toString();
        }

        @SneakyThrows
        private static void getEmployeFromStation(RestHighLevelClient client ) {
            ObjectMapper objectMapper = new ObjectMapper();
            WorkstationService workstationService = new WorkstationService(client,objectMapper);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter name");
            String workstationID = scanner.nextLine();

            for(Workstation w : workstationService.getAll()){
                if(w.getName().equals(workstationID)) {
                    for (Employee e : w.getEmployees()) {
                        System.out.println(e.toString());
                    }
                }
            }
        }

        private static void getEmployee(RestHighLevelClient client ) {

            System.out.println("What you want to do:");
            System.out.println("1 - get all, 2 - get by ID, 3 - exit:");
            Scanner scanner = new Scanner(System.in);
            int select = scanner.nextInt();

            switch (select){
                case 1:
                    getEmployeeAll(client);
                    break;
                case 2:
                    getEmployeeByID(client);
                    break;
                case 3:
                    break;
            }

        }

        @SneakyThrows
        private static void getEmployeeAll(RestHighLevelClient client){
            ObjectMapper objectMapper = new ObjectMapper();
            EmployeeService employeeService = new EmployeeService(Main.client,objectMapper);

            for(Employee w : employeeService.getAll()){
                System.out.println(w.toString());
            }
        }

        @SneakyThrows
        private static void getEmployeeByID(RestHighLevelClient client){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter ID");
            Long employeeID = scanner.nextLong();

            EmployeeService employeeService = new EmployeeService(client,objectMapper);

            var work = employeeService.getById(employeeID).get();

            work.toString();
        }


        static private void getCar(RestHighLevelClient client ) {

            System.out.println("What you want to do:");
            System.out.println("1 - get all, 2 - get by ID, 3 - exit");
            Scanner scanner = new Scanner(System.in);
            int select = scanner.nextInt();

            switch (select){
                case 1:
                    getCarAll(client);
                    break;
                case 2:
                    getCarById(client);
                    break;
                case 3:
                    break;

            }

        }

        @SneakyThrows
        private static void getCarAll(RestHighLevelClient client){
            CarService carService = new CarService(client,objectMapper);

            for(Car c : carService.getAll()){
                System.out.println(c.toString());
            }
        }

        @SneakyThrows
        private static void getCarById(RestHighLevelClient client){
            CarService carService = new CarService(client,objectMapper);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter ID");
            Long id = scanner.nextLong();

            var car = carService.getById(id).get();

            car.toString();

        }


        @SneakyThrows
        private static void procesingEmployees(RestHighLevelClient client ) {
            System.out.println("Enter rise value");
            ObjectMapper objectMapper1 = new ObjectMapper();
            Scanner scanner = new Scanner(System.in);
            Double rise = scanner.nextDouble();
            EmployeeService service = new EmployeeService(client,objectMapper1);

            for(Employee e: service.getAll()){
                e.setSalary(e.getSalary() + rise);
                service.add(e, e.getId());
            }
        }

    }

