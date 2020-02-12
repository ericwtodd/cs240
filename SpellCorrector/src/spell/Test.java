package spell;

import javax.sound.midi.SysexMessage;
import java.io.IOException;

public class Test {

    public static void main(String[] args) {
        try {
            test1(args[0]);
            test2(args);
        } catch (Exception ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }
    }


    private static void test1(String s) throws IOException {

        SpellCorrector spellCorrector = new SpellCorrector();
        spellCorrector.useDictionary(s);

        //System.out.print(spellCorrector.dictionary.toString());
        System.out.println("Node Count: " + spellCorrector.dictionary.nodeCount);
        System.out.println("Word Count: " + spellCorrector.dictionary.wordCount);

        System.out.println(spellCorrector.suggestSimilarWord("Sherlock"));
    }

    private static void test2(String[] args) throws IOException
    {
        System.out.println("Dictionary 1:");
        SpellCorrector spellCorrector = new SpellCorrector();
        spellCorrector.useDictionary(args[0]);
        System.out.println("Dictionary 2:");
        SpellCorrector spellCorrector2 = new SpellCorrector();
        spellCorrector2.useDictionary(args[1]);

        System.out.println(spellCorrector.dictionary.equals(spellCorrector2.dictionary));
        System.out.println("1. Node Count: " + spellCorrector.dictionary.nodeCount);
        System.out.println("1. Word Count: " + spellCorrector.dictionary.wordCount);

        System.out.println("2. Node Count: " + spellCorrector2.dictionary.nodeCount);
        System.out.println("2. Word Count: " + spellCorrector2.dictionary.wordCount);

    }


}
