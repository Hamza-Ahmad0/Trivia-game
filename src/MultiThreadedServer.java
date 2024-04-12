import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class MultiThreadedServer {
    // ArrayList to store questions
    private static ArrayList<Question> questions;

    // ArrayList to store a subset of questions for each game session
    private static ArrayList<Question> fiveQuestions;

    // ArrayList to store player information
    private static ArrayList<Player> Players = new ArrayList<>();

    // Counter for the number of players connected to the server
    private static int PlayersCount = 0;

    // ArrayList to store pairs of clients playing together
    private static ArrayList<Pair> ClientPair = new ArrayList<>();

    // Counter for the game sessions
    private static int GameID = 0;

    // Server port number
    private static int ServerPort = 2000;

    // Object used for synchronization
    private static final Object lock = new Object();

    // DateTimeFormatter for formatting date and time
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/LLL/yy");

    public static void main(String[] args) {
        // Load questions from file
        try {
            ObjectInput objectInput = new ObjectInputStream(new FileInputStream("questions.out"));
            questions = (ArrayList<Question>) objectInput.readObject();
            objectInput.close();
        } catch (IOException | ClassNotFoundException ioException) {
            System.err.println("ERROR - " + ioException);
        }

        // Load player information from file
        try {
            ObjectInput objectInput = new ObjectInputStream(new FileInputStream("Users.txt"));
            Players = (ArrayList<Player>) objectInput.readObject();
            objectInput.close();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while reading players");
        }

        try {
            // Create a server socket to accept client connections
            ServerSocket serverSocket = new ServerSocket(ServerPort);

            // Continuously accept client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                // Create a new thread to handle the client
                ClientHandler nextClient = new ClientHandler(clientSocket);
                nextClient.start();
            }

        } catch (IOException e) {
            System.err.println("Error while accepting a client");
        }
    }

    // Inner class representing a thread that handles communication with a client
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private static ArrayList<Integer> usersNum = new ArrayList<>();
        private static int IdIndex = 0;

        // Constructor to initialize the client socket
        ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

                // Sign up or Log-In
                int response = 1;
                int choice = Integer.parseInt(reader.readLine());

                // Continue handling user choices until the user successfully signs up or logs in
                while (response != 0) {
                    switch (choice) {
                        case 0:
                            // Sign up
                            response = SignUp(reader, writer);
                            writer.println(response);
                            if (response == 0) {
                                writer.println(Players.size() - 1);
                                IdIndex++;
                            }
                            break;
                        case 1:
                            // Log in
                            response = SignIn(reader, writer);
                            writer.println(response);
                            if (response == 0) {
                                writer.println(usersNum.get(IdIndex));
                                IdIndex++;
                            }
                            break;
                    }
                }

                // Main menu loop
                int userChoice = 0;
                while (userChoice != 3) {
                    userChoice = Integer.parseInt(reader.readLine());
                    switch (userChoice) {
                        case 0:
                            // Start a new game session
                            PlayersCount++;
                            if (PlayersCount % 2 != 0) {
                                // Shuffle questions for each new game session
                                ArrayList<Question> shuffledQuestions = new ArrayList<>(questions);
                                Collections.shuffle(shuffledQuestions);
                                fiveQuestions = new ArrayList<>(shuffledQuestions.subList(0, 5));
                            }
                            new Gamesession(writer, reader, (PlayersCount % 2 == 0) ? GameID++ : GameID).start();
                            PlayersCount--;
                            break;
                        case 1:
                            // Show user's game history
                            ShowMyResult(reader, writer);
                            break;
                        case 2:
                            // Show leaderboard
                            ShowLeaderboard(writer);
                            break;
                        case 3:
                            // Log out
                            IdIndex--;
                            usersNum.remove(usersNum.indexOf(Integer.parseInt(reader.readLine())));
                            break;
                        default:
                            System.out.println("Invalid choice");
                            break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Method to handle user sign-up
        public synchronized static int SignUp(BufferedReader reader, PrintWriter writer) {
            String username;
            int password;
            try {
                username = reader.readLine();
                password = Integer.parseInt(reader.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Check if the username already exists
            for (int i = 0; i < Players.size(); i++) {
                if (username.equals(Players.get(i).GetUserName())) {
                    return 1; // Username already exists
                }
            }

            // Create a new player and add to the list
            Player newPlayer = new Player(username, password);
            Players.add(newPlayer);

            // Save updated player information to file
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("Users.txt"));
                objectOutputStream.writeObject(Players);
                objectOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return 0; // Sign-up successful
        }

        // Method to handle user sign-in
        public synchronized static int SignIn(BufferedReader reader, PrintWriter writer) {
            String username;
            int password;
            try {
                username = reader.readLine();
                password = Integer.parseInt(reader.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Check if the user is already signed in
            for (int i = 0; i < usersNum.size(); i++) {
                if (IdIndex >= 1) {
                    if (username.equals(Players.get(usersNum.get(i)).GetUserName())) {
                        return 4; // User is already signed in
                    }
                }
            }

            // Check if the username and password match
            for (int i = 0; i < Players.size(); i++) {
                if (username.equals(Players.get(i).GetUserName())) {
                    if (password == Players.get(i).GetPasswordHash()) {
                        usersNum.add(i);
                        return 0; // Sign-in successful
                    }
                    return 3; // Incorrect password
                }
            }
            return 2; // User not found
        }
    }

    // Method to display the user's game history
    public static void ShowMyResult(BufferedReader reader, PrintWriter writer) {
        String username;
        try {
            username = reader.readLine();
            String result = "";

            // Find the player by username
            Player currentPlayer = findPlayerByUsername(username);

            if (currentPlayer != null) {
                int numGames = currentPlayer.GetGamesPlayed();

                // Display the game history
                for (int i = 0; i < numGames; i++) {
                    result += "The Game Number " + (i + 1) + "\n" + currentPlayer.History(i) + "\n";
                }
            } else {
                writer.println("Player not found.");
            }

            // Send the number of games and the result to the client
            writer.println(currentPlayer.GetGamesPlayed());
            writer.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to find a player by username
    public static Player findPlayerByUsername(String username) {
        for (Player player : Players) {
            if (player.GetUserName().equals(username)) {
                return player;
            }
        }
        return null;
    }

    // Method to display the leaderboard
    public static void ShowLeaderboard(PrintWriter writer) {
        // Lists to store player information for sorting
        ArrayList<Integer> sortScore = new ArrayList<>();
        ArrayList<Integer> sortGames = new ArrayList<>();
        ArrayList<String> sortUser = new ArrayList<>();
        ArrayList<String> Top = new ArrayList<>();

        // Populate the sorting lists
        for (int i = 0; i < Players.size(); i++) {
            sortScore.add(Players.get(i).GetTotalScore());
            sortUser.add(Players.get(i).GetUserName());
            sortGames.add(Players.get(i).GetGamesPlayed());
        }

        // Sort players by points
        sortPlayersByPoints(sortUser, sortGames, sortScore);

        // Build the leaderboard message
        String Top5 = "Leaderboard:\n";
        for (int i = 0; i < 5; i++) {
            Top5 += (i + 1) + ") " + sortUser.get(i) + " , points: " + sortScore.get(i) +
                    " , Number of games: " + sortGames.get(i) + "\n";
        }

        // Send the leaderboard message to the client
        writer.println(Top5);
    }

    // Method to sort players by points
    private static void sortPlayersByPoints(ArrayList<String> Username, ArrayList<Integer> gamesPlayed, ArrayList<Integer> Score) {
        for (int i = 0; i < Score.size() - 1; i++) {
            for (int j = 0; j < Score.size() - i - 1; j++) {
                if (Score.get(j) < Score.get(j + 1)) {
                    // Swap positions in the sorting lists
                    Collections.swap(Score, j, j + 1);
                    Collections.swap(Username, j, j + 1);
                    Collections.swap(gamesPlayed, j, j + 1);
                }
            }
        }
    }

    // Inner class representing a game session
    private static class Gamesession extends Thread {
        private BufferedReader reader;
        private PrintWriter writer;
        private int GameiD;

        // Constructor to initialize the game session
        Gamesession(PrintWriter writer, BufferedReader reader, int GameID) {
            this.writer = writer;
            this.reader = reader;
            this.GameiD = GameID;
            try {
                NewGame(reader, writer);
            } catch (IOException e) {
                System.err.println("Error while starting the game");
            }
        }

        // Method to handle a new game
        public void NewGame(BufferedReader reader, PrintWriter writer) throws IOException {

            // Send the number of players to the client
            writer.println(PlayersCount);

            // Check if it's the first or second player in the game session
            if (PlayersCount % 2 != 0) {
                synchronized (lock) {
                    try {
                        // Create a pair for the game session and wait for the second player
                        Pair gamePair = new Pair();
                        MultiThreadedServer.ClientPair.add(gamePair);
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Block the first thread
            writer.println(1);

            // Check if it's the second player and wake up the first thread
            if (PlayersCount % 2 == 0) {
                synchronized (lock) {
                    lock.notify();
                } // Second thread wakes up the first thread
            }

            int point = 0;
            boolean FirstWrong = true;

            // Loop through the questions in the game session
            for (Question question : fiveQuestions) {
                // Send the question and choices to the clients
                writer.println(question.getQuestion());
                for (int i = 0; i < question.getChoices().length; i++) {
                    writer.println(question.getChoices()[i]);
                }

                FirstWrong = true;

                // Receive the player's choice and the decider's ID
                int choice = Integer.parseInt(reader.readLine());
                int QuestionWinner = Integer.parseInt(reader.readLine());

                // Update the pair with the choices and deciders
                ClientPair.get(GameiD).addChoice(choice);
                ClientPair.get(GameiD).addDecider(QuestionWinner);

                // Check if the choice is correct and update points
                if ((ClientPair.get(GameiD).getChoices().get(0) == question.getCorrectAnswer()) && (ClientPair.get(GameiD).getDecider().size() < 2)) {
                        synchronized (lock) {
                            point += question.getPoints();
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    System.out.println(GameiD);
                    ClientPair.get(GameiD).editResponse("Answer is " + question.getCorrectAnswer() + " ,"
                            + Players.get(ClientPair.get(GameiD).getDecider().get(0)).GetUserName() + " Got the points = "
                            + question.getPoints());
                }
                //if 1st player wrong wait for the 2nd
                if (ClientPair.get(GameiD).getDecider().size() < 2){
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                    FirstWrong = false;
                }
                //if 2nd player is the winner of the question
                if ((ClientPair.get(GameiD).getChoices().get(0) != question.getCorrectAnswer()) &&
                        (ClientPair.get(GameiD).getChoices().get(1) == question.getCorrectAnswer())) {
                    synchronized (lock) {
                        if (FirstWrong) {
                            point += question.getPoints();
                            lock.notify();
                        }
                    }
                    System.out.println(GameiD);
                    ClientPair.get(GameiD).editResponse("Answer is " + question.getCorrectAnswer() + " ,"
                            + Players.get(ClientPair.get(GameiD).getDecider().get(1)).GetUserName() + " Got the points = "
                            + question.getPoints());
                }

                // Wake up the waiting threads
                if (((ClientPair.get(GameiD).getChoices().get(0) == question.getCorrectAnswer()) &&
                        (ClientPair.get(GameiD).getChoices().get(1) != question.getCorrectAnswer())) ||
                        ((ClientPair.get(GameiD).getChoices().get(0) != question.getCorrectAnswer()) &&
                                (ClientPair.get(GameiD).getChoices().get(1) != question.getCorrectAnswer())) ||
                        ((ClientPair.get(GameiD).getChoices().get(0) == question.getCorrectAnswer()) &&
                                (ClientPair.get(GameiD).getChoices().get(1) == question.getCorrectAnswer()))){
                    synchronized (lock) {
                        lock.notify();
                    }
                }

                // Pause for 3 seconds
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }

                // Check if no player got the correct answer
                if ((ClientPair.get(GameiD).getDecider().size() == 2)
                        && (ClientPair.get(GameiD).getChoices().get(0) != question.getCorrectAnswer())
                        && (ClientPair.get(GameiD).getChoices().get(1) != question.getCorrectAnswer()))
                    ClientPair.get(GameiD).editResponse("None got the correct answer and the correct answer is  "
                            + question.getCorrectAnswer());

                // Send the response message to the clients
                writer.println(ClientPair.get(GameiD).getRespondMessage());

                // Clear choices and deciders for the next round
                ClientPair.get(GameiD).clearRound();
            }

            // Send the final points to the clients
            writer.println(point);

            // Update pair with points, usernames, and wake up the waiting threads
            synchronized (lock) {
                ClientPair.get(GameiD).addPoints(Integer.valueOf(reader.readLine()));
                ClientPair.get(GameiD).addUsername(reader.readLine());
            }

            if(ClientPair.get(GameiD).getUsernames().size() < 2){
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            writer.println(1);

            if(ClientPair.get(GameiD).getUsernames().size() == 2){
                synchronized (lock) {
                    lock.notify();
                }
            }
            //checks the result of the match and displays it
            LocalDateTime matchDate = LocalDateTime.now();
            String msg = "its a draw " + ClientPair.get(GameiD).getUsernames().get(0) + " with " +
                    ClientPair.get(GameiD).getPoints().get(0) + " points vs " + ClientPair.get(GameiD).getUsernames().get(1) +
                    " with " + ClientPair.get(GameiD).getPoints().get(1) + " points " + dtf.format(matchDate);

            if(ClientPair.get(GameiD).getPoints().get(0) > ClientPair.get(GameiD).getPoints().get(1)){
                msg = "Winner is " +ClientPair.get(GameiD).getUsernames().get(0) + " with " + ClientPair.get(GameiD).getPoints().get(0) +
                        " points vs " + ClientPair.get(GameiD).getUsernames().get(1) + " with " + ClientPair.get(GameiD).getPoints().get(1) +
                        " points " + dtf.format(matchDate);
            }

            if(ClientPair.get(GameiD).getPoints().get(0) < ClientPair.get(GameiD).getPoints().get(1)){
                msg = "Winner is " +ClientPair.get(GameiD).getUsernames().get(1) + " with " + ClientPair.get(GameiD).getPoints().get(1) +
                        " points vs " + ClientPair.get(GameiD).getUsernames().get(0) + " with " + ClientPair.get(GameiD).getPoints().get(0) +
                        " points " + dtf.format(matchDate);
            }
            writer.println(msg);

            //update players data
            int ID = Integer.parseInt(reader.readLine());
            Players.get(ID).updateHistory(msg);
            Players.get(ID).SetGamesScore(point);
            Players.get(ID).UpdateGamesPlayed();
            Players.get(ID).updateTotalScore();
            writer.println(point);

            ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream("Users.txt"));
            obj.writeObject(Players);
            obj.close();

        }
    }
}

