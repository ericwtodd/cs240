package spell;

public class TrieNode implements ITrie.INode {

    int frequencyCount;
    TrieNode[] letters;

    public TrieNode() {
        frequencyCount = 0;
        letters = new TrieNode[26];
    }

    /**
     * Returns the frequency count for the word represented by the node
     *
     * @return The frequency count for the word represented by the node
     */
    public int getValue() {
        return frequencyCount;
    }

    public void toStringHelper(StringBuilder builder, String base) {
        if (frequencyCount != 0) {
            builder.append(base).append('\n');
        }
        for (int i = 0; i < this.letters.length; i++) {
            if (this.letters[i] != null) {
                this.letters[i].toStringHelper(builder, base + (char) (i + 'a'));
            }
        }
    }

    public boolean equalTo(TrieNode node)
    {
        if(node == null){
            return false;
        }
        if (this.frequencyCount != node.frequencyCount)
        {
            return false;
        }
        for (int i = 0; i < this.letters.length; i++)
        {
            if(this.letters[i] == null)
            {
                if (node.letters[i] != null)
                {
                    return false;
                }
            }
            else {
                if(this.letters[i].equalTo(node.letters[i]) == false)
                {
                    return false;
                };
            }
        }
        return true;
    }
}
