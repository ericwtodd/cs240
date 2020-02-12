package spell;

import com.sun.jdi.ClassType;

import javax.sound.midi.SysexMessage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class SpellCorrector implements ISpellCorrector {
    Trie dictionary;

    public SpellCorrector() {
        //The Trie is initialized to Null
        dictionary = new Trie();
    }

    /**
     * Tells this <code>SpellCorrector</code> to use the given file as its dictionary
     * for generating suggestions.
     *
     * @param dictionaryFileName File containing the words to be used
     * @throws IOException If the file cannot be read
     */
    public void useDictionary(String dictionaryFileName) throws IOException {
        dictionary.reset();
        //Clear the Dictionary first before adding new words.
        //If it's a valid file, load it and add all the words into the Dictionary Trie
        File file = new File(dictionaryFileName);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String word = scanner.next();
            if (word.matches("^[a-zA-Z]+$")) {
                dictionary.add(word);
            }
        }
    }

    /**
     * Suggest a word from the dictionary that most closely matches
     * <code>inputWord</code>
     *
     * @param inputWord
     * @return The suggestion or null if there is no similar word in the dictionary
     */
    public String suggestSimilarWord(String inputWord) {
        inputWord = inputWord.toLowerCase();
        if (dictionary.find(inputWord) != null) //The word is in the Trie
        {
            return inputWord;
        } else if (getBestWord(getVariants(inputWord)) != null) //If Distance 1
        {
            return getBestWord(getVariants(inputWord));
        } else //If Distance 2 Words
        {
            Set<String> distance2 = new HashSet<>();
            for (String word : getVariants(inputWord)) {

                distance2.addAll(getVariants(word));
            }
            return getBestWord(distance2);
        }
    }

    public Set<String> getVariants(String inputWord) {
        Set<String> words = new HashSet<>();
        words.addAll(deletionDistance1(inputWord));
        words.addAll(transpositionDistance1(inputWord));
        words.addAll(alterationDistance1(inputWord));
        words.addAll(insertionDistance1(inputWord));
        return words;
//
    }

    public String getBestWord(Set<String> words) {
        String bestWord = null;
        int bestWordValue = 0;
        for (String word : words) {
            if (dictionary.find(word) != null)//It's in the dictionary
            {
                if (dictionary.find(word).getValue() >= bestWordValue) {
                    if (bestWord == null)
                    {
                        bestWord = word;
                        bestWordValue = dictionary.find(word).getValue();
                    }
                    if (word.compareTo(bestWord) <= 0) //word is first alphabetically, or the exact same word
                    {
                        bestWord = word;
                        bestWordValue = dictionary.find(word).getValue();
                    }
                }
            }
        }
//        words.stream()
//                .map((word) -> dictionary.find(word))
//                .filter((node) -> node != null)
//                .collect(Collectors.toSet());
        return bestWord;
    }

    public Set<String> deletionDistance1(String word) {
        Set<String> deletion1 = new HashSet<>();
        for (int i = 0; i < word.length(); i++) {
            StringBuilder builder = new StringBuilder(word);
            deletion1.add(builder.deleteCharAt(i).toString());
        }
        //System.out.println(deletion1);
        return deletion1;
    }

    public Set<String> transpositionDistance1(String word) {
        Set<String> transposition1 = new HashSet<>();
        for (int i = 0; i + 1 < word.length(); i++) {
            char[] chars = word.toCharArray();
            char temp = chars[i];
            chars[i] = chars[i + 1];
            chars[i + 1] = temp;
            transposition1.add(String.valueOf(chars));
        }
        //System.out.println(transposition1);
        return transposition1;
    }

    public Set<String> alterationDistance1(String word) {
        Set<String> alteration1 = new HashSet<>();
        for (int i = 0; i < word.length(); i++)//Each letter in the word
        {
            char[] chars = word.toCharArray();
            for (int j = 0; j < 26; j++)//Each letter in the alphabet
            {
                if (word.charAt(i) != (char) (j + 'a')) {
                    chars[i] = (char) (j + 'a');
                    alteration1.add(String.valueOf(chars));
                }
            }
        }
       // System.out.println(alteration1);
       // System.out.println(alteration1.size());
        return alteration1;
    }

    public Set<String> insertionDistance1(String word) {
        Set<String> insertion1 = new HashSet<>();
        for (int i = 0; i <= word.length(); i++) {
            for (int j = 0; j < 26; j++) {
                StringBuilder builder = new StringBuilder(word);
                builder.insert(i, (char) (j + 'a'));
                insertion1.add(builder.toString());
            }
        }
        //System.out.println(insertion1);
        //System.out.println(insertion1.size());
        return insertion1;
    }
}