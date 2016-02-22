package detection;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class ESA
{
	public static String executeCommand(String command)
	{
		String s = null, sim = null;

		try
		{
			Runtime run = Runtime.getRuntime();
			Process proc = run.exec(command);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			while ((s = stdInput.readLine()) != null)
			{
				sim = s;
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}

		return sim;
	}

	public static ArrayList<String> findESAWords(ArrayList<String> esaWords)
	{
		ArrayList<String> words = new ArrayList<String>();

		for (String word : esaWords)
		{
			String[] tokens = word.split("_");
			String str1 = tokens[0];
			String word1 = str1.replace(" ", "_");
			String word2 = str1.replace(" ", "_");
			word1 = "\"" + word1.trim() + "\"";
			word2 = "\"" + word2.trim() + "\"";

			String command = "./run_analyzer " + word1 + " " + word2;
			String sim = executeCommand(command);

			if (!sim.equals("NaN"))
			{
				words.add(word);
			}
		}

		return words;
	}

	public static void doESA(Hashtable<String, Integer> allWords, ArrayList<String> otherWords, String esaFile)
			throws FileNotFoundException
	{

		ArrayList<String> oov = findESAWords(otherWords);

		// System.out.println("other " + otherWords.size());
		//
		// for (String word : otherWords)
		// {
		// System.out.println(word);
		// }
		//
		//
		// System.out.println("oov - " + oov.size() + " : ");
		//
		// for (String str : oov)
		// {
		// System.out.print(str + ", ");
		// }
		//
		// System.out.println();

		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(esaFile));

			for (String word1 : oov)
			{
				String[] tokens1 = word1.split("_");
				String str1 = tokens1[0], pos1 = tokens1[1];

				for (String word2 : allWords.keySet())
				{
					String[] tokens2 = word2.split("_");
					String str2 = tokens2[0], pos2 = tokens2[1];

					if ((word1.equals(word2)) || (!pos1.equals(pos2)))
					{
						continue;
					}

					// if ((word1.equals(word2)))
					// {
					// continue;
					// }

					String w1 = str1.replace(" ", "_");
					String w2 = str2.replace(" ", "_");
					w1 = "\"" + w1.trim() + "\"";
					w2 = "\"" + w2.trim() + "\"";

					String command = "./run_analyzer " + w1 + " " + w2;

					String sim = executeCommand(command);

					if (!sim.equals("NaN"))
					{
						// out.write(command + "\n");
						out.write(word1 + "\t" + word2 + "\t" + sim + "\n");
					}
				}

			}

			out.close();
		}

		catch (IOException e)
		{
		}
	}
}