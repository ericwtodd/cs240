package hangman;

import javafx.util.Pair;

import java.io.File;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    Set<String> wordSet;
    boolean[] guessesMade;
    String wordBeingGuessed;
    int guesses;
    int wordLength;

    public EvilHangmanGame() {
        guesses = 0;
        wordLength = 0;
        wordSet = new HashSet<>();
        wordBeingGuessed = "";
        guessesMade = new boolean[26];
    }
/*
    @SuppressWarnings("serial")
    public static class GuessAlreadyMadeException extends IEvilHangmanGame.GuessAlreadyMadeException {
    }
*/

    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>.
     * <p>
     * This method should set up everything required to play the game,
     * but should not actually play the game. (ie. There should not be
     * a loop to prompt for input from the user.)
     *
     * @param dictionary Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    public void startGame(File dictionary, int wordLength) {
        try {

            this.wordSet = new HashSet<>();
            this.wordLength = wordLength;
            Scanner scanner = new Scanner(dictionary);
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (word.matches("^[a-zA-Z]+$") && word.length() == wordLength) {

                    wordSet.add(word.toLowerCase());
                }
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < wordLength; i++) {
                builder.append("-");
            }
            this.wordBeingGuessed = builder.toString();
        } catch (Exception ex) {
            System.out.println("Error! Exception Found: ");
            ex.printStackTrace();
        }
    }


    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these words had been the secret word for the whole game.
     * @throws IEvilHangmanGame.GuessAlreadyMadeException If the character <code>guess</code>
     *                                                    has already been guessed in this game.
     */
    public Set<String> makeGuess(char guess) throws IEvilHangmanGame.GuessAlreadyMadeException {

        if (!guessesMade[(guess - 'a')])//Haven't guessed this letter yet
        {
            guessesMade[(guess - 'a')] = true;//Mark the letter as guessed
            //Here we need to partition the set into subgroups and choose the best one.
            //And return that subgroup of words as the new set

            Map.Entry<String, Set<String>> best = getBestPartition(partitionSet(wordSet, guess));

            //Update the word being guessed
            int changed = 0;
            for (int i = 0; i < best.getKey().length(); i++) {
                if (best.getKey().charAt(i) == '1') {
                    char[] updateWord = wordBeingGuessed.toCharArray();
                    updateWord[i] = guess;
                    this.wordBeingGuessed = String.valueOf(updateWord);
                    changed += 1;
                }
            }
            if (changed == 0) {
                System.out.println("Sorry, there are no " + guess + "\'s");
                guesses -= 1; //subtract one from the number of available guesses.
            } else if (changed == 1) {
                System.out.println("Yes, there is " + changed + ' ' + guess);
            } else {
                System.out.println("Yes, there are " + changed + ' ' + guess + "\'s");
            }
            //Update the set
            this.wordSet = best.getValue();
            return best.getValue();
        } else {
            throw new GuessAlreadyMadeException();
        }
    }

    //Partition the set into subgroups based on position and count
    public Map<String, Set<String>> partitionSet(Set<String> words, char guess) {
        Map<String, Set<String>> partitions = new HashMap<>();
        for (String word : words) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    builder.append(1);
                } else {
                    builder.append(0);
                }
            }
            if (!partitions.containsKey(builder.toString())) {
                partitions.put(builder.toString(), new HashSet<>());
            }
            partitions.get(builder.toString()).add(word);
        }
        return partitions;
    }

    public Map.Entry<String, Set<String>> getBestPartition(Map<String, Set<String>> partitions) {
        Set<String> newWordSet = new HashSet<>();
        int biggestSetSize = newWordSet.size();

//       for (String key : partitions.keySet()) {
//           System.out.println(key + ' ' + partitions.get(key).size());
//        }

        //If there is a biggest set, return that
        if (biggestPartition(partitions) != null) {
            //         System.out.println("We chose by Biggest Partition: " + biggestPartition(partitions).getKey());
            return biggestPartition(partitions);
        }

        //Otherwise, return the set that doesn't have the letter in it
        else if (partitionWithoutLetter(partitions) != null) {
            //       System.out.println("We chose by without Letter: " + partitionWithoutLetter(partitions).getKey());
            return partitionWithoutLetter(partitions);
        }

        //Otherwise, Choose the one that has the letter appear the fewest number of times
        else if (smallestFrequency(partitions) != null) {
            //    System.out.println("We chose by smallestFrequency: " + smallestFrequency(partitions).getKey());
            return smallestFrequency(partitions);
        }

        //Otherwise, choose the one that has the rightmost guessed letter.
        else {
            // System.out.println("We chose by RightMost: " + rightMostPartition(partitions).getKey());
            return rightMostPartition(partitions);
        }
    }

    private Map.Entry<String, Set<String>> biggestPartition(Map<String, Set<String>> partitions) {
        int biggestPartitionSize = 0;
        Map.Entry<String, Set<String>> biggest = null;
        for (Map.Entry<String, Set<String>> entry : partitions.entrySet()) {
            if (entry.getValue().size() > biggestPartitionSize) {
                biggestPartitionSize = entry.getValue().size();
                biggest = entry;
            }
        }
        for (Map.Entry<String, Set<String>> entry : partitions.entrySet()) {
            if (entry.getValue().size() == biggestPartitionSize && !entry.equals(biggest)) {
                return null;
            }
        }
        //If there are two that have the max size
        // else if (entry.getValue().size() == biggestPartitionSize) {
        //     return null;
        // }

        return biggest;
    }

    private Map.Entry<String, Set<String>> partitionWithoutLetter(Map<String, Set<String>> partitions) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < wordLength; i++) {
            builder.append(0);
        }
        for (Map.Entry<String, Set<String>> entry : partitions.entrySet()) {
            if (entry.getKey().equals(builder.toString())) {
                return entry;
            }
        }
        return null;
    }

    private Map.Entry<String, Set<String>> rightMostPartition(Map<String, Set<String>> partitions) {
        String rightMost = null;
        for (Map.Entry<String, Set<String>> entry : partitions.entrySet()) {
            if (rightMost == null) {
                rightMost = entry.getKey();
            }
            if (entry.getKey().compareTo(rightMost) < 0) {
                rightMost = entry.getKey();
            }
//            System.out.println("Key: " + entry.getKey());
        }
        for (Map.Entry<String, Set<String>> entry : partitions.entrySet()) {
            if (entry.getKey().equals(rightMost)) {
//                System.out.println("This is the best one: " + entry.getKey());
                return entry;
            }
        }
        return null;
    }

    private Map.Entry<String, Set<String>> smallestFrequency(Map<String, Set<String>> partitions) {
        int smallestFreq = wordLength;
        Map.Entry<String, Set<String>> smallest = null;

        for (Map.Entry<String, Set<String>> entry : partitions.entrySet()) {
            if (charSum(entry.getKey()) < smallestFreq) {
                smallestFreq = charSum(entry.getKey());
                smallest = entry;
            } else if (charSum(entry.getKey()) == smallestFreq) {
                return null;
            }
        }
        return smallest;
    }

    private int charSum(String word) {
        int sum = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == 1) {
                sum += 1;
            }
        }
        return sum;
    }
}


