package snippets;

import java.util.*;

public class PosTagConvertor
{
	public static Hashtable<String, Character> convertToWordNetTags(Hashtable<String, String> words)
	{
		Hashtable<String, Character> tags = new Hashtable<String, Character>();
		for (String key : words.keySet())
		{
			if (words.get(key).startsWith("N"))
			{
				tags.put(key, 'n');
			}

			else if (words.get(key).startsWith("J"))
			{
				tags.put(key, 'a');
			}

			else if (words.get(key).startsWith("V"))
			{
				tags.put(key, 'v');
			}

			else if (words.get(key).startsWith("R"))
			{
				tags.put(key, 'r');
			}

			else
			{
				tags.put(key, ' ');
			}
		}

		return tags;

	}

	public static void main(String[] args)
	{
		Hashtable<String, String> words = new Hashtable<String, String>();
		words.put("biggest", "JJS");
		words.put("John", "NNP");
		words.put("better", "RBR");
		words.put("took", "VBD");
		words.put("in", "IN");
		System.out.println(convertToWordNetTags(words));
	}

}
