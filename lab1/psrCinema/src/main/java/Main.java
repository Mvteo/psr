import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.protocol.codec.CacheGetAllCodec;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;


import java.io.Serializable;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

public class Main {

    final private static Random r = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws UnknownHostException, ParseException {

        ClientConfig clientConfig = HConfig.getClientConfig();
        final HazelcastInstance client = HazelcastClient.newHazelcastClient( clientConfig );

        int select;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Welcome to CinemaManager");
            System.out.println("Enter value to select menu");
            System.out.println("1 - create , 2 - update, 3 - delete, 4 - get, 5 - processing, 6 - exit");

            select = scanner.nextInt();
            switch (select) {
                case 1:
                    create(client);
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
                    procesing(client);
                    break;
                case 6:
                    break;
            }

        } while (select != 6);

    }

    private static void create(HazelcastInstance client) throws ParseException {
        System.out.println("What you want to create");
        System.out.println("1 - movie , 2 - repertoire, 3 - employee, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);

        select = scanner.nextInt();
        switch (select) {
            case 1:
                createMovie(client);
            case 2:
                createRepertoire(client);
                break;
            case 3:
                createEmployee(client);
                break;
            case 4:
                break;
        }
    }


    private static void createRepertoire(HazelcastInstance client) throws ParseException {
        String hall;
        Date dateTime;
        String dateTimeString;
        String movieTitle;
        Double price;

        Boolean flag = false;


        Map<Long, Movie> movies = client.getMap("movies");
        Map<Long, Repertoire> repertoires = client.getMap("repertoires");

        Scanner scanner = new Scanner(System.in);

        scanner.nextLine();

        System.out.println("Enter hall");
        hall = scanner.nextLine();

        System.out.println("Enter date (dd-MM HH:mm)");
        dateTimeString = scanner.nextLine();
        dateTime = new SimpleDateFormat("dd-MM HH:mm").parse(dateTimeString);

        while(true){
            System.out.println("Enter movie title");
            movieTitle = scanner.nextLine();

            for(Map.Entry<Long, Movie> e : movies.entrySet()){
                if(e.getValue().getTitle().equals(movieTitle)){
                    flag = true;
                }
            }
            if(flag){
                break;
            }
            else {
                System.out.println("This movie did not exist.");
            }

        }

        System.out.println("Enter price");
        price = scanner.nextDouble();

        Repertoire repertoire = Repertoire.builder()
                .hall(hall)
                .dataTime(dateTime)
                .movieTitle(movieTitle)
                .price(price)
                .build();

        Long key = (long) Math.abs(r.nextInt());
        repertoires.put(key,repertoire);

    }

    private static void createEmployee(HazelcastInstance client) {
        String name;
        String surname;
        String position;
        double salary;

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Name");
        name = scanner.nextLine();

        System.out.println("Enter Surname");
        surname = scanner.nextLine();

        System.out.println("Enter Position");
        position = scanner.nextLine();

        System.out.println("Enter Salary");
        salary = scanner.nextDouble();

        Employee employee = Employee.builder()
                .name(name)
                .surname(surname)
                .position(position)
                .salary(salary)
                .build();

        Map<Long,Employee> employees = client.getMap("employees");
        Long key = (long) Math.abs(r.nextInt());
        employees.put(key,employee);
    }

    static void createMovie(HazelcastInstance client) {
        String title;
        String director;
        String description;
        double duration;

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Title");
        title = scanner.nextLine();

        System.out.println("Enter Director");
        director = scanner.nextLine();

        System.out.println("Enter description");
        description = scanner.nextLine();

        System.out.println("Enter duration");
        duration = scanner.nextDouble();

        Movie movie = Movie.builder()
                .title(title)
                .director(director)
                .description(description)
                .duration(duration)
                .build();

        Map<Long, Movie> movies = client.getMap("movies");
        Long key1 = (long) Math.abs(r.nextInt());
        movies.put(key1,movie);


    }

    static void update(HazelcastInstance client) throws ParseException {
        System.out.println("What u want to update:");
        System.out.println("1 - movie, 2 - repertoire, 3 - employees, 4 - exit");
        int select;

        Scanner scanner = new Scanner(System.in);

        select = scanner.nextInt();
        switch (select) {
            case 1:
                updateMovie(client);
                break;
            case 2:
                updateRepertoire(client);
                break;
            case 3:
                updateEmployee(client);
                break;

            case 4:
                break;
        }

    }

    private static void updateRepertoire(HazelcastInstance client) throws ParseException {
        String hall;
        Date dateTime;
        String dateTimeString;
        String movieTitle;
        String price;

        Boolean flag = false;

        Scanner scanner = new Scanner(System.in);
        System.out.println("What repertoire you want to upgrade:");

        Long repertoireID = scanner.nextLong();

        Map<Long,Movie> movies = client.getMap("movies");
        Map<Long,Repertoire> repertoires = client.getMap("repertoires");

        Repertoire oldRepertoire = repertoires.get(repertoireID);
        Repertoire newRepertoire = new Repertoire();

        if(!repertoires.containsKey(repertoireID)){
            System.out.println("There is no reportoire with this ID");
        }else {
            scanner.nextLine();
            System.out.println("Enter new Hall");
            hall = scanner.nextLine();

            if(hall.length() > 0){
                newRepertoire.setHall(hall);
            }else {
                newRepertoire.setHall(oldRepertoire.getHall());
            }

            System.out.println("Enter new date");
            dateTimeString = scanner.nextLine();

            if(dateTimeString.length() > 0){
                dateTime = new SimpleDateFormat("dd-MM HH:mm").parse(dateTimeString);
                newRepertoire.setDataTime(dateTime);
            }else {
                newRepertoire.setDataTime(oldRepertoire.getDataTime());
            }

            while (true){
                System.out.println("Enter movie title");

                movieTitle = scanner.nextLine();

                if(movieTitle.length() > 0){
                    for(Map.Entry<Long, Movie> e : movies.entrySet()){

                        if(e.getValue().getTitle().equals(movieTitle)){

                            flag = true;
                        }
                    }
                    if(flag){
                        break;
                    }
                    else {
                        System.out.println("This movie did not exist.");
                    }
                }else {
                    break;
                }

            }

            if(movieTitle.length() > 0){
                newRepertoire.setMovieTitle(movieTitle);
            }else {
                newRepertoire.setMovieTitle(oldRepertoire.getMovieTitle());
            }


            System.out.println("Enter new price");
            price = scanner.nextLine();

            if(price.length() > 0){
                double newPrice = Double.parseDouble(price);
                newRepertoire.setPrice(newPrice);
            }
            else {
                newRepertoire.setPrice(oldRepertoire.getPrice());
            }

            repertoires.put(repertoireID,newRepertoire);


        }

    }

    private static void updateEmployee(HazelcastInstance client) {
        String name;
        String surname;
        String position;
        String salary;
        Scanner scanner = new Scanner(System.in);
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("What employee you want to upgrade:");
        Long employeeID = scanner.nextLong();

        Map<Long, Employee> employees = client.getMap("employees");
        Employee employeeNew = new Employee();
        Employee employeeOld = employees.get(employeeID);

        if(!employees.containsKey(employeeID)){
            System.out.println("There is no employee with this ID");
        }
        else {
            scanner2.nextLine();
            System.out.println("Enter new name");
            name = scanner2.nextLine();

            if(name.length() > 0){
                employeeNew.setName(name);
            }
            else{
                employeeNew.setName(employeeOld.getName());
            }

            System.out.println("Enter new surname");
            surname = scanner2.nextLine();

            if(surname.length() > 0){
                employeeNew.setSurname(surname);
            }else {
                employeeNew.setSurname(employeeOld.getSurname());
            }

            System.out.println("Enter new position");
            position = scanner2.nextLine();

            if(position.length() > 0){
                employeeNew.setPosition(position);
            }else {
                employeeNew.setPosition(employeeOld.getPosition());
            }

            System.out.println("Enter new salary");
            salary = scanner2.nextLine();

            if(salary.length() > 0){
                double newsal = Double.parseDouble(salary);
                employeeNew.setSalary(newsal);
            }
            else {
                employeeNew.setSalary(employeeOld.getSalary());
            }

            employees.put(employeeID,employeeNew);

        }
    }

    static void updateMovie(HazelcastInstance client) {
        String newTitle;
        String newDirector;
        String newDescription;
        String newDuration;
        Scanner scanner = new Scanner(System.in);
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("What movie you want to upgrade:");
        Long movieID = scanner.nextLong();
        Map<Long, Movie> movies = client.getMap("movies");
        Movie oldMovie = movies.get(movieID);
        Movie newMovie = movies.get(movieID);

        if(!movies.containsKey(movieID)){
            System.out.println("There is no movie with this ID");
        }else {
            scanner2.nextLine();
            System.out.println("Enter new Title");
            newTitle = scanner2.nextLine();

            if (newTitle.length() > 0) {
                newMovie.setTitle(newTitle);
            }
            else
            {
                newMovie.setTitle(oldMovie.getTitle());
            }

            System.out.println("Enter new Director");
            newDirector = scanner2.nextLine();

            if (newDirector.length() > 0) {
                newMovie.setDirector(newDirector);
            }
            else{
                newMovie.setDirector(oldMovie.getDirector());
            }

            System.out.println("Enter new description");
            newDescription = scanner2.nextLine();

            if (newDescription.length() > 0) {
                newMovie.setDescription(newDescription);
            }else {
                newMovie.setDescription(oldMovie.getDescription());
            }

            System.out.println("Enter new duration");
            newDuration = scanner2.nextLine();

            if (newDuration.length() > 0) {
                double duration = Double.parseDouble(newDuration.trim());
                newMovie.setDuration(duration);
            }
            else {
                newMovie.setDuration(oldMovie.getDuration());
            }

            movies.put(movieID,newMovie);
        }
    }

    static void delete(HazelcastInstance client){
        System.out.println("What you want to delete:");
        System.out.println("1 - movie, 2 - repertoire, 3 - employees, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);
        select = scanner.nextInt();
        switch (select) {
            case 1:
                deleteMovie(client);
                break;
            case 2:
                deleteRepertoire(client);
                break;
            case 3:
                deleteEmployee(client);
                break;
            case 4:
                break;
        }

    }

    private static void deleteRepertoire(HazelcastInstance client) {
        long repertoireID;
        Map<Long,Repertoire> repertoires = client.getMap("repertoires");
        System.out.println("What repertoire you want to delete:");
        Scanner scanner = new Scanner(System.in);
        repertoireID = scanner.nextLong();

        repertoires.remove(repertoireID);

        System.out.println("You deleted employee with ID => "+ repertoireID);
    }

    private static void deleteEmployee(HazelcastInstance client) {
        long employeeID;
        Map<Long,Movie> employees = client.getMap("employees");
        System.out.println("What emplyee you want to delete:");
        Scanner scanner = new Scanner(System.in);
        employeeID = scanner.nextLong();

        employees.remove(employeeID);

        System.out.println("You deleted employee with ID => "+ employeeID);
    }

    static void deleteMovie(HazelcastInstance client) {
        long movieID;
        Map<Long,Movie> movies = client.getMap("movies");
        System.out.println("What movie you want to delete:");
        Scanner scanner = new Scanner(System.in);
        movieID = scanner.nextLong();

        movies.remove(movieID);

        System.out.println("You deleted movie with ID => "+ movieID);


    }

    static void get(HazelcastInstance client){
        System.out.println("What u want to get:");
        System.out.println("1 - movie, 2 - repertoire, 3 - employees, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);
        select = scanner.nextInt();
        switch (select) {
            case 1:
                getMovie(client);
                break;
            case 2:
                getRepertoire(client);
                break;
            case 3:
                getEmployee(client);
                break;
            case 4:
                break;
        }

    }

    private static void getRepertoire(HazelcastInstance client) {

        System.out.println("What you want to do:");
        System.out.println("1 - get all, 2 - get by ID, 3 - get with price less then, 4 - exit");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();

        switch (select){
            case 1:
                getRepertoireAll(client);
                break;
            case 2:
                getRepertoireByID(client);
                break;
            case 3:
                getRepertoireLess(client);
                break;
            case 4:
                break;
        }

    }


    private static void getRepertoireAll(HazelcastInstance client){
        Map<Long,Repertoire> repertoires = client.getMap("repertoires");
        System.out.println("All repertoires: ");
        for(Map.Entry<Long, Repertoire> e : repertoires.entrySet()){
            System.out.println(e.getKey() + " => " + e.getValue());
        }
    }

    private static void getRepertoireByID(HazelcastInstance client){
        Map<Long,Repertoire> repertoires = client.getMap("repertoires");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter ID");
        Long repertoireID = scanner.nextLong();

        if(repertoires.containsKey(repertoireID)){
            System.out.println(repertoires.get(repertoireID).toString());
        }
        else {
            System.out.println("There is no repertoire with this ID");
        }

    }

    private static void getRepertoireLess(HazelcastInstance client){
        IMap<Long,Repertoire> repertoires = client.getMap("repertoires");
        System.out.println("Enter price: ");
        Scanner scanner = new Scanner(System.in);
        Double price = scanner.nextDouble();

        Predicate<?,?> pricePredicate = Predicates.lessThan("price", price);

        Collection<Repertoire> repertoires1 = repertoires.values(Predicates.and(pricePredicate));
        for(Repertoire r : repertoires1){
            System.out.println(r.toString());
        }
    }


    private static void getEmployee(HazelcastInstance client) {

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

    private static void getEmployeeAll(HazelcastInstance client){
        Map<Long,Movie> employees = client.getMap("employees");
        System.out.println("All emplyees: ");
        for(Map.Entry<Long, Movie> e : employees.entrySet()){
            System.out.println(e.getKey() + " => " + e.getValue());
        }
    }

    private static void getEmployeeByID(HazelcastInstance client){
        Map<Long,Employee> employees = client.getMap("employees");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter ID");
        Long employeeID = scanner.nextLong();

        if(employees.containsKey(employeeID)){
            System.out.println(employees.get(employeeID).toString());
        }
        else {
            System.out.println("There is no employee with this ID");
        }

    }


    static private void getMovie(HazelcastInstance client) {

        System.out.println("What you want to do:");
        System.out.println("1 - get all, 2 - get by ID, 3 - get by director, 4 - exit");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();

        switch (select){
            case 1:
                getMovieAll(client);
                break;
            case 2:
                getMovieByID(client);
                break;
            case 3:
                getMovieByDirector(client);
                break;
            case 4:
                break;
        }

    }

    private static void getMovieAll(HazelcastInstance client){
        Map<Long,Movie> movies = client.getMap("movies");
        System.out.println("All movies: ");
        for(Map.Entry<Long, Movie> e : movies.entrySet()){
            System.out.println(e.getKey() + " => " + e.getValue());
        }
    }

    private static void getMovieByID(HazelcastInstance client){
        Map<Long,Movie> movies = client.getMap("movies");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter ID");
        Long movieID = scanner.nextLong();

        if(movies.containsKey(movieID)){
            System.out.println(movies.get(movieID).toString());
        }
        else {
            System.out.println("There is no movie with this ID");
        }

    }

    private static void getMovieByDirector(HazelcastInstance client){
        IMap<Long,Movie> movies = client.getMap("movies");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Director");
        String movieDirector = scanner.nextLine();

        Predicate<?,?> pricePredicate = Predicates.equal("director", movieDirector);

        Collection<Movie> repertoires1 = movies.values(Predicates.and(pricePredicate));
        for(Movie m : repertoires1){
            System.out.println(m.toString());
        }
    }

    static void procesing(HazelcastInstance client){
        System.out.println("What u want to processing:");
        System.out.println("1 - repertoire lower all prices, 2 - employees give everyone a raise by 5%, 3 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);
        select = scanner.nextInt();
        switch (select) {
            case 1:
                procesingRepertoire(client);
                break;
            case 2:
                procesingEmployees(client);
                break;
            case 3:
                break;

        }

    }

    private static void procesingEmployees(HazelcastInstance client) {
        IMap<Long, Employee> employees = client.getMap("employees");
        employees.executeOnEntries(new HEntryProcessor());

        for(Map.Entry<Long,Employee> e : employees.entrySet()){
            System.out.println(e.getKey() + "=> "+ e.getValue());
        }
    }

    static class HEntryProcessor implements EntryProcessor<Long, Employee, Double>, Serializable{

        @Override
        public Double process(Map.Entry<Long, Employee> entry) {
            Employee employee = entry.getValue();
            Double salary = employee.getSalary();

            salary = salary *1.05;

            employee.setSalary(salary);

            System.out.println("Procesing = " + employee);
            entry.setValue(employee);
            return null;
        }
    }

    static private void procesingRepertoire(HazelcastInstance client) {

        IExecutorService executorService = client.getExecutorService("exec");
        executorService.submitToAllMembers(new HCallable());

    }

    static class HCallable implements Callable<Void>, Serializable, HazelcastInstanceAware{

        private static final long serialVersionUID = 1L;
        private transient HazelcastInstance instance;

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.instance = hazelcastInstance;
        }

        @Override
        public Void call() throws Exception {
            IMap<Long, Repertoire> repertoires = instance.getMap("repertoires");
            for(Map.Entry<Long,Repertoire> r : repertoires.entrySet()){
                r.getValue().setPrice(r.getValue().getPrice()*0.95);
                repertoires.put(r.getKey(),r.getValue());
            }

            System.out.println("All repertoires: ");
            for(Map.Entry<Long, Repertoire> e : repertoires.entrySet()){
                System.out.println(e.getKey() + " => " + e.getValue());
            }
            return null;
        }
    }


}
