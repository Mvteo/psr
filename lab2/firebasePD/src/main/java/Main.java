import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

public class Main {

    final private static Random r = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        FileInputStream serviceAccount =
                new FileInputStream("C:\\Users\\mateo\\Desktop\\PSR\\psr_lab2\\key.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://unisystem-b056d-default-rtdb.europe-west1.firebasedatabase.app")
                .build();

        FirebaseApp.initializeApp(options);

        var databaseReference = FirebaseDatabase.getInstance().getReference();
        int select;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Welcome to UniSystem");
            System.out.println("Enter value to select menu");
            System.out.println("1 - create , 2 - update, 3 - delete, 4 - get, 5 - processing, 6 - exit");

            select = scanner.nextInt();
            switch (select) {
                case 1:
                    create(databaseReference);
                    break;
                case 2:
                    update(databaseReference);
                    break;
                case 3:
                    delete(databaseReference);
                    break;
                case 4:
                    get(databaseReference);
                    break;
                case 5:
                    procesing(databaseReference);
                    break;
                case 6:
                    break;
            }

        } while (select != 6);
    }

    @SneakyThrows
    private static void procesing(DatabaseReference dbRef) {
        Scanner scanner = new Scanner(System.in);
        var students = dbRef.child("students");
        System.out.println("Enter the student ID to calculate its average");
        String id = scanner.nextLine();
        var singleValue = new SingleValue<>(Student.class);

        students.child(id).addListenerForSingleValueEvent(singleValue);




        if ( singleValue.getValue().isPresent() ) {
            int counter = 0;
            double sum = 0;

            for (Map.Entry<String,Double> e :
                    singleValue.getValue().get().getGrade().entrySet()) {
                sum += e.getValue();
                counter++;
            }

            System.out.println("Avg = " + sum/counter);

        }
        else {
            System.out.println("There is no subject with this ID");
        }    }

    private static void update(DatabaseReference dbRef) {
        System.out.println("What u want to update:");
        System.out.println("1 - subject, 2 - student, 3 - lecturer, 4 - exit");
        int select;

        Scanner scanner = new Scanner(System.in);

        select = scanner.nextInt();
        switch (select) {
            case 1:
                updateSubject(dbRef);
                break;
            case 2:
                updateStudent(dbRef);
                break;
            case 3:
                updateLecturer(dbRef);
                break;
            case 4:
                break;
        }
    }

    @SneakyThrows
    private static void updateLecturer(DatabaseReference dbRef) {
        //MongoCollection<Document> lecturer = db.getCollection("lecturer");
        var lecturers = dbRef.child("lecturers");

        var listner = new SingleValue<>(Lecturer.class);

        var lecturerOld = listner.getValue();
        Scanner scanner = new Scanner(System.in);
        ObjectMapper objectMapper = new ObjectMapper();
        String subject;
        ArrayList<String> subjects = new ArrayList<>();

        Lecturer lecturerNew = new Lecturer();

        String _id;
        System.out.println("Enter id of lecturer");
        _id = scanner.nextLine();

       // Document byId = lecturer.find(eq("_id",_id)).first();

        if (lecturerOld.isPresent()) {

            System.out.println("Enter new name");
            lecturerNew.setName(scanner.nextLine());

            System.out.println("Enter new surname");
            lecturerNew.setSurname(scanner.nextLine());

            while (true){
                System.out.println("Enter new subject");
                subject = scanner.nextLine();
                if(subject.isBlank()){
                    break;
                }
                subjects.add(subject);
            }
            lecturerNew.setSubjects(subjects);

            System.out.println("Enter salary");
            lecturerNew.setSalary(scanner.nextDouble());
            lecturerNew.set_id(_id);
           // Document doc = Document.parse(objectMapper.writeValueAsString(lecturerNew));
          //  lecturer.updateOne(eq("_id",_id),new Document("$set",doc));
            lecturers.child(_id.toString()).setValueAsync(lecturerNew).get();

        }
        else {
            System.out.println("There is no lecturer with this ID");
        }

    }

    @SneakyThrows
    private static void updateStudent(DatabaseReference dbRef) {

        var students = dbRef.child("students");
        Scanner scanner = new Scanner(System.in);
        ObjectMapper objectMapper = new ObjectMapper();
        String subjectName;
        String _id;
        Map<String,Double> newGrade = new HashMap<>();

        var listner = new SingleValue<>(Student.class);
        Student studentNew = new Student();

        System.out.println("Enter id of student");
        _id = scanner.nextLine();
students.child(_id.toString()).addListenerForSingleValueEvent(listner);
var studentOld = listner.getValue();
        if(studentOld.isPresent()) {
            System.out.println("Enter new name");
            studentNew.setName(scanner.nextLine());

            System.out.println("Enter new surname");
            studentNew.setSurname(scanner.nextLine());


            while (true) {
                System.out.println("Enter new subject");
                subjectName = scanner.nextLine();
                if (subjectName.isBlank()) {
                    break;
                }
                System.out.println("Enter grade");
                Double grade = Double.parseDouble(scanner.nextLine());
                newGrade.put(subjectName, grade);
            }
            studentNew.setGrade(newGrade);
            studentNew.set_id(_id);

            students.child(_id.toString()).setValueAsync(studentNew);
        }else {
            System.out.println("There is no student with this ID");
        }
    }

    @SneakyThrows
    private static void updateSubject(DatabaseReference dbRef) {
        String _id;
        ObjectMapper objectMapper = new ObjectMapper();
     //   MongoCollection<Document> subject = db.getCollection("subject");
        var subjects = dbRef.child("subjects");
        Scanner scanner = new Scanner(System.in);
        Subject subjectNew = new Subject();
        System.out.println("Enter id of subject to update");
        _id = scanner.nextLine();

        var listner = new SingleValue<>(Subject.class);
        subjects.child(_id.toString()).addListenerForSingleValueEvent(listner);

        var subjectOld = listner.getValue();

        if(subjectOld.isPresent()) {

            System.out.println("Enter name");
            subjectNew.setName(scanner.nextLine());

            System.out.println("Enter ETCS");
            subjectNew.setETCS(scanner.nextInt());
            subjectNew.set_id(_id);

            subjects.child(_id.toString()).setValueAsync(subjectNew).get();
        }
        else {
            System.out.println("There is no subject with this ID");
        }

    }

    private static void delete(DatabaseReference dbRef) throws ExecutionException, InterruptedException {
        System.out.println("What you want to delete:");
        System.out.println("1 - subject, 2 - student, 3 - lecturer, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);
        select = scanner.nextInt();
        switch (select) {
            case 1:
                deleteSubject(dbRef);
                break;
            case 2:
                deleteStudent(dbRef);
                break;
            case 3:
                deleteLecturer(dbRef);
                break;
            case 4:
                break;
        }
    }

    private static void deleteLecturer(DatabaseReference dbRef) {
        var lecturers = dbRef.child("lecturers");

        System.out.println("Enter id to delete lecturer");
        Scanner scanner = new Scanner(System.in);
        String id = scanner.nextLine();

        var listner = new SingleValue<>(Lecturer.class);

        lecturers.child(id).addListenerForSingleValueEvent(listner);
        if(listner.getValue().isPresent()){
            System.out.println(listner.getValue());
            lecturers.child(id).removeValueAsync();
        }else {
            System.out.println("There is no lecturer with this ID");
        }
    }

    private static void deleteStudent(DatabaseReference dbRef) {

        var students = dbRef.child("students");

        System.out.println("Enter id to delete student");
        Scanner scanner = new Scanner(System.in);
        String id = scanner.nextLine();

        var listner = new SingleValue<>(Student.class);

        students.child(id).addListenerForSingleValueEvent(listner);
        if(listner.getValue().isPresent()){
            System.out.println(listner.getValue());
            students.child(id).removeValueAsync();
        }else {
            System.out.println("There is no student with this ID");
        }
    }

    private static void deleteSubject(DatabaseReference dbRef) throws ExecutionException, InterruptedException {
        var subjects = dbRef.child("subjects");

        System.out.println("Enter id to delete subject");
        Scanner scanner = new Scanner(System.in);
        String id = scanner.nextLine();

        var listner = new SingleValue<>(Subject.class);

        subjects.child(id).addListenerForSingleValueEvent(listner);
        if(listner.getValue().isPresent()){
            System.out.println(listner.getValue());
            subjects.child(id).removeValueAsync();
        }else {
            System.out.println("There is no subjet with this ID");
        }
    }

    private static void get(DatabaseReference dbRef) {
        System.out.println("What u want to get:");
        System.out.println("1 - subject by id, 2 - student, 3 - lecturer, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);
        select = scanner.nextInt();
        switch (select) {
            case 1:
                getSubjectByID(dbRef);
                break;
            case 2:
                getStudent(dbRef);
                break;
            case 3:
                getLecturer(dbRef);
                break;
            case 4:
                break;
        }
    }

    private static void getLecturer(DatabaseReference dbRef) {
        System.out.println("What you want to do:");
        System.out.println("1 - get all, 2 - get by ID,3 - get by subject ,4 - exit:");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();

        switch (select){
            case 1:
                getLecturerAll(dbRef);
                break;
            case 2:
                getLecturerByID(dbRef);
                break;
            case 3:
                getLecturerBySubject(dbRef);
                break;
            case 4:
                break;
        }
    }

    private static void getLecturerBySubject(DatabaseReference dbRef) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter subject");
        String subject = scanner.nextLine();

        var lecturers = dbRef.child("lecturers");
        var valueList = new ListOfValues<>(Lecturer.class);

        lecturers.addListenerForSingleValueEvent(valueList);

        var lecturerList = valueList.getValue();

        valueList.getValue().stream()
                .filter(lecturer -> lecturer.getSubjects().contains(subject)
                        ).forEach(System.out::println);
    }

    private static boolean getLecturerByID(DatabaseReference dbRef) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter id");
        String id = scanner.nextLine();

        var lecturers = dbRef.child("lecturers");
        var singleValue = new SingleValue<>(Lecturer.class);

        lecturers.child(id).addListenerForSingleValueEvent(singleValue);

        if(singleValue.getValue().isPresent()){
            System.out.println(singleValue.getValue().toString());
            return true;
        }

            System.out.println("There is no lecturer with this ID");
            return false;

    }

    private static void getLecturerAll(DatabaseReference dbRef) {
        var lecturers = dbRef.child("lecturers");


        var listner = new ListOfValues<>(Lecturer.class);
        lecturers.addListenerForSingleValueEvent(listner);

        listner.getValue().forEach(System.out::println);
    }

    private static void getStudent(DatabaseReference dbRef) {
        System.out.println("What you want to do:");
        System.out.println("1 - get all, 2 - get by ID, 3 - get by surname, 4 - exit");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();

        switch (select){
            case 1:
                getStudentAll(dbRef);
                break;
            case 2:
                getStudentByID(dbRef);
                break;
            case 3:
                getStudentBySurname(dbRef);
                break;
            case 4:
                break;
        }
    }

    private static void getStudentBySurname(DatabaseReference dbRef) {
       // MongoCollection<Document> student = db.getCollection("student");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter surname");
        String surname = scanner.nextLine();

        var students = dbRef.child("students");
        var singleValue = new SingleValue<>(Student.class);

        students.addListenerForSingleValueEvent(singleValue);

        singleValue.getValue().stream().filter(student -> student.getSurname().equals(surname)).forEach(System.out::println);
    }

    @SneakyThrows
    private static boolean getStudentByID(DatabaseReference dbRef) {
      //  MongoCollection<Document> student = db.getCollection("student");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter id");
        String id = scanner.nextLine();

        var students = dbRef.child("students");
        var singleValue = new SingleValue<>(Student.class);

        students.child(id).addListenerForSingleValueEvent(singleValue);

        if(singleValue.getValue().isPresent()){
            System.out.println(singleValue.getValue().toString());
            return true;
        }else{
            System.out.println("There is no student with this ID");
        return false;
        }
    }

    private static void getStudentAll(DatabaseReference dbRef) {

        var students = dbRef.child("students");

        var listOfValues = new ListOfValues<>(Student.class);
        students.addListenerForSingleValueEvent(listOfValues);

        listOfValues.getValue().stream().forEach(System.out::println);
    }

    private static boolean getSubjectByID(DatabaseReference dbRef) {
        var subjects = dbRef.child("subjects");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter id");

        String id = scanner.nextLine();

        var singleValue = new SingleValue<>(Subject.class);

        subjects.child(id).addListenerForSingleValueEvent(singleValue);
        if(singleValue.getValue().isPresent()){
            System.out.println(singleValue.getValue().toString());
           return true;
        }else {
            System.out.println("There is no subjet with this ID");
            return false;
        }
    }

    private static void getSubjectAll(DatabaseReference dbRef) {
//        MongoCollection<Document> subject = db.getCollection("subject");
//
//        for(Document doc : subject.find())
//            System.out.println("subject: " + doc.toJson());
    }

    private static void create(DatabaseReference dbRef) {
        System.out.println("What you want to create");
        System.out.println("1 - subject , 2 - student, 3 - lecturer, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);

        select = scanner.nextInt();
        switch (select) {
            case 1:
                createSubject(dbRef);
                break;
            case 2:
                createStudent(dbRef);
                break;
            case 3:
                createLecturer(dbRef);
                break;
            case 4:
                break;
        }
    }

    @SneakyThrows
    private static void createLecturer(DatabaseReference dbRef) {
      //  MongoCollection<Document> lecturer = db.getCollection("lecturer");
        var lecturers = dbRef.child("lecturers");
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> subjects = new ArrayList<>();
        String subject;

        Long newId = (long) Math.abs(r.nextInt());
        Scanner scanner = new Scanner(System.in);
        Lecturer lecturerNew = new Lecturer();

        System.out.println("Enter name");
        lecturerNew.setName(scanner.nextLine());

        System.out.println("Enter surname");
        lecturerNew.setSurname(scanner.nextLine());

        while (true){
            System.out.println("Enter subject");
            subject = scanner.nextLine();
            if(subject.isBlank()){
                break;
            }
            subjects.add(subject);
        }
        lecturerNew.setSubjects(subjects);

        System.out.println("Enter salary");
        lecturerNew.setSalary(scanner.nextDouble());
        lecturerNew.set_id(newId.toString());

        lecturers.child(lecturerNew.get_id().toString())
                .setValueAsync(lecturerNew)
                .get();
       // lecturer.insertOne(Document.parse(objectMapper.writeValueAsString(lecturerNew)));
    }

    @SneakyThrows
    private static void createStudent(DatabaseReference dbRef) {
       // MongoCollection<Document> student = db.getCollection("student");

        var students = dbRef.child("students");
        ObjectMapper objectMapper = new ObjectMapper();
        String subject;
        Map<String,Double> newGrade = new HashMap<>();
        Long newId = (long) Math.abs(r.nextInt());
        Scanner scanner = new Scanner(System.in);
        Student studentNew = new Student();

        System.out.println("Enter name");
        studentNew.setName(scanner.nextLine());

        System.out.println("Enter surname");
        studentNew.setSurname(scanner.nextLine());



        while (true){
            System.out.println("Enter subject name");
            subject = scanner.nextLine();
            if(subject.isBlank()) {
                break;
            }
            System.out.println("Enter grade");
            Double grade = Double.parseDouble(scanner.nextLine());
            newGrade.put(subject,grade);
        }
        studentNew.setGrade(newGrade);


        studentNew.set_id(newId.toString());

        students.child(studentNew.get_id().toString())
                .setValueAsync(studentNew)
                .get();
       // student.insertOne(Document.parse(objectMapper.writeValueAsString(studentNew)));
    }

    @SneakyThrows
    private static void createSubject(DatabaseReference dbRef) {
        //MongoCollection<Document> subject = db.getCollection("subject");
        var subjects = dbRef.child("subjects");
        ObjectMapper objectMapper = new ObjectMapper();

        Long newId = (long) Math.abs(r.nextInt());

        Scanner scanner = new Scanner(System.in);
        Subject subjectNew = new Subject();

        System.out.println("Enter name");
        subjectNew.setName(scanner.nextLine());

        System.out.println("Enter ETCS");
        subjectNew.setETCS(scanner.nextInt());

        subjectNew.set_id(newId.toString());

        subjects.child(subjectNew.get_id().toString())
                .setValueAsync(subjectNew)
                .get();

       // subject.insertOne(Document.parse(objectMapper.writeValueAsString(subjectNew)));
    }
}

