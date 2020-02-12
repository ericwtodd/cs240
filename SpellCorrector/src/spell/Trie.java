package spell;

import java.util.Objects;

public class Trie implements ITrie {
    int wordCount;
    int nodeCount;
    TrieNode root;
    boolean atEnd;

    public Trie() {
        wordCount = 0;
        nodeCount = 1;
        root = new TrieNode();
    }

    /**
     * Adds the specified word to the trie (if necessary) and increments the word's frequency count
     *
     * @param word The word being added to the trie
     */
    public void add(String word) {
        //Make sure all characters are lower case.
        word = word.toLowerCase();
        //Start at the root.
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) //For each character in the word
        {
            char c = word.charAt(i); //Get that character
            int index = c - 'a'; //Find its index
            //If that position is null at the node we're on, there is currently no existent node there.
            //So we make a new node there.
            if (node.letters[index] == null) {
                TrieNode tempNode = new TrieNode();
                node.letters[index] = tempNode;
                node = tempNode; //Then point at the new node we're at.
                nodeCount += 1;
            } else //otherwise point at that letter
            {
                node = node.letters[index];
            }
        }
        if (node.frequencyCount == 0) //If the word we added hasn't been counted yet, (is unique) we count it here.
        {
            wordCount += 1;
        }
        //We're at the end of the word so we increment the frequency count of the node of the end of the word.
        //System.out.println("Node Frequency Count: " + node.frequencyCount);
        node.frequencyCount += 1;
    }

    /**
     * Searches the trie for the specified word
     *
     * @param word The word being searched for
     *
     * @return A reference to the trie node that represents the word,
     * 			or null if the word is not in the trie
     */
    public ITrie.INode find(String word) {
        word = word.toLowerCase();
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int index = c - 'a';
            if (node.letters[index] != null) {
                node = node.letters[index];
            } else {
                return null;
            }
        }
        if (node == null || node.frequencyCount == 0) {
            return null;
        } else {
            return node;
        }
    }

    /**
     * Returns the number of unique words in the trie
     *
     * @return The number of unique words in the trie
     */
    public int getWordCount() {
        return wordCount;
    }

    /**
     * Returns the number of nodes in the trie
     *
     * @return The number of nodes in the trie
     */
    public int getNodeCount() {
        return nodeCount;
    }

    /**
     * The toString specification is as follows:
     * For each word, in alphabetical order:
     * <word>\n
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        root.toStringHelper(output, "");
        return output.toString();
    }
    @Override
    public int hashCode() {
        return wordCount * 3 + nodeCount * 7;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Trie trie = (Trie) o;
        if ((trie.wordCount != this.wordCount) || (trie.nodeCount != this.nodeCount)) {
            return false;
        }
        //Check the whole thing and each member to see if they are exactly the same, if anything is off,
        //they're different
        return this.root.equalTo(trie.root);
    }

    public void reset() {
        wordCount = 0;
        nodeCount = 1;
        root = new TrieNode();
    }
}
