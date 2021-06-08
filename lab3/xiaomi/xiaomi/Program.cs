using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Sqo;

namespace xiaomi
{

    public class Driver
    {
        public int OID { get; set; }
        public string name { get; set; }
        public string lastName { get; set; }
        public decimal salary { get; set; }

        public override string ToString()
        {
            return "ID: " + OID + ", name " + name + ", last name: " + lastName + ", salary: " + salary.ToString();
        }


    }

    public class Routes
    {
        public int OID { get; set; }
        public List<string> busStops { get; set; }

        public override string ToString()
        {
            if(busStops == null) {
                return OID.ToString();

            }

            return "ID: " + OID + ", bus stops: " + String.Join(", ", busStops);
        }

    }

    class Program
    {
        private static void procesing(Siaqodb siaqodb)
        {
            Console.WriteLine("Enter value for rise");
            decimal rise = Convert.ToDecimal(Console.ReadLine());

            IObjectList<Driver> drivers = siaqodb.LoadAll<Driver>();

            foreach(Driver driver in drivers)
            {
                driver.salary = driver.salary + rise;
                siaqodb.StoreObject(driver);
            }
        }
            private static void update(Siaqodb siaqodb)
        {
            Console.WriteLine("What u want to update:");
            Console.WriteLine("1 - driver, 2 - route, 3 - exit");
            int select;

            select = Convert.ToInt32(Console.ReadLine());

            switch (select)
            {
                case 1:
                    updateDriver(siaqodb);
                    break;
                case 2:
                    updateRoute(siaqodb);
                    break;
                case 3:
                    break;

            }
        }

        private static void updateRoute(Siaqodb siaqodb)
        {

            Console.WriteLine("Enter id of route");
            int id = Convert.ToInt32(Console.ReadLine());

            List<string> busStops = new List<string>();
            String stop;

            var route = siaqodb.LoadObjectByOID<Routes>(id);

            if (route != null)
            {
                while (true)
                {
                    Console.WriteLine("Enter bus stop name");
                    stop = Console.ReadLine();
                    if (stop.Length == 0)
                    {
                        break;
                    }
                    busStops.Add(stop);
                }
                route.busStops = busStops;
                siaqodb.StoreObject(route);
            }
            else
            {
                Console.WriteLine("There is no route with this ID");
            }

        }

        private static void updateDriver(Siaqodb siaqodb)
        {
            Console.WriteLine("Enter id of driver");
            int id = Convert.ToInt32(Console.ReadLine());

            var driver = siaqodb.LoadObjectByOID<Driver>(id);

            if(driver != null)
            {
                Console.WriteLine("Enter name");
                driver.name = Console.ReadLine();
                Console.WriteLine("Enter surname");
                driver.lastName = Console.ReadLine();
                Console.WriteLine("Enter salary");
                driver.salary = Convert.ToDecimal(Console.ReadLine());
                siaqodb.StoreObject(driver);
            }
            else
            {
                Console.WriteLine("There is no driver with this ID");
            }


        }

        private static void delete(Siaqodb siaqodb)
        {
            Console.WriteLine("What you want to delete:");
            Console.WriteLine("1 - driver, 2 - route, 3 - exit");
            int select;

            select = Convert.ToInt32(Console.ReadLine());
            switch (select)
            {
                case 1:
                    deleteDriver(siaqodb);
                    break;
                case 2:
                    deleteRoute(siaqodb);
                    break;
                case 3:
                    break;
            }
        }

        private static void deleteRoute(Siaqodb siaqodb)
        {
            Console.WriteLine("Enter id of route to delete");

            int id = Convert.ToInt32(Console.ReadLine());

            var route = siaqodb.LoadObjectByOID<Routes>(id);
            if (route != null) { 
                siaqodb.Delete(route);
            }
            else
            {
                Console.WriteLine("There is no route with this ID");
            }

        }

        private static void deleteDriver(Siaqodb siaqodb)
        {
            Console.WriteLine("Enter id of driver to delete");

            int id = Convert.ToInt32(Console.ReadLine());
            var driver = siaqodb.LoadObjectByOID<Driver>(id);

            if (driver != null)
            {
                siaqodb.Delete(driver);
            }
            else
            {
                Console.WriteLine("There is no driver with this ID");
            }
        }
        private static void getRoute(Siaqodb siaqodb)
        {
            Console.WriteLine("What you want to do:");
            Console.WriteLine("1 - get all, 2 - get by Bus stop, 3 - exit");
            int select = Convert.ToInt32(Console.ReadLine());

            switch (select)
            {
                case 1:
                    getRoutesAll(siaqodb);
                    break;
                case 2:
                    getRoutesByBusStop(siaqodb);
                    break;
                case 3:
                    break;
            }
        }

        
private static void getRoutesByBusStop(Siaqodb siaqodb)
        {
            Console.WriteLine("Enter route");
            string busStop = Console.ReadLine();

            IObjectList<Routes> routes = siaqodb.LoadAll<Routes>();

            foreach (Routes route in routes)
            {
                if(route.busStops.Contains(busStop))
                Console.WriteLine(route.ToString());
            }
        }

