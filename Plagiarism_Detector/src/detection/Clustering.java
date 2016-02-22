package detection;

import java.io.*;
import java.util.*;

public class Clustering
{
	static Hashtable<String, Integer> setIDs;
	static Hashtable<Integer, HashSet<String>> sets;
	static String[] words;
	static double[][] scores;
	static int maxClusId = 0;

	public static void executeCommand(String command)
	{
		try
		{
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void printSets()
	{
		for (Integer setNum : sets.keySet())
		{
			System.out.println("Set #" + setNum + ": " + sets.get(setNum));
		}
	}

	public static ArrayList<Map.Entry<String, Double>> sortValue(Hashtable<String, Double> t)
	{
		// Transfer as List and sort it
		ArrayList<Map.Entry<String, Double>> l = new ArrayList(t.entrySet());
		Collections.sort(l, new Comparator<Map.Entry<String, Double>>()
		{
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
			{
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		return l;
	}

	public static void findSimilarities(String simFile, String oovFile)
	{
		String command = "python nltk_sim.py " + simFile + " " + oovFile;
		executeCommand(command);
	}

	public static Hashtable<String, Integer> formClusters(double threshold, String simFile)
	{
		setIDs = new Hashtable<String, Integer>();
		sets = new Hashtable<Integer, HashSet<String>>();
		int setNum = 1;

		Hashtable<String, Double> similarities = new Hashtable<String, Double>();

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(simFile));

			for (;;)
			{
				String line = br.readLine();
				if (line == null)
				{
					br.close();
					break;
				}

				else
				{
					String[] elems = line.split("\t");
					String key = elems[0] + "\t" + elems[1];
					similarities.put(key, Double.parseDouble(elems[2]));
				}
			}

			br.close();

			ArrayList<Map.Entry<String, Double>> sim = sortValue(similarities);

			for (int i = 0; i < sim.size(); i++)
			{
				String key = sim.get(i).getKey();
				String[] elems = key.split("\t");
				double similarity = sim.get(i).getValue();
				String pos1 = elems[0].split("_")[1], pos2 = elems[1].split("_")[1];

				if (!pos1.substring(0, 2).equals(pos2.substring(0, 2)))
				{
					continue;
				}

				if (similarity < threshold)
				{
					break;
				}

				if ((setIDs.containsKey(elems[0])) && (setIDs.containsKey(elems[1])))
				{
					if (setIDs.get(elems[0]) != setIDs.get(elems[1]))
					{
						int mergedSet = setIDs.get(elems[0]);
						int otherSet = setIDs.get(elems[1]);
						for (String word : sets.get(otherSet))
						{
							sets.get(mergedSet).add(word);
							setIDs.put(word, mergedSet);
						}

						sets.remove(otherSet);
					}
				}

				else if (setIDs.containsKey(elems[0]))
				{
					sets.get(setIDs.get(elems[0])).add(elems[1]);
					setIDs.put(elems[1], setIDs.get(elems[0]));
				}

				else if (setIDs.containsKey(elems[1]))
				{
					sets.get(setIDs.get(elems[1])).add(elems[0]);
					setIDs.put(elems[0], setIDs.get(elems[1]));
				}

				else
				{
					sets.put(setNum, new HashSet<String>());
					sets.get(setNum).add(elems[0]);
					sets.get(setNum).add(elems[1]);
					setIDs.put(elems[0], setNum);
					setIDs.put(elems[1], setNum);
					setNum++;
				}
			}
		}

		catch (Exception e)
		{
			System.out.println("Error: " + e);
		}

		maxClusId = setNum;
		return setIDs;
	}
}