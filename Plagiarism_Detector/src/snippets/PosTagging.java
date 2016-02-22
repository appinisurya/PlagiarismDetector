package snippets;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PosTagging
{
	public static void main(String[] args)
	{
		String a = "who is going to train him";
		MaxentTagger tagger = new MaxentTagger("models/wsj-0-18-left3words-distsim.tagger");
		String tagged = tagger.tagString(a);
		System.out.println(tagged);
	}
}
