
import java.io.Serializable;

/**
 * The `Question` class represents a quiz question with the question text,
 * a list of choices, the correct answer index, and the associated points.
 * It implements the Serializable interface for object serialization.
 */
public class Question implements Serializable {
    // Private instance variables to store question information
    private String question;
    private String[] choices;
    private int correctAnswer;
    private int points;

    // Default constructor
    public Question() {}

    /**
     * Parameterized constructor to initialize a Question object.
     *
     * @param question      The text of the question.
     * @param choices       An array of choices for the question.
     * @param correctAnswer The index of the correct answer in the choices array.
     * @param points        The points associated with the question.
     */
    public Question(String question, String[] choices, int correctAnswer, int points) {
        this.question = question;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
        this.points = points;
    }

    // Getter method to retrieve the question text
    public String getQuestion() {
        return question;
    }

    // Setter method to set the question text
    public void setQuestion(String question) {
        this.question = question;
    }

    // Getter method to retrieve the array of choices
    public String[] getChoices() {
        return choices;
    }

    // Setter method to set the array of choices
    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    // Getter method to retrieve the index of the correct answer
    public int getCorrectAnswer() {
        return correctAnswer;
    }

    // Setter method to set the index of the correct answer
    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    // Getter method to retrieve the points associated with the question
    public int getPoints() {
        return points;
    }

    // Setter method to set the points associated with the question
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Override of the toString method to provide a string representation of the question.
     *
     * @return A formatted string containing the question, choices, and points.
     */
    @Override
    public String toString() {
        String s = question + " (" + points + " points)\n";
        for (int i = 0; i < choices.length; i++)
            s += choices[i] + "\n";
        return s;
    }
}

