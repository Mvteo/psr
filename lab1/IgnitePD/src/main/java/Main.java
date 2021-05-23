import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.AddressResolver;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.processors.cache.LongOperationsDumpSettingsClosure;
import org.apache.ignite.internal.util.lang.IgnitePredicate2X;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;


import javax.cache.Cache;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;


public class Main {

    final private static Random r = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws Exception {

        IgniteConfiguration cfg = new IgniteConfiguration();

        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi().setLocalPort(8300);

        cfg.setDiscoverySpi(discoverySpi);
        Ignite ignite = Ignition.start(cfg);

        setUpCache(ignite);

        int select;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Welcome to CinemaManager");
            System.out.println("Enter value to select menu");
            System.out.println("1 - create , 2 - update, 3 - delete, 4 - get, 5 - processing, 6 - exit");

            select = scanner.nextInt();
            switch (select) {
                case 1:
                    create(ignite);
                    break;
                case 2:
                    update(ignite);
                    break;
                case 3:
                    delete(ignite);
                    break;
                case 4:
                    get(ignite);
                    break;
                case 5:
                    procesing(ignite);
                    break;
                case 6:
                    break;
            }

        } while (select != 6);

    }


    private static void procesing(Ignite ignite) throws Exception {
        System.out.println("What u want to processing:");
        System.out.println("1 - repertoire lower all prices, 2 - employees give everyone a raise by 5%, 3 - sum employees salary, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);
        select = scanner.nextInt();
        switch (select) {
            case 1:
                procesingRepertoire(ignite);
                break;
            case 2:
                procesingEmployees(ignite);
                break;
            case 3:
                procesingSalary(ignite);
                break;
            case 4:
                break;

        }
    }

    private static void procesingSalary(Ignite ignite) {
        System.out.println("Sums up the value of the salary from employees");

        IgniteCache<Long,Employee> employeeIgniteCache = ignite.cache("employeeIgniteCache");

        double sum = 0;

        for (Cache.Entry<Long, Employee> entry : employeeIgniteCache) {
            sum += entry.getValue().getSalary();
        }

        System.out.println("Sum of employees salary: " + sum);

    }

    private static void procesingEmployees(Ignite ignite) {
        ExecutorService executorService = ignite.executorService();

        executorService.submit(new IgniteRunnable() {
            @Override
            public void run() {
                IgniteCache<Long,Employee> employeeIgniteCache = ignite.cache("employeeIgniteCache");
                for(IgniteCache.Entry<Long,Employee> e : employeeIgniteCache){
                    e.getValue().setSalary(e.getValue().getSalary() * 1.05);
                    employeeIgniteCache.replace(e.getKey(),e.getValue());
                }
            }
        });

    }


    private static void procesingRepertoire(Ignite ignite) {
        IgniteCache<Long, Repertoire> repertoireIgniteCache = ignite.cache("repertoireIgniteCache");

        Set<Long> keySet = new HashSet<Long>();

        for(IgniteCache.Entry<Long, Repertoire> r : repertoireIgniteCache){
            keySet.add(r.getKey());
        }


        System.out.println(keySet);

        repertoireIgniteCache.invokeAll(keySet, new EntryProcessor<Long, Repertoire, Object>() {
            @Override
            public Object process(MutableEntry<Long, Repertoire> mutableEntry, Object... objects) throws EntryProcessorException {
                Repertoire repertoire = mutableEntry.getValue();
                Double price = repertoire.getPrice();

                price = price * 0.90;

                repertoire.setPrice(price);

                System.out.println("Processing = " + repertoire.toString());

                mutableEntry.setValue(repertoire);

                return null;
            }
        });


    }

    private static void update(Ignite ignite) throws ParseException {
        System.out.println("What u want to update:");
        System.out.println("1 - movie, 2 - repertoire, 3 - employees, 4 - exit");
        int select;

        Scanner scanner = new Scanner(System.in);

        select = scanner.nextInt();
        switch (select) {
            case 1:
                updateMovie(ignite);
                break;
            case 2:
                updateRepertoire(ignite);
                break;
            case 3:
                updateEmployee(ignite);
                break;

            case 4:
                break;
        }
    }

    private static void updateEmployee(Ignite ignite) {
        String name;
        String surname;
        String position;
        String salary;
        Scanner scanner = new Scanner(System.in);
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("What employee you want to upgrade:");
        Long employeeID = scanner.nextLong();

        IgniteCache<Long, Employee> employeeIgniteCache = ignite.cache("employeeIgniteCache");

        Employee employeeNew = new Employee();
        Employee employeeOld = employeeIgniteCache.get(employeeID);

        if(!employeeIgniteCache.containsKey(employeeID)){
            System.out.println("There is no employee with this ID");
        }
        else {
            scanner2.nextLine();
            System.out.println("Enter new name");
            name = scanner2.nextLine();

            if (name.length() > 0) {
                employeeNew.setName(name);
            } else {
                employeeNew.setName(employeeOld.getName());
            }

            System.out.println("Enter new surname");
            surname = scanner2.nextLine();

            if (surname.length() > 0) {
                employeeNew.setSurname(surname);
            } else {
                employeeNew.setSurname(employeeOld.getSurname());
            }

            System.out.println("Enter new position");
            position = scanner2.nextLine();

            if (position.length() > 0) {
                employeeNew.setPosition(position);
            } else {
                employeeNew.setPosition(employeeOld.getPosition());
            }

            System.out.println("Enter new salary");
            salary = scanner2.nextLine();

            if (salary.length() > 0) {
                double newsal = Double.parseDouble(salary);
                employeeNew.setSalary(newsal);
            } else {
                employeeNew.setSalary(employeeOld.getSalary());
            }

            employeeIgniteCache.replace(employeeID, employeeNew);
        }
    }

    private static void updateRepertoire(Ignite ignite) throws ParseException {
        String hall;
        Date dateTime;
        String dateTimeString;
        String movieTitle;
        String price;

        Boolean flag = false;

        Scanner scanner = new Scanner(System.in);
        System.out.println("What repertoire you want to upgrade:");

        Long repertoireID = scanner.nextLong();

        IgniteCache<Long, Movie> movieIgniteCache = ignite.cache("movieIgniteCache");
        IgniteCache<Long, Repertoire> repertoireIgniteCache = ignite.cache("repertoireIgniteCache");


        Repertoire oldRepertoire = repertoireIgniteCache.get(repertoireID);
        Repertoire newRepertoire = new Repertoire();

        if (!repertoireIgniteCache.containsKey(repertoireID)) {
            System.out.println("There is no reportoire with this ID");
        } else {
            scanner.nextLine();
            System.out.println("Enter new Hall");
            hall = scanner.nextLine();

            if (hall.length() > 0) {
                newRepertoire.setHall(hall);
            } else {
                newRepertoire.setHall(oldRepertoire.getHall());
            }

            System.out.println("Enter new date");
            dateTimeString = scanner.nextLine();

            if (dateTimeString.length() > 0) {
                dateTime = new SimpleDateFormat("dd-MM HH:mm").parse(dateTimeString);
                newRepertoire.setDataTime(dateTime);
            } else {
                newRepertoire.setDataTime(oldRepertoire.getDataTime());
            }

            while (true) {
                System.out.println("Enter movie title");

                movieTitle = scanner.nextLine();

                if (movieTitle.length() > 0) {
                    for (IgniteCache.Entry<Long, Movie> e : movieIgniteCache) {

                        if (e.getValue().getTitle().equals(movieTitle)) {

                            flag = true;
                        }
                    }
                    if (flag) {
                        break;
                    } else {
                        System.out.println("This movie did not exist.");
                    }
                } else {
                    break;
                }

            }

            if (movieTitle.length() > 0) {
                newRepertoire.setMovieTitle(movieTitle);
            } else {
                newRepertoire.setMovieTitle(oldRepertoire.getMovieTitle());
            }


            System.out.println("Enter new price");
            price = scanner.nextLine();

            if (price.length() > 0) {
                double newPrice = Double.parseDouble(price);
                newRepertoire.setPrice(newPrice);
            } else {
                newRepertoire.setPrice(oldRepertoire.getPrice());
            }

            repertoireIgniteCache.replace(repertoireID,newRepertoire);
        }
    }

    private static void updateMovie(Ignite ignite) {
        String newTitle;
        String newDirector;
        String newDescription;
        String newDuration;
        Scanner scanner = new Scanner(System.in);
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("What movie you want to upgrade:");
        Long movieID = scanner.nextLong();

        IgniteCache<Long, Movie> movieIgniteCache = ignite.cache("movieIgniteCache");

        Movie oldMovie = movieIgniteCache.get(movieID);
        Movie newMovie = movieIgniteCache.get(movieID);

        if (!movieIgniteCache.containsKey(movieID)) {
            System.out.println("There is no movie with this ID");
        } else {
            scanner2.nextLine();
            System.out.println("Enter new Title");
            newTitle = scanner2.nextLine();

            if (newTitle.length() > 0) {
                newMovie.setTitle(newTitle);
            } else {
                newMovie.setTitle(oldMovie.getTitle());
            }

            System.out.println("Enter new Director");
            newDirector = scanner2.nextLine();

            if (newDirector.length() > 0) {
                newMovie.setDirector(newDirector);
            } else {
                newMovie.setDirector(oldMovie.getDirector());
            }

            System.out.println("Enter new description");
            newDescription = scanner2.nextLine();

            if (newDescription.length() > 0) {
                newMovie.setDescription(newDescription);
            } else {
                newMovie.setDescription(oldMovie.getDescription());
            }

            System.out.println("Enter new duration");
            newDuration = scanner2.nextLine();

            if (newDuration.length() > 0) {
                double duration = Double.parseDouble(newDuration.trim());
                newMovie.setDuration(duration);
            } else {
                newMovie.setDuration(oldMovie.getDuration());
            }

            movieIgniteCache.replace(movieID, newMovie);
        }
    }

    private static void delete(Ignite ignite) {
        System.out.println("What you want to delete:");
        System.out.println("1 - movie, 2 - repertoire, 3 - employees, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);
        select = scanner.nextInt();
        switch (select) {
            case 1:
                deleteMovie(ignite);
                break;
            case 2:
                deleteRepertoire(ignite);
                break;
            case 3:
                deleteEmployee(ignite);
                break;
            case 4:
                break;
        }
    }

    private static void deleteEmployee(Ignite ignite) {
        IgniteCache<Long,Employee> employeeIgniteCache = ignite.cache("employeeIgniteCache");
        System.out.println("What employee you want to delete:");
        Scanner scanner = new Scanner(System.in);
        Long employeeID = scanner.nextLong();

        employeeIgniteCache.remove(employeeID);
    }

    private static void deleteRepertoire(Ignite ignite) {
        IgniteCache<Long, Repertoire> repertoireIgniteCache = ignite.cache("repertoireIgniteCache");
        System.out.println("What repertoire you want to delete:");
        Scanner scanner = new Scanner(System.in);
        Long repertoireID = scanner.nextLong();

        repertoireIgniteCache.remove(repertoireID);
    }

    private static void deleteMovie(Ignite ignite) {
        IgniteCache<Long, Movie> movieIgniteCache = ignite.cache("movieIgniteCache");
        System.out.println("What movie you want to delete:");
        Scanner scanner = new Scanner(System.in);
        Long movieID = scanner.nextLong();

        movieIgniteCache.remove(movieID);
    }

    private static void setUpCache(Ignite ignite) {
        IgniteCache<Long, Movie> cache = ignite.createCache("movieIgniteCache");
        IgniteCache<Long,Employee> employeeIgniteCache = ignite.createCache("employeeIgniteCache");
        IgniteCache<Long,Repertoire> repertoireIgniteCache = ignite.createCache("repertoireIgniteCache");
    }

    private static void get(Ignite ignite) {
        System.out.println("What u want to get:");
        System.out.println("1 - movie, 2 - repertoire, 3 - employees, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);
        select = scanner.nextInt();
        switch (select) {
            case 1:
                getMovie(ignite);
                break;
            case 2:
                getRepertoire(ignite);
                break;
            case 3:
                getEmployee(ignite);
                break;
            case 4:
                break;
        }
    }

    private static void getEmployee(Ignite ignite) {
        System.out.println("What you want to do:");
        System.out.println("1 - get all, 2 - get by ID, 3 - exit:");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();

        switch (select){
            case 1:
                getEmployeeAll(ignite);
                break;
            case 2:
                getEmployeeByID(ignite);
                break;
            case 3:
                break;
        }
    }

    private static void getEmployeeByID(Ignite ignite) {
        IgniteCache<Long, Employee> employeeIgniteCache = ignite.cache("employeeIgniteCache");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter ID");
        Long employeeID = scanner.nextLong();

        if(employeeIgniteCache.containsKey(employeeID)){
            System.out.println(employeeIgniteCache.get(employeeID).toString());
        }else {
            System.out.println("There is no employee with this ID");
        }
    }

    private static void getEmployeeAll(Ignite ignite) {
        IgniteCache<Long, Employee> employeeIgniteCache = ignite.cache("employeeIgniteCache");

        for(IgniteCache.Entry<Long, Employee> e : employeeIgniteCache){
            System.out.println("ID: " + e.getKey() + " value: "+ e.getValue().toString());
        }
    }

    private static void getRepertoire(Ignite ignite) {

        System.out.println("What you want to do:");
        System.out.println("1 - get all, 2 - get by ID, 3 - get with price less then, 4 - exit");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();

        switch (select){
            case 1:
                getRepertoireAll(ignite);
                break;
            case 2:
                getRepertoireByID(ignite);
                break;
            case 3:
                getRepertoireLess(ignite);
                break;
            case 4:
                break;
        }
    }

    private static void getRepertoireLess(Ignite ignite) {

        IgniteCache<Long,Repertoire> repertoireIgniteCache = ignite.cache("repertoireIgniteCache");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter price to display repositories with a price lower than the given price");
        double price = scanner.nextDouble();

        IgniteBiPredicate<Long, Repertoire> filter = (key,r) -> r.getPrice() < price;

        //Predicate<?,?> pricePredicate = Predicates.equal("director", movieDirector);

        try (QueryCursor<Cache.Entry<Long,Repertoire>> qryCursor = repertoireIgniteCache.query(new ScanQuery<>(filter))){
            qryCursor.forEach(
                    entry -> System.out.println("Key => " + entry.getKey()+ "value => " + entry.getValue().toString())
            );
        }

        scanner.nextLine();
    }

    private static void getRepertoireByID(Ignite ignite) {

        //Map<Long,Movie> movies = client.getMap("movies");
        IgniteCache<Long,Repertoire> repertoireIgniteCache = ignite.cache("repertoireIgniteCache");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter ID");
        Long repertoireID = scanner.nextLong();

        if(repertoireIgniteCache.containsKey(repertoireID)){
            System.out.println(repertoireIgniteCache.get(repertoireID).toString());
        }else {
            System.out.println("There is no repertoire with this ID");
        }

    }

    private static void getRepertoireAll(Ignite ignite) {
        IgniteCache<Long, Repertoire> repertoireIgniteCache = ignite.cache("repertoireIgniteCache");

        for(IgniteCache.Entry<Long,Repertoire> e : repertoireIgniteCache){
            System.out.println("ID: " + e.getKey() + "value: " + e.getValue().toString());
        }
    }

    private static void getMovie(Ignite ignite) {

        System.out.println("What you want to do:");
        System.out.println("1 - get all, 2 - get by ID, 3 - get by director, 4 - exit");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();

        switch (select){
            case 1:
                getMovieAll(ignite);
                break;
            case 2:
                getMovieByID(ignite);
                break;
            case 3:
                getMovieByDirector(ignite);
                break;
            case 4:
                break;
        }
    }

    private static void getMovieByDirector(Ignite ignite) {
       // IMap<Long,Movie> movies = client.getMap("movies");

        IgniteCache<Long, Movie> movieIgniteCache = ignite.cache("movieIgniteCache");



        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Director");
        String movieDirector = scanner.nextLine();

        IgniteBiPredicate<Long, Movie> filter = (key,m) -> m.getDirector().equals(movieDirector);

        //Predicate<?,?> pricePredicate = Predicates.equal("director", movieDirector);

        try (QueryCursor<Cache.Entry<Long,Movie>> qryCursor = movieIgniteCache.query(new ScanQuery<>(filter))){
            qryCursor.forEach(
                    entry -> System.out.println("Key => " + entry.getKey()+ "value => " + entry.getValue().toString())
            );
        }

        scanner.nextLine();
    }

    private static void getMovieByID(Ignite ignite) {
        //Map<Long,Movie> movies = client.getMap("movies");
        IgniteCache<Long,Movie> movieIgniteCache = ignite.cache("movieIgniteCache");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter ID");
        Long movieID = scanner.nextLong();

        if(movieIgniteCache.containsKey(movieID)){
            System.out.println(movieIgniteCache.get(movieID).toString());
        }else {
            System.out.println("There is no movie with this ID");
        }

    }

    private static void getMovieAll(Ignite ignite) {
        IgniteCache<Long, Movie> movieIgniteCache = ignite.cache("movieIgniteCache");


        for(IgniteCache.Entry<Long,Movie> e : movieIgniteCache){
            System.out.println("ID: " + e.getKey() + "value: " + e.getValue().toString());
        }
    }

    //************************************************************************************************************CREATE

    private static void create(Ignite ignite) throws ParseException {
        System.out.println("What you want to create");
        System.out.println("1 - movie , 2 - repertoire, 3 - employee, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);

        select = scanner.nextInt();
        switch (select) {
            case 1:
                createMovie(ignite);
                break;
            case 2:
                createRepertoire(ignite);
                break;
            case 3:
                createEmployee(ignite);
                break;
            case 4:
                break;
        }
    }

    private static void createEmployee(Ignite ignite) {
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

       // Map<Long,Employee> employees = client.getMap("employees");
        IgniteCache<Long, Employee> employeeIgniteCache = ignite.cache("employeeIgniteCache");
        Long key = (long) Math.abs(r.nextInt());
        employeeIgniteCache.put(key,employee);
        scanner.nextLine();
    }

    private static void createRepertoire(Ignite ignite) throws ParseException {
        String hall;
        Date dateTime;
        String dateTimeString;
        String movieTitle;
        Double price;

        Boolean flag = false;


       // Map<Long, Movie> movies = client.getMap("movies");
       // Map<Long, Repertoire> repertoires = client.getMap("repertoires");

        IgniteCache<Long, Movie> movieIgniteCache = ignite.cache("movieIgniteCache");
        IgniteCache<Long,Repertoire> repertoireIgniteCache = ignite.cache("repertoireIgniteCache");

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

            for(IgniteCache.Entry<Long, Movie> e : movieIgniteCache){
                if(e.getValue().getTitle().equals(movieTitle)){
                    flag = true;
                }
            }
            if(flag){
                break;
            }
            else{
                System.out.println("This movie did not exist.");
            }

//            for(Map.Entry<Long, Movie> e : movies.entrySet()){
//                if(e.getValue().getTitle().equals(movieTitle)){
//                    flag = true;
//                }
//            }
//            if(flag){
//                break;
//            }
//            else {
//                System.out.println("This movie did not exist.");
//            }

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
        repertoireIgniteCache.put(key,repertoire);
        scanner.nextLine();
    }

    private static void createMovie(Ignite ignite) {
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

        IgniteCache<Long, Movie> movieIgniteCache = ignite.cache("movieIgniteCache");
        Long key1 = (long) Math.abs(r.nextInt());
        System.out.println(key1);
        movieIgniteCache.put(key1,movie);
        scanner.nextLine();
    }


}
