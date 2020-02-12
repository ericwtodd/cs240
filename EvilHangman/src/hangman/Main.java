package hangman;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static final String USAGE = "Usage: java Main dictionary(.txt) wordLength(int >= 2) guesses(int >= 1)";

    public static void main(String[] args) {
        //Create a new instance of EvilHangmanGame
        EvilHangmanGame game = new EvilHangmanGame();
        //Create a File and initialize everything you get from the commandline
        if (args.length != 3 || Integer.valueOf(args[1]) < 2 || Integer.valueOf(args[2]) < 1)
        {
            System.out.println("Error: " + USAGE);
            return;
        }

        File file = new File(args[0]);
        game.wordLength = Integer.parseInt(args[1]);
        game.guesses = Integer.parseInt(args[2]);

        game.startGame(file, game.wordLength);

        Set<String> words = new HashSet<>();

        if (game.wordSet.isEmpty())
        {
            System.out.println("There are no words in this file!");
            return;
        }
        GuessesLoop:
        while (game.guesses > 0) {
            if (game.guesses == 1) {
                System.out.println("You have " + game.guesses + " guess left");
            } else {
                System.out.println("You have " + game.guesses + " guesses left");
            }

            System.out.print("Used letters: ");
            //print out the list of guesses.
            for (int i = 0; i < game.guessesMade.length; i++) {
                if (game.guessesMade[i] == true) {
                    System.out.print((char) (i + 'a'));
                    System.out.print(' ');
                }
            }
            System.out.print("\nWord: ");
            System.out.print(game.wordBeingGuessed + '\n');
            //print out '-' for each letter not guessed, otherwise include letter
            // create a scanner so we can read the command-line input

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter guess: ");
            String s = scanner.next();
            //Make it lowercase so all the guesses are uniform.
            if (s.matches("[A-Za-z]{1}")) {
                try {
                    s = s.toLowerCase();
                    words = game.makeGuess(s.charAt(0));
                } catch (IEvilHangmanGame.GuessAlreadyMadeException ex) {
                    System.out.println("You already used that letter");
                }
            } else {
                System.out.println("Invalid input");
            }
            System.out.print('\n');
            if (!game.wordBeingGuessed.contains("-"))//If they've guessed the word
            {
                break GuessesLoop;
            }
        }

        if (!game.wordBeingGuessed.contains("-"))
        {
            System.out.println("You win!");
            System.out.println("The word was: " + game.wordBeingGuessed);
            return;
        }
        else
        {
            System.out.println("You lose!");
           // System.out.println(words);
            System.out.println("The word was: " + words.iterator().next());
            return;
        }
    }
}