        private static void getRoutesAll(Siaqodb siaqodb)
        {
            IObjectList<Routes> routes = siaqodb.LoadAll<Routes>();

            foreach (Routes route in routes)
            {
                Console.WriteLine(route.ToString());
            }

        }

        private static void getDriverBySurname(Siaqodb siaqodb)
        {
            Console.WriteLine("Enter last name");
            string surname = Console.ReadLine();

            var query = from Driver driver in siaqodb
                        where driver.lastName.Contains(surname)
                        select driver;
            foreach (Driver dri in query){
                Console.WriteLine(dri.ToString());
            }
        }
        private static void getDriverById(Siaqodb siaqodb)
        {
            Console.WriteLine("Enter id");
            int id = Convert.ToInt32(Console.ReadLine());

            var driver = siaqodb.LoadObjectByOID<Driver>(id);

            Console.WriteLine(driver.ToString());
        }

        private static void getDrivers(Siaqodb siaqodb)
        {
            Console.WriteLine("What you want to do:");
            Console.WriteLine("1 - get all, 2 - get by ID, 3 - get by surname, 4 - exit");
            int select = Convert.ToInt32(Console.ReadLine());

            switch (select)
            {
                case 1:
                    getAllDriver(siaqodb);
                    break;
                case 3:
                    getDriverBySurname(siaqodb);
                    break;
                case 2:
                    getDriverById(siaqodb);
                    break;
                case 4:
                    break;
            }
        }

        public static void getAllDriver(Siaqodb siaqodb)
        {
            IObjectList<Driver> drivers = siaqodb.LoadAll<Driver>();

            foreach (Driver driver in drivers) {
                Console.WriteLine(driver.ToString());
            }
        }

        private static void get(Siaqodb siaqodb)
        {
            Console.WriteLine("What u want to get:");
            Console.WriteLine("1 - driver, 2 - route, 3 - exit");
            int select;
            select = Convert.ToInt32(Console.ReadLine());
            switch (select)
            {
                case 1:
                    getDrivers(siaqodb);
                    break;
                case 2:
                    getRoute(siaqodb);
                    break;
                case 3:
                    break;
            }
        }

        public static void createDriver(Siaqodb siaqodb)
        {
            Driver driver = new Driver();

            Console.WriteLine("Enter name");
            driver.name = Console.ReadLine();
            Console.WriteLine("Enter surname");
            driver.lastName = Console.ReadLine();
            Console.WriteLine("Enter salary");
            driver.salary = Convert.ToDecimal(Console.ReadLine());
            siaqodb.StoreObject(driver);

        }
        public static void createRoute(Siaqodb siaqodb)
        {
            List<string> busStops = new List<string>();
            string bus;
            Routes route = new Routes();
            while (true)
            {
                Console.WriteLine("Enter bus stop name");
                bus = Console.ReadLine();
                if (bus.Length == 0)
                {
                    break;
                }
                busStops.Add(bus);
            }
            route.busStops = busStops;
            siaqodb.StoreObject(route);
        }

        private static void create(Siaqodb siaqodb)
        {
            Console.WriteLine("What you want to create");
            Console.WriteLine("1 - driver , 2 - route, 3 - exit");
            int select = Convert.ToInt32(Console.ReadLine());

            switch (select)
            {
                case 1:
                    createDriver(siaqodb);
                    break;
                case 2:
                    createRoute(siaqodb);
                    break;
                case 3:
                    break;
            }
        }
        static void Main(string[] args)
        {
            Siaqodb siaqodb = new Siaqodb("c:\\Siaqodb\\");
            int select;
            do {
                Console.WriteLine("Welcome to BusSystem");
                Console.WriteLine("Enter value to select menu");
                Console.WriteLine("1 - create , 2 - update, 3 - delete, 4 - get, 5 - processing, 6 - exit");
                select = Convert.ToInt32(Console.ReadLine());
                switch (select) {
                    case 1:
                        create(siaqodb);
                        break;
                    case 2:
                        update(siaqodb);
                        break;
                    case 3:
                        delete(siaqodb);
                        break;
                    case 4:
                        get(siaqodb);
                        break;
                    case 5:
                        procesing(siaqodb);
                        break;
                    case 6:
                        break;
                }

            } while (select != 6);
        }
    } 
}