class SingleValue<T> implements ValueEventListener {
    private final Class<T> type;
    private final Semaphore mutex;
    private T obj;

    @SneakyThrows
    public SingleValue(Class<T> type) {
        this.type = type;
        mutex = new Semaphore(1);
        mutex.acquire();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        try {
            obj = dataSnapshot.getValue(type);
        } finally {
            mutex.release();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        try {
            System.out.println("An error has occurred" + databaseError.getMessage());
            System.out.println(databaseError.getDetails());
        } finally {
            mutex.release();
        }
    }

    @SneakyThrows
    Optional<T> getValue() {
        mutex.acquire();
        var ret = Optional.ofNullable(obj);
        mutex.release();
        return ret;
    }

}
class ListOfValues<T> implements ValueEventListener {
    private final Class<T> type;
    private final Semaphore mutex;
    private final List<T> list;

    @SneakyThrows
    public ListOfValues(Class<T> type) {
        this.type = type;
        list = new ArrayList<>();
        mutex = new Semaphore(1);
        mutex.acquire();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        try {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                list.add(child.getValue(type));
            }
        } finally {
            mutex.release();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        try {
            System.out.println("An error has occurred" + databaseError.getMessage());
            System.out.println(databaseError.getDetails());
        } finally {
            mutex.release();
        }
    }

    @SneakyThrows
    List<T> getValue() {
        mutex.acquire();
        var retList = this.list;
        mutex.release();
        return retList;
    }
}
