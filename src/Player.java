import java.io.Serializable;
import java.util.ArrayList;

/**
 * The `Player` class represents a player in a game. It implements the `Serializable` interface
 * to enable instances of this class to be serialized for storage or transmission.
 */
public class Player implements Serializable {
    // Private instance variables to store player information
    private String UserName;
    private int PasswordHash;
    private ArrayList<Integer> GameScore = new ArrayList<>();
    private int GamesPlayed;
    private int totalScore;
    private ArrayList<String> OpponentsInfo = new ArrayList<>();

    /**
     * Constructor to initialize a `Player` object with a username and password hash.
     *
     * @param UserName      The username of the player.
     * @param PasswordHash  The hash of the player's password.
     */
    Player(String UserName, int PasswordHash) {
        this.UserName = UserName;
        this.PasswordHash = PasswordHash;
    }

    /**
     * Getter method to retrieve the username of the player.
     *
     * @return The username of the player.
     */
    public String GetUserName() {
        return this.UserName;
    }

    /**
     * Getter method to retrieve the password hash of the player.
     *
     * @return The password hash of the player.
     */
    public int GetPasswordHash() {
        return this.PasswordHash;
    }

    /**
     * Getter method to retrieve the game score for a specific game based on the index.
     *
     * @param index The index of the game for which to retrieve the score.
     * @return The game score for the specified game.
     */
    public int GetScore(int index) {
        return this.GameScore.get(index);
    }

    /**
     * Getter method to retrieve the total number of games played by the player.
     *
     * @return The total number of games played by the player.
     */
    public int GetGamesPlayed() {
        return this.GamesPlayed;
    }

    /**
     * Getter method to retrieve the total score of the player.
     *
     * @return The total score of the player.
     */
    public int GetTotalScore() {
        return this.totalScore;
    }

    /**
     * Getter method to retrieve opponent information for a specific game based on the index.
     *
     * @param index The index of the game for which to retrieve opponent information.
     * @return Opponent information for the specified game.
     */
    public String History(int index) {
        return this.OpponentsInfo.get(index).toString();
    }

    /**
     * Method to update opponent information for a game.
     *
     * @param result Opponent information to be added to the list.
     */
    public void updateHistory(String result) {
        this.OpponentsInfo.add(result);
    }

    /**
     * Method to increment the total number of games played by the player.
     */
    public void UpdateGamesPlayed() {
        this.GamesPlayed += 1;
    }

    /**
     * Method to set the game score for a specific game.
     *
     * @param GameScore The game score to be added to the list.
     */
    public void SetGamesScore(int GameScore) {
        this.GameScore.add(GameScore);
    }

    /**
     * Method to update the total score of the player based on the latest game played.
     */
    public void updateTotalScore() {
        this.totalScore += this.GameScore.get(this.GamesPlayed - 1);
    }
}
