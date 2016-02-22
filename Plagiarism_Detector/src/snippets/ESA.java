package snippets;

import java.io.*;
import java.util.*;

public class ESA
{
	public static Hashtable<String, Double> esaSim = new Hashtable<String, Double>();

	public static void executeCommand(String command, String str1, String str2)
	{
		try
		{
			Runtime run = Runtime.getRuntime();
			Process proc = run.exec(command);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			int count_words = 2;
			count_words += str1.length() - str1.replace(" ", "").length();
			count_words += str2.length() - str2.replace(" ", "").length();

			String s = null;
			while ((s = stdInput.readLine()) != null)
			{
				if (count_words == 0)
				{
					esaSim.put(str1 + "$" + str2, Double.parseDouble(s));
				}

				count_words--;
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void doESA() throws FileNotFoundException
	{
		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new FileReader("esa_input.txt"));
			String line;

			while ((line = br.readLine()) != null)
			{
				String[] words = line.split("\t");

				String word1 = words[0], word2 = words[1];
				String str1 = words[0], str2 = words[1];
				word1 = word1.replace(" ", "_");
				word2 = word2.replace(" ", "_");
				word1 = "\"" + word1 + "\"";
				word2 = "\"" + word2 + "\"";
				word1.trim();
				word2.trim();

				String command = "./run_analyzer " + word1 + " " + word2;
				executeCommand(command, str1, str2);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		doESA();
		System.out.println(esaSim);
	}
}