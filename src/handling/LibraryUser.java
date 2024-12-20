package handling;

public class LibraryUser {
    private String name;
    private String userId;


    public LibraryUser(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }


    public String getName() { return name; }
    public String getUserId() { return userId; }


    public void displayUser() {
        System.out.println("Name: " + name + ", UserID: " + userId);
    }
}
