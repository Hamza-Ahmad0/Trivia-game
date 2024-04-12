import java.util.ArrayList;

public class Pair {
    // ArrayList to store the decider, choices, points, and usernames
    private ArrayList<Integer> decider = new ArrayList<>();
    private ArrayList<Integer> choices = new ArrayList<>();
    private ArrayList<Integer> points = new ArrayList<>();
    private String respondMessage = "none got the correct answer no points";
    private ArrayList<String> usernames = new ArrayList<>();

    // Getter and Setter methods for 'choices'
    public ArrayList<Integer> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<Integer> choices) {
        this.choices = choices;
    }

    // Getter and Setter methods for 'points'
    public ArrayList<Integer> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Integer> points) {
        this.points = points;
    }

    // Getter and Setter methods for 'respondMessage'
    public String getRespondMessage() {
        return respondMessage;
    }

    public void setRespondMessage(String respondMessage) {
        this.respondMessage = respondMessage;
    }

    // Getter and Setter methods for 'usernames'
    public ArrayList<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(ArrayList<String> usernames) {
        this.usernames = usernames;
    }

    // Getter and Setter methods for 'decider'
    public ArrayList<Integer> getDecider() {
        return decider;
    }

    public void setDecider(ArrayList<Integer> decider) {
        this.decider = decider;
    }

    // Method to clear all data in the 'Pair' object
    public void clear() {
        decider.clear();
        choices.clear();
        points.clear();
        respondMessage = "none got the correct answer no points";
        usernames.clear();
    }

    // Methods to add individual elements to the respective ArrayLists
    public void addChoice(int choice) {
        this.choices.add(choice);
    }

    public void addDecider(int decider) {
        this.decider.add(decider);
    }

    public void addPoints(int points) {
        this.points.add(points);
    }

    public void addUsername(String username) {
        this.usernames.add(username);
    }

    // Method to edit the response message
    public void editResponse(String respondMessage) {
        this.respondMessage = respondMessage;
    }

    // Method to clear specific data related to a round
    public void clearRound() {
        this.decider.clear();
        this.choices.clear();
    }
}
