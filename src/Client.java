import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;

/**
 * The `Client` class represents a client application that communicates with a server.
 * It handles user authentication, game initiation, and menu navigation through a GUI.
 */
public class Client extends Thread {

    private static int ID;           // Unique identifier for the client
    private static String UserName;   // Username of the client

    /**
     * The main method where the execution of the client application begins.
     * It establishes a connection with the server and handles user authentication.
     * Once authenticated, it presents a menu for various actions such as starting a new game,
     * viewing results, showing the leaderboard, and signing out.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        try {
            // Establish a connection to the server using a socket
            InetAddress address = InetAddress.getLoopbackAddress();
            Socket socket = new Socket(address, 2000);

            // Set up input and output streams for communication with the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // Prompt the user to either sign up or log in
            int cho = JOptionPane.showOptionDialog(
                    null,
                    "Choose an option:",
                    "Sign Up or Sign In",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[]{"Sign up", "Login"},
                    "Sign up"
            );
            writer.println(cho);

            // User authentication loop
            while (true) {
                // Prompt the user to enter username and password
                UserName = JOptionPane.showInputDialog(null, "Enter your username:", "Username Input", JOptionPane.QUESTION_MESSAGE);
                String Password = JOptionPane.showInputDialog(null, "Enter your password:", "password Input", JOptionPane.QUESTION_MESSAGE);

                // Send username and hashed password to the server
                writer.println(UserName);
                writer.println(Password.hashCode());

                // Receive server response indicating the success or failure of authentication
                int ServerResponse = Integer.parseInt(reader.readLine());

                // Handle authentication responses
                if (ServerResponse == 0) {
                    JOptionPane.showMessageDialog(null, "Logged in successfully");
                    ID = Integer.parseInt(reader.readLine());
                    break;
                } else if ((ServerResponse == 1) && cho == 0) {
                    // Username already taken during sign up
                    int option = JOptionPane.showOptionDialog(
                            null,
                            "UserName already taken\nDo you want to re-enter or exit?",
                            "Server Response",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            new Object[]{"Re-enter", "Exit"},
                            "Re-enter");

                    if (option == 1)
                        System.exit(0);
                } else if (ServerResponse == 2 && cho == 1) {
                    // No matching username during login
                    int option = JOptionPane.showOptionDialog(
                            null,
                            "No username matches",
                            "Server Response",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            new Object[]{"Re-enter", "Exit"},
                            "Re-enter");

                    if (option == 1)
                        System.exit(0);
                } else if (ServerResponse == 3 && cho == 1) {
                    // Incorrect password during login
                    int option = JOptionPane.showOptionDialog(
                            null,
                            "Password Incorrect",
                            "Server Response",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            new Object[]{"Re-enter", "Exit"},
                            "Re-enter");

                    if (option == 1)
                        System.exit(0);
                } else if (ServerResponse == 4 && cho == 1) {
                    // Account already logged in during login
                    int option = JOptionPane.showOptionDialog(
                            null,
                            "Account already logged in",
                            "Server Response",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            new Object[]{"Re-enter", "Exit"},
                            "Re-enter");

                    if (option == 1)
                        System.exit(0);
                }
            }

            // Main menu loop after successful authentication
            int Signout = 0;
            while (Signout != 1) {
                // Display main menu options to the user
                Object[] options = {"Start new game", "Show my results", "Show leaderboard", "Sign out"};
                int choice = JOptionPane.showOptionDialog(
                        null,
                        "Select an option:",
                        "Main Menu",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]  // Default choice (optional)
                );

                // Send the user's choice to the server
                writer.println(choice);

                // Switch based on user's menu choice
                switch (choice) {
                    case 0:
                        // Start a new game
                        NewGame(reader, writer);
                        break;
                    case 1:
                        // Show user's results
                        ShowMyResult(reader, writer);
                        break;
                    case 2:
                        // Show the leaderboard
                        ShowLeaderboard(reader);
                        break;
                    case 3:
                        // Sign out
                        writer.println(ID);
                        socket.close();
                        Signout = 1;
                        break;
                    default:
                        System.out.println("Invalid choice");
                        break;
                }
            }
        } catch (Exception e) {
            // Handle exceptions
            System.err.println("ERROR - " + e);
        }
    }

    /**
     * Method to handle the initiation and progress of a new game.
     * It prompts the user with questions, receives user choices, and displays the results.
     *
     * @param reader BufferedReader for reading from the server.
     * @param writer PrintWriter for writing to the server.
     * @throws IOException if an I/O error occurs.
     */
    public static void NewGame(BufferedReader reader, PrintWriter writer) throws IOException {
        // Get the number of players in the game
        int PlayersCount = Integer.parseInt(reader.readLine());

        // Display waiting message if the number of players is odd (waiting for another player)
        if (PlayersCount % 2 != 0) {
            JOptionPane.showMessageDialog(null,
                    "Waiting for another player...",
                    "Waiting for Players",
                    JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon("waiting_icon.png"));  // Replace with your icon path
        }

        // Notify that the game has started once the second player joins
        reader.readLine();
        JOptionPane.showMessageDialog(null,
                "Game has started!!",
                "Starting Game",
                JOptionPane.INFORMATION_MESSAGE);

        try {
            // Loop through 5 rounds of the game
            for (int i = 0; i < 5; i++) {
                // Receive the question and choices from the server
                String question = reader.readLine();
                String[] choices = new String[4];
                for (int j = 0; j < 4; j++) {
                    choices[j] = reader.readLine();
                }

                // Display the question and choices to the user
                int choice = JOptionPane.showOptionDialog(
                        null,
                        question,
                        UserName,
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        choices,
                        null
                );
                choice++;

                // Send the user's choice and ID to the server
                writer.println(choice);
                writer.println(ID);

                // Receive and display the result message from the server
                String respondedMsg = reader.readLine();
                JOptionPane.showMessageDialog(null,
                        respondedMsg,
                        "Question result",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            // Receive and send the final points and username to the server
            int points = Integer.parseInt(reader.readLine());
            writer.println(points);
            writer.println(UserName);

            // Receive and display the final results message from the server
            reader.readLine();
            String msg = reader.readLine();
            writer.println(ID);
            JOptionPane.showMessageDialog(null,
                    msg,
                    "Results",
                    JOptionPane.INFORMATION_MESSAGE);

            // Display the user's final points
            JOptionPane.showMessageDialog(null,
                    "Your points = " + reader.readLine(),
                    UserName,
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            // Handle exceptions
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to handle the display of the user's game results.
     *
     * @param reader BufferedReader for reading from the server.
     * @param writer PrintWriter for writing to the server.
     * @throws IOException if an I/O error occurs.
     */
    public static void ShowMyResult(BufferedReader reader, PrintWriter writer) throws IOException {
        // Send the username to the server
        writer.println(UserName);

        // Receive the number of games played by the user
        int numGames = Integer.parseInt(reader.readLine());
        String respondedMsg;

        try {
            // Display the user's results or a message indicating no games played
            if (numGames > 0) {
                respondedMsg = "";
                for (int i = 0; i < numGames; i++) {
                    for (int j = 0; j < 2; j++) {
                        respondedMsg += reader.readLine() + "\n";
                    }
                }
            } else {
                respondedMsg = "No game was played";
            }
        } catch (IOException e) {
            // Handle exceptions
            throw new RuntimeException(e);
        }

        // Display the user's results
        JOptionPane.showMessageDialog(null,
                respondedMsg,
                "Result",
                JOptionPane.INFORMATION_MESSAGE);

        // Notify that the information has been received
        reader.readLine();
    }

    /**
     * Method to handle the display of the leaderboard.
     *
     * @param reader BufferedReader for reading from the server.
     * @throws IOException if an I/O error occurs.
     */
    public static void ShowLeaderboard(BufferedReader reader) throws IOException {
        // Receive the leaderboard information from the server
        String respondedMsg;

        try {
            respondedMsg = "";
            for (int i = 0; i < 6; i++) {
                respondedMsg += reader.readLine() + "\n";
            }
        } catch (IOException e) {
            // Handle exceptions
            throw new RuntimeException(e);
        }

        // Display the leaderboard
        JOptionPane.showMessageDialog(null,
                respondedMsg,
                "Leaderboard",
                JOptionPane.INFORMATION_MESSAGE);

        // Notify that the information has been received
        reader.readLine();
    }
}
