package snippets;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordLemmatizer
{

	protected StanfordCoreNLP pipeline;

	public StanfordLemmatizer()
	{
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		this.pipeline = new StanfordCoreNLP(props);
	}

	public String lemmatize(String documentText)
	{
		// Create an empty Annotation just with the given text
		Annotation document = new Annotation(documentText);
		// run all Annotators on this text
		this.pipeline.annotate(document);
		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		String output = "";

		for (CoreMap sentence : sentences)
		{
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class))
			{
				// Retrieve and add the lemma for each word into the
				// list of lemmas
				// lemmas.add(token.get(LemmaAnnotation.class));
				output = output + " " + (token.get(LemmaAnnotation.class));
			}
		}
		return output.trim();
	}

	public static void main(String[] args)
	{
		System.out.println("Starting Stanford Lemmatizer");
		String text = "rise industry growth cities expansion population three great developments late nineteenth century american history new larger steam powered factories became feature american landscape east transformed farm hands industrial laborers provided jobs rising tide immigrants industry came urbanization growth large cities like fall river massachusetts bordens lived became centers production well commerce trade";
		StanfordLemmatizer slem = new StanfordLemmatizer();
		System.out.println(slem.lemmatize(text));
	}
}