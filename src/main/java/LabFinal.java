import static java.lang.System.out;
import java.util.*;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import java.lang.reflect.*;
import static com.mongodb.client.model.Filters.*;
interface Alertable {
    void sendEmergencyAlert(CityZone location);
}
interface Reportable {
    String generateUsageReport();
}
abstract class CityResource implements Reportable {
    protected static int generateID = 0;
    protected int resourceID;
    protected CityZone location;
    protected String status;
    protected String name;
    private static ArrayList<CityZone> cityZones;
    static {
        cityZones = new ArrayList<CityZone>();
    }
    protected CityResource() {
        this.resourceID = 0;
        this.location = null;
        this.status = "";
    }
    public CityResource(String name, CityZone location, String status) {
        this.resourceID = ++generateID;
        this.name = name;
        this.location = location;
        this.status = status;
    }

    public int getResourceID() { return resourceID; }
    public void setResourceID(int resourceID) { this.resourceID = resourceID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CityZone getLocation()  { return location; }
    public void setLocation(CityZone location) { this.location = location; }
    public String getStatus()    { return status; }
    public void setStatus(String status) { this.status = status; }
    public static void addCityZones(CityZone city) { cityZones.add(city); }
    public static ArrayList<CityZone> getCityZones() { return cityZones; }
    abstract double calculateMaintenanceCost(double rate);
    @Override
        public String toString() {
            return "CityResource{" +
                "resourceID=" + resourceID +
                ", location='" + location.getName() + '\'' +
                ", status='" + status + '\'' +
                '}';
        }
    @Override public String generateUsageReport(){ return "Cities: " + cityZones.size(); }
}

class CityRepository<T> {
    private MongoClient mongoClient;
    private MongoDatabase db;
    public CityRepository(String client, String database) {
        try {
            mongoClient = MongoClients.create(client);
            db = mongoClient.getDatabase(database);
            System.out.println("Connected to database: " + db.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
   public static <T> Document toDocument(T object) {
        Document document = new Document();
        Class<?> className = object.getClass();  
        for (Field field : className.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                document.append(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return document;
    }

    public Consumer signup(String logType, String name, String email, String password) {
        Consumer consumer = new Consumer(name, email, password);
        if (logType.intern() == "admin") {
            out.println("New Admin Login, email: " + email);
            try {
                MongoCollection collection = db.getCollection("admin");
                out.println("Obj:" + collection);
                collection.insertOne(toDocument(consumer));
                return consumer;
            } catch (Exception e) {
                out.println("Failed because: " + e);
            }
        } else {
            out.println("New User Login, email: " + email);
            try {
                MongoCollection collection = db.getCollection("consumer");
                collection.insertOne(toDocument(consumer));
                out.println("Obj:" + collection);
                return consumer;
            } catch (Exception e) {
                out.println("Failed because: " + e);
            }
        }
        return null;
    }

    public Consumer signin(String logType, String email, String password) {
        Consumer consumer = Consumer.findConsumer(email, password);
        if (logType.intern() == "admin") {
            out.println("New Admin Login, email: " + email);
            try {
                MongoCollection collection =  db.getCollection("admin");
                Document user = (Document) collection.find(
                and(
                    eq("email", email),
                    eq("password", password)
                )
            ).first();

            if (user != null) {
                out.println("Login successful! Welcome, " + user.getString("name"));
                return consumer != null? consumer: new Consumer(user.getString("name"), email, password);
            } else {
                out.println("Invalid email or password.");
            }
            } catch (Exception e) {
                out.println("Failed because: " + e);
            }
        } else {
            out.println("New User Login, email: " + email);
            try {
                MongoCollection collection = db.getCollection("consumer");
                Document user = (Document) collection.find(
                and(
                    eq("email", email),
                    eq("password", password)
                )
            ).first(); 
            if (user != null) {
                out.println("Login successful! Welcome, " + user.getString("name"));
                return consumer != null? consumer: new Consumer(user.getString("name"), email, password);
            } else {
                out.println("Invalid email or password.");
            }
            } catch (Exception e) {
                out.println("Failed because: " + e);
            }
        }
        return null;
    }
    public <T extends CityResource> void createResource(String name, CityZone location, String status,  Class<T> resourceClass) {
            try {
                T resource = resourceClass
                        .getDeclaredConstructor(String.class, CityZone.class, String.class)
                        .newInstance(name, location, status);
                System.out.println("Created: " + resource);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    @Override 
    public String toString() {
        return "CityRepository{" +
            "mongoClient=" + mongoClient +
            ", db=" + (db != null ? db.getName() : "null") +
            '}';
    }
}

class TransportUnit extends CityResource {
    protected volatile double totalDistanceCovered;
    private Thread travel;
    protected ArrayList<Consumer> passengers;
    protected TransportUnit() {
        super();
        passengers = new ArrayList<Consumer>();
        setTravel(0);
    }
    public TransportUnit(String name, CityZone location, String status){
        super(name, location, status);
        passengers = new ArrayList<Consumer>();
        setTravel(0);
    }

    public double getDistanceCovered(){ return totalDistanceCovered; }
    public void startJourney() { travel.start(); }
    public void endJourney() { travel.stop(); }
    public void setTravel(double distance) {
        travel = new Thread(() -> {
            for (int i = 0; i < distance; i++) {
                totalDistanceCovered += 50; //kmph
                try { Thread.sleep(1000); } catch (InterruptedException e) {
                    out.println("Ran out of fuel");
                }
            }
        });
    }
    @Override
    double calculateMaintenanceCost(double mileage){ return totalDistanceCovered*256*mileage; }
    @Override
    public String toString() {
        return super.toString() +
            ", TransportUnit{" +
            "totalDistanceCovered=" + totalDistanceCovered +
            "Total passegners: " + passengers.size() +
            '}';
    }
    @Override public String generateUsageReport(){ return "Total passengers: " + passengers.size(); }
}

class Bus extends TransportUnit{
    protected Bus() { super(); }
    public Bus(String name, CityZone location, String status) {
        super(name, location, status);
        ((TransportationHub) location.getTransportHub()).addBus(this);
    }
    @Override
    public String toString() {
        return super.toString() +
            ", Bus{}";
    }
    @Override public String generateUsageReport(){ return ""; }
}

class Train extends TransportUnit{
    protected Train() { super(); }
    public Train(String name, CityZone location, String status) {
        super(name, location, status);
        ((TransportationHub) location.getTransportHub()).addTrain(this);

    }
    @Override
    public String toString() {
        return super.toString() +
            ", Train{}";
    }
    @Override public String generateUsageReport(){ return ""; }
}

class PowerStation extends CityResource implements Alertable {
    protected PowerStation() { super(); }
    public PowerStation(String name, CityZone location, String status) {
        super(name, location, status);
        location.getEnergyHub().addResource(this);
    }
    public void outage(SmartGrid grid, PowerStation powerStation) { 
        grid.outage();
        sendEmergencyAlert(powerStation.getLocation());
    }
    @Override public void sendEmergencyAlert(CityZone location){ 
        for (EmergencyService emergencyService: location.getEmergencyHub().getResources()) {
            if(emergencyService.getLocation() == location) emergencyService.sendEmergencyAlert(location);
        }
    }
    @Override double calculateMaintenanceCost(double tariffs){ return tariffs*SmartGrid.getEnergyConsumed(); }
    @Override
    public String toString() {
        return super.toString() +
            ", PowerStation{}";
    }
    @Override public String generateUsageReport(){ return ""; }
}

class EmergencyService extends CityResource implements Alertable {
    private static double emergencyResponseTime; 
    private static int responseCount;
    static {
        emergencyResponseTime = 0;
        responseCount = 0;
    }
    protected EmergencyService() {
        super();
    }

    public EmergencyService(String name, CityZone location, String status) {
        super(name, location, status);
    }

    public static double getEmergencyResponseTime() {
        return emergencyResponseTime;
    }

    public static void setEmergencyResponseTime(double time) {
        emergencyResponseTime = time;
    }

    @Override
    double calculateMaintenanceCost(double rate) {
        return 0.0;
    }
    public void dispatch(CityZone location) {
        double responseTime = 3 + Math.random() * 7;
        emergencyResponseTime = ((emergencyResponseTime * responseCount) + responseTime) / (responseCount + 1);
        responseCount++;
        System.out.println(this.getClass().getSimpleName()+" dispatched to "+location.getName()+". Arrived in "+responseTime+" minutes.");
    }

    @Override
    public void sendEmergencyAlert(CityZone location) {
        for (EmergencyService unit : location.getEmergencyHub().getResources()) {
            unit.dispatch(location);
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                ", EmergencyService{}";
    }

    @Override
    public String generateUsageReport() {
        return "Average Emergency Response Time: "+emergencyResponseTime+" minutes";
    }
}

class Police extends EmergencyService{
    protected Police() { super(); }
    public Police(String name, CityZone location, String status) {
        super(name, location, status);
        ((EmergencyHub) location.getEmergencyHub()).addPolice(this);

    }
    @Override
    public String toString() {
        return super.toString() +
            ", Police{}";
    }
    @Override public String generateUsageReport(){ return ""; }
}

class FireFighter extends EmergencyService{
    protected FireFighter() { super(); }
    public FireFighter(String name, CityZone location, String status) {
        super(name, location, status);
        ((EmergencyHub) location.getEmergencyHub()).addFireFighter(this);
    }
    @Override
    public String toString() {
        return super.toString() +
            ", Firefighter{}";
    }
    @Override public String generateUsageReport(){ return ""; }
}
class CityZone implements Reportable {
    private String name;
    private TransportationHub transportHub;
    private EnergyHub energyHub;
    private EmergencyHub emergencyHub;

    protected CityZone() {}

    public CityZone(String name) {
        this.name = name;
        this.transportHub  = new TransportationHub();
        this.energyHub     = new EnergyHub();
        this.emergencyHub  = new EmergencyHub();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TransportationHub getTransportHub() {
        return transportHub;
    }
    public EnergyHub getEnergyHub() {
        return energyHub;
    }
    public EmergencyHub getEmergencyHub() {
        return emergencyHub;
    }

    

    public double getDistance(CityZone city2) {
        int sum1 = name.chars().sum();
        int sum2 = city2.name.chars().sum();
        return Math.abs(sum1 - sum2) * 1.37;
    }

    @Override
    public String generateUsageReport() {
        return "Zone: " + name + "\n" +
               "--- Transport ---\n"  + transportHub.generateUsageReport()  +
               "--- Energy ---\n"     + energyHub.generateUsageReport()     +
               "--- Emergency ---\n"  + emergencyHub.generateUsageReport();
    }

    @Override
    public String toString() {
        return "CityZone{" +
               "zoneName='" + name + '\'' +
               ", transportHub=" + transportHub +
               ", energyHub="    + energyHub    +
               ", emergencyHub=" + emergencyHub +
               '}';
    }
}

class ResourceHub<T extends CityResource> implements Reportable{
    protected ArrayList<T> resources;

    public ResourceHub() {
        this.resources = new ArrayList<>();
    }

    public void addResource(T resource) {
        resources.add(resource);
    }

    public void removeResource(T resource) {
        resources.remove(resource);
    }

    public ArrayList<T> getResources() {
        return resources;
    }
     @Override
    public String toString() {
        return "ResourceHub with " + resources.size() + " resources.";
    }
    @Override
    public String generateUsageReport() {
        String report ="";
        for (T resource : resources) {
            report+= resource.toString() +"\n";
        }
        return report;
    }
}
class EnergyHub extends ResourceHub<PowerStation> {
    public EnergyHub() {
        super();
    }

    public void addPowerStation(PowerStation powerStation) {
        super.addResource(powerStation);
    }

    public ArrayList<PowerStation> getPowerStations() {
        return getResources();
    }
    @Override
    public String toString() {
        return "EnergyHub{" +
                "powerStations=" + getPowerStations() +
                '}';
    }

    @Override
    public String generateUsageReport() {
        return "Total power stations: " + getPowerStations().size();
    }
}

class EmergencyHub extends ResourceHub<EmergencyService> {
    public EmergencyHub() {
        super();
    }

    public void addPolice(Police police) {
        super.addResource(police);
    }

    public void addFireFighter(FireFighter fireFighter) {
        super.addResource(fireFighter);
    }

    public ArrayList<Police> getPoliceUnits() {
        ArrayList<Police> policeUnits = new ArrayList<>();
        for (EmergencyService unit : getResources()) {
            if (unit instanceof Police) policeUnits.add((Police) unit);
        }
        return policeUnits;
    }

    public ArrayList<FireFighter> getFireFighterUnits() {
        ArrayList<FireFighter> fireFighterUnits = new ArrayList<>();
        for (EmergencyService unit : getResources()) {
            if (unit instanceof FireFighter) fireFighterUnits.add((FireFighter) unit);
        }
        return fireFighterUnits;
    }

    @Override
    public String toString() {
        return "EmergencyHub{" +
                "policeUnits=" + getPoliceUnits() +
                ", fireFighterUnits=" + getFireFighterUnits() +
                '}';
    }

    @Override
    public String generateUsageReport() {
        return "Total police units: " + getPoliceUnits().size() +
                ", total firefighter units: " + getFireFighterUnits().size();
    }
}
class TransportationHub extends ResourceHub<TransportUnit> {
    public TransportationHub() {
        super();
    }

    public void addBus(Bus bus) {
        super.addResource(bus);
    }

    public void addTrain(Train train) {
        super.addResource(train);
    }

    public ArrayList<Bus> getBusses() {
        ArrayList<Bus> busses = new ArrayList<>();
        for (TransportUnit unit : getResources()) {
            if (unit instanceof Bus) busses.add((Bus) unit);
        }
        return busses;
    }

    public ArrayList<Train> getTrains() {
        ArrayList<Train> trains = new ArrayList<>();
        for (TransportUnit unit : getResources()) {
            if (unit instanceof Train) trains.add((Train) unit);
        }
        return trains;
    }

    @Override
    public String toString() {
        return "TransportationHub{" +
                "busses=" + getBusses() +
                ", trains=" + getTrains() +
                '}';
    }

    @Override
    public String generateUsageReport() {
        return "Total busses: " + getBusses().size() + ", total trains: " + getTrains().size();
    }
}


class SmartGrid implements Reportable{
    protected ArrayList<PowerStation> powerStations;
    protected ArrayList<Consumer> consumers;
    protected static volatile double totalEnergyConsumed = 0;
    private Thread energySupplier;
    public SmartGrid() {
        this.powerStations = new ArrayList<PowerStation>();
        this.consumers = new ArrayList<Consumer>();
        setEnergySupplier(0);
    }
    public SmartGrid(double rate, ArrayList<PowerStation>  powerStations, ArrayList<Consumer> consumers) {
        setEnergySupplier(rate);
        this.powerStations = powerStations;
        this.consumers = consumers;
    }
    public Thread getEnergySupplier(){ return energySupplier; }
    private void setEnergySupplier(double rate) {
        energySupplier = new Thread(() -> {
            for (int i = 0; i < 200; i++) {
                totalEnergyConsumed += rate;
                try { Thread.sleep(1000); } catch (InterruptedException e) {
                    out.println("Outage");
                }
            }
        });
    }
    public void supplyEnergy(Consumer consumer, PowerStation powerStation) { 
        if (consumer instanceof Industry) setEnergySupplier(10);
        else setEnergySupplier(5);
        if (!powerStations.contains(powerStation)) powerStations.add(powerStation);
        if(!consumers.contains(consumer)) consumers.add(consumer);
        if(!energySupplier.isAlive()) energySupplier.start(); 
    }
    public void stopSupplyEnergy() { energySupplier.stop(); }
    public ArrayList<PowerStation>  getPowerStations() { return powerStations; }
    public void addPowerStation(PowerStation  powerStation) { powerStations.add(powerStation); }
    public ArrayList<Consumer> getConsumers() { return consumers; }
    public void addConsumer(Consumer consumer) { consumers.add(consumer); }
    public static double getEnergyConsumed() { return totalEnergyConsumed; }
    public static void setEnergyConsumed(double energy) { totalEnergyConsumed = energy; }
    public void outage() {
        energySupplier.stop();
        energySupplier.start();
    }
    @Override
    public String toString() {
        return "SmartGrid{" +
            "powerStations=" + powerStations +
            ", consumers=" + consumers +
            ", totalEnergyConsumed=" + totalEnergyConsumed +
            '}';
    }
    @Override public String generateUsageReport(){ return ""; }
}

class Consumer{
    protected int id;
    protected String name;
    protected String email;
    private String password;
    private static int generateID = 0;
    private static ArrayList<Consumer> consumers;
    static {
        consumers = new ArrayList<Consumer>();
    }
    public Consumer(){}
    public Consumer(String name, String email, String password) {
         this.id = ++generateID; this.name = name; this.email = email; this.password = password;
         consumers.add(this);
        }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public static Consumer findConsumer(String email, String password) {
        for (Consumer consumer : consumers) {
            if (consumer.email.intern() == email.intern() && consumer.password.intern() == password.intern()) return consumer;
        }
        return null;
    }
    @Override
    public String toString() {
        return "Consumer{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}

class Household extends Consumer{
    private ArrayList<Consumer> members;
    public Household() {members = new ArrayList<Consumer>();}
    public Household(ArrayList<Consumer> members) { this.members = members; }
    public void addMember(Consumer consumer) { members.add(consumer); }
    @Override
    public String toString() {
        return super.toString() +
            ", Household{" +
            "members=" + members +
            '}';
    }
}

class Industry extends Consumer{
    public Industry() { super("", "", ""); }
    public Industry(String name, String email, String password) { super(name, email, password); }
    @Override
    public String toString() {
        return super.toString() +
            ", Industry{}";
    }
}

public class LabFinal {
    public LabFinal() {}
    public static void main(String[] args) {
        App.setDatabase(new CityRepository("mongodb://localhost:27017", CityRepository.class.getSimpleName()));
        Application.launch(App.class, args);
        out.println("Successful Program Execution");
    }
}