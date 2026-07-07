package util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.UserType;
import model.bill.Bill;
import model.rental.Rental;
import model.equipment.Equipment;
import model.user.Staff;
import model.user.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DataStore {
    private static DataStore instance;

    // four on-memory lists
    private ArrayList<User> users;
    private ArrayList<Equipment> equipments;
    private ArrayList<Rental> rentals;
    private ArrayList<Bill> bills;

    private DataStore() {
        this.users = new ArrayList<>();
        this.equipments = new ArrayList<>();
        this.rentals = new ArrayList<>();
        this.bills = new ArrayList<>();
    }

    // every caller does DataStore.getInstance - never new DataStore
    // singleton access point
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public void init() {
        // creates `data/` folder if it doesnt exists
        // loads each json file into matching list
        // if users.json is empty, seeds the default admin account
        createDataFolder();
        loadAll();
        if (users.isEmpty()) {
            seedAdmin();
        }

        verifyOrCreateFile("data/users.json", users);
        verifyOrCreateFile("data/equipments.json", equipments);
        verifyOrCreateFile("data/rentals.json", rentals);
        verifyOrCreateFile("data/bills.json", bills);
    }

    private void verifyOrCreateFile(String path, Object list) {
        File file = new File(path);
        if (!file.exists()) {
            saveList(path, list);
        }
    }

    private void createDataFolder() {
        File folder = new File("data/");
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
    }

    private void loadAll() {
        users = loadList("data/users.json", new TypeToken<ArrayList<User>>() {
        }.getType());
        equipments = loadList("data/equipments.json", new TypeToken<ArrayList<Equipment>>() {
        }.getType());
        rentals = loadList("data/rentals.json", new TypeToken<ArrayList<Rental>>() {
        }.getType());
        bills = loadList("data/bills.json", new TypeToken<ArrayList<Bill>>() {
        }.getType());
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                    @Override
                    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    }
                })
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    @Override
                    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                })
                .registerTypeAdapter(Equipment.class, new JsonSerializer<Equipment>() {
                    @Override
                    public JsonElement serialize(Equipment src, Type typeofSrc, JsonSerializationContext context) {
                        JsonObject result = context.serialize(src).getAsJsonObject();
                        result.addProperty("type", src.getClass().getSimpleName());
                        return result;
                    }
                })
                .registerTypeAdapter(Equipment.class, new JsonDeserializer<Equipment>() {
                    @Override
                    public Equipment deserialize(JsonElement json, Type typeofSrc, JsonDeserializationContext context) throws JsonParseException {
                        JsonObject jsonObject = json.getAsJsonObject();

                        String type = null;
                        if (jsonObject.has("type")) {
                            type = jsonObject.get("type").getAsString();
                        } else if (jsonObject.has("category")) {
                            String categoryType = jsonObject.get("category").getAsString();
                            type = switch (categoryType) {
                                case "Electronics" -> "ElectronicsEquipment";
                                case "Media" -> "MediaEquipment";
                                case "Lab" -> "LabEquipment";
                                default -> null;
                            };
                        }

                        if (type == null) throw new JsonParseException("Failed to determine class type for equipment");

                        try {
                            Class<?> clazz = Class.forName("model.equipment." + type);
                            return context.deserialize(json, clazz);
                        } catch (ClassNotFoundException e) {
                            throw new JsonParseException("Unknown equipment type: " + type, e);
                        }
                    }
                })
                .registerTypeAdapter(User.class, new JsonSerializer<User>() {
                    @Override
                    public JsonElement serialize(User src, Type typeofSrc, JsonSerializationContext context) {
                        JsonObject result = context.serialize(src).getAsJsonObject();
                        result.addProperty("type", src.getClass().getSimpleName());
                        return result;
                    }
                })
                .registerTypeAdapter(User.class, new JsonDeserializer<User>() {
                    @Override
                    public User deserialize(JsonElement json, Type typeofSrc, JsonDeserializationContext context) throws JsonParseException {
                        JsonObject jsonObject = json.getAsJsonObject();

                        String type = null;
                        if (jsonObject.has("type")) {
                            type = jsonObject.get("type").getAsString();
                        } else if (jsonObject.has("userType")) {
                            String userType = jsonObject.get("userType").getAsString();
                            type = ("ADMIN".equals(userType) || "STAFF".equals(userType)) ? "Staff" : "Student";
                        }

                        if (type == null) throw new JsonParseException("Failed to determine class type for user");

                        try {
                            Class<?> clazz = Class.forName("model.user." + type);
                            return context.deserialize(json, clazz);
                        } catch (ClassNotFoundException e) {
                            throw new JsonParseException("Unknown user type: " + type, e);
                        }
                    }
                })
                .create();
    }

    private <T> ArrayList<T> loadList(String path, Type type) {
        try {
            FileReader file = new FileReader(path);
            Gson gson = createGson();
            ArrayList<T> result = gson.fromJson(file, type);
            file.close();
            return result != null ? result : new ArrayList<T>();
        } catch (IOException e) {
            return new ArrayList<T>();
        }
    }

    private void saveList(String path, Object list) {
        try {
            FileWriter file = new FileWriter(path);
            Gson gson = createGson();
            gson.toJson(list, file);
            file.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save: " + path, e);
        }
    }

    private void seedAdmin() {
        // seed admin code here
        String hashPassword = HashUtil.sha256("admin123");
        User admin = new Staff(
                IDGenerator.generateUserId(),
                "System Admin",
                "admin@gmail.com",
                hashPassword,
                IDGenerator.generateStaffId(),
                "Technical",
                UserType.ADMIN
        );
        users.add(admin);
        saveUsers();
    }

    // getters
    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Equipment> getEquipments() {
        return equipments;
    }

    public ArrayList<Rental> getRentals() {
        return rentals;
    }

    public ArrayList<Bill> getBills() {
        return bills;
    }


    // save methods
    public void saveUsers() {
        saveList("data/users.json", users);
    }

    public void saveEquipment() {
        saveList("data/equipments.json", equipments);
    }

    public void saveRental() {
        saveList("data/rentals.json", rentals);
    }

    public void saveBill() {
        saveList("data/bills.json", bills);
    }

}