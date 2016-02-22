package detection;

import java.io.*;
import java.util.*;

import snippets.StanfordLemmatizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class SampleCheck
{
	static Hashtable<String, Integer> Clusters = new Hashtable<String, Integer>();
	static HashSet<String> stopWords = new HashSet<String>();
	static Hashtable<String, Integer> allWords = new Hashtable<String, Integer>();
	static BufferedWriter py;
	static int setId = 0;
	static int hash = 0;

	public static int[] findHash(ArrayList<String> file)
	{
		ArrayList<Integer> hashes = new ArrayList<Integer>();
		int idx = 0;

		for (String word : file)
		{
			if (Clusters.containsKey(word))
			{
				hashes.add(Clusters.get(word));
			}

			else
			{
				Clusters.put(word, setId);
				hashes.add(Clusters.get(word));
				setId++;
			}
		}

		int hash[] = new int[hashes.size()];
		for (Integer i : hashes)
		{
			hash[idx++] = i;
		}

		return hash;
	}

	public static void printHash(int[] h, BufferedWriter out)
	{
		try
		{
			for (Integer hashVal : h)
			{
				out.write(hashVal + " ");
			}

			out.write("\n");
		}

		catch (Exception e)
		{
			System.out.println("Error: " + e);
		}
	}

	public static String readFileAsString(String filePath) throws IOException
	{
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1)
		{
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	public static String formatInput(String fname) throws java.io.IOException
	{
		try
		{
			String buff = readFileAsString(fname);

			// replace tabs, newlines, carriage returns, ".", "," and "-" with
			// space
			buff = buff.replaceAll("[-\t\r\n.,\u2014]", " ");

			// convert everything to lower case
			buff = buff.toLowerCase();

			// remove all character except a-z
			buff = buff.replaceAll("[^a-z ]", "");

			// remove multiple spaces
			buff = buff.trim().replaceAll(" +", " ");

			return buff;
		}

		catch (Exception e)
		{
			System.out.println("Error: " + e);
			return null;
		}

	}

	public static void writeToFile(String fileName, String file)
	{
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			out.write(file);
			out.close();
		}

		catch (Exception e)
		{
			System.out.println("Error: " + e);
		}
	}

	public static int[] convertIntegers(List<Integer> integers)
	{
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	public static int findIntersectionSize(int[] a, int[] b)
	{
		int size = 0, idx_a = 0, idx_b = 0;
		Arrays.sort(a);
		Arrays.sort(b);

		// System.out.println();
		while (idx_a < a.length && idx_b < b.length)
		{
			if (a[idx_a] == b[idx_b])
			{
				// System.out.print(idx_a + ", ");
				// System.out.println(a[idx_a] + ",");
				size++;
				idx_a++;
				idx_b++;
			}

			else if (a[idx_a] < b[idx_b])
			{
				idx_a++;
			}

			else
			{
				idx_b++;
			}
		}

		return size;
	}

	public static void loadStopWordList(String fname)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(fname));

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
					stopWords.add(line);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e);
		}
	}

	public static ArrayList<String> removeStopWords(String file, String tagged, String lemmatized) throws IOException
	{
		file.trim();
		String[] words = file.split(" ");
		String[] pos_tagged_words = tagged.split(" ");
		String[] lemmatized_words = lemmatized.split(" ");
		ArrayList<String> wordsList = new ArrayList<String>();

		for (int i = 0; i < words.length; i++)
		{
			if (!(stopWords.contains(words[i])))
			{
				String lemma = lemmatized_words[i];
				String pos_tag = pos_tagged_words[i].split("_")[1];
				String new_word = lemma + "_" + pos_tag;

				if (!allWords.containsKey(new_word))
				{
					allWords.put(new_word, hash++);
					py.write(new_word + "\n");
				}

				wordsList.add(new_word);
			}
		}

		return wordsList;
	}

	public static void main(String[] args) throws IOException
	{
		// store the reference of print stream
		PrintStream err = System.err;

		// making all writes to the System.err stream silent
		System.setErr(new PrintStream(new OutputStream()
		{
			public void write(int b)
			{
			}
		}));

		String stopWordsFile = "stopwords.txt";
		loadStopWordList(stopWordsFile);

		// for lexical chain
		int lex_base = Integer.parseInt(args[0]);
		int lex_kgram = Integer.parseInt(args[1]);
		int lex_win_size = Integer.parseInt(args[2]);

		// for direct
		int dir_base = Integer.parseInt(args[3]);
		int dir_kgram = Integer.parseInt(args[4]);
		int dir_win_size = Integer.parseInt(args[5]);

		// threshold for agglomerative hierarchical clustering
		double threshold = Double.parseDouble(args[6]);

		boolean fp_opt = Boolean.parseBoolean(args[7]);

		MaxentTagger tagger = new MaxentTagger("models/wsj-0-18-left3words-distsim.tagger");
		StanfordLemmatizer slem = new StanfordLemmatizer();

		// open all required files
		File source = new File("source.txt");
		File sus = new File("copy.txt");
		BufferedWriter out = new BufferedWriter(new FileWriter("sample_output.txt"));
		py = new BufferedWriter(new FileWriter("input.txt"));

		// re-initialize all variables for next pair
		Clusters.clear();
		Clustering.maxClusId = 0;
		setId = 0;
		allWords.clear();
		hash = 1;

		// preprocess to remove punctuations and lower all cases
		String file1 = formatInput(source.getPath());
		// pos tagging
		String tagged1 = tagger.tagString(file1);
		// lemmetization
		String lemmatized1 = slem.lemmatize(file1);
		// remove stopwords and create final wordslist
		ArrayList<String> words1 = removeStopWords(file1, tagged1, lemmatized1);

		out.write(file1 + "\n\n");
		out.write(lemmatized1 + "\n\n");
		out.write(tagged1 + "\n\n");
		out.write(words1.toString() + "\n\n");

		// preprocess to remove punctuations and lower all cases
		String file2 = formatInput(sus.getPath());
		// pos tagging
		String tagged2 = tagger.tagString(file2);
		// lemmetization
		String lemmatized2 = slem.lemmatize(file2);
		// remove stopwords and create final wordslist
		ArrayList<String> words2 = removeStopWords(file2, tagged2, lemmatized2);

		out.write(file2 + "\n\n");
		out.write(lemmatized2 + "\n\n");
		out.write(tagged2 + "\n\n");
		out.write(words2.toString() + "\n\n");
		py.close();

		System.out.print(sus.getName() + "\t" + source.getName() + "\t");
		out.write(file1 + "\n\n");
		out.write(file2 + "\n\n");

		ArrayList<Integer> directFp, sortedFp;

		directFp = DirectFingerprint.findFingerPrint(words1, dir_win_size, dir_base, dir_kgram, fp_opt, allWords);
		sortedFp = DirectFingerprint.findSortedFingerPrint(words1, dir_win_size, dir_base, dir_kgram, fp_opt, allWords);

		int[] dfp1 = convertIntegers(directFp);
		int[] sdfp1 = convertIntegers(sortedFp);

		out.write("File 1 Direct Fingerprint: \n" + directFp + "\n\n");
		out.write("File 1 Direct Sorted Fingerprint: \n" + sortedFp + "\n\n");

		directFp.clear();
		sortedFp.clear();

		directFp = DirectFingerprint.findFingerPrint(words2, dir_win_size, dir_base, dir_kgram, fp_opt, allWords);
		sortedFp = DirectFingerprint.findSortedFingerPrint(words2, dir_win_size, dir_base, dir_kgram, fp_opt, allWords);

		int[] dfp2 = convertIntegers(directFp);
		int[] sdfp2 = convertIntegers(sortedFp);

		out.write("File 2 Direct Fingerprint: \n" + directFp + "\n\n");
		out.write("File 2 Direct Sorted Fingerprint: \n" + sortedFp + "\n\n");

		directFp.clear();
		sortedFp.clear();

		int dsmallerSize = dfp1.length < dfp2.length ? dfp1.length : dfp2.length;
		int dlargerSize = dfp1.length >= dfp2.length ? dfp1.length : dfp2.length;
		int dInteresectSize = findIntersectionSize(dfp1, dfp2);
		int dunionSize = dfp1.length + dfp2.length - dInteresectSize;

		out.write("Direct Size of intersection: " + dInteresectSize + "\n");
		out.write("Direct Size of union: " + dunionSize + "\n");
		out.write("Direct Size of smaller set: " + dsmallerSize + "\n");
		out.write("Direct Size of larger set: " + dlargerSize + "\n");
		out.write("Direct Jaccard: " + String.format("%.3f", (double) dInteresectSize / dunionSize) + "\n");
		out.write("Direct Jaccard with smaller set: " + String.format("%.3f", (double) dInteresectSize / dsmallerSize)
				+ "\n\n");

		System.out.printf(String.format("%.3f", (double) dInteresectSize / dsmallerSize) + "\t");

		int sdsmallerSize = sdfp1.length < sdfp2.length ? sdfp1.length : sdfp2.length;
		int sdlargerSize = sdfp1.length >= sdfp2.length ? sdfp1.length : sdfp2.length;
		int sdInteresectSize = findIntersectionSize(sdfp1, sdfp2);
		int sdunionSize = sdfp1.length + sdfp2.length - sdInteresectSize;

		out.write("Stats after sorting\n\n");
		out.write("Direct Size of intersection: " + sdInteresectSize + "\n");
		out.write("Direct Size of union: " + sdunionSize + "\n");
		out.write("Direct Size of smaller set: " + sdsmallerSize + "\n");
		out.write("Direct Size of larger set: " + sdlargerSize + "\n");
		out.write("Direct Jaccard: " + String.format("%.3f", (double) sdInteresectSize / sdunionSize) + "\n");
		out.write("Direct Jaccard with smaller set: "
				+ String.format("%.3f", (double) sdInteresectSize / sdsmallerSize) + "\n\n");

		System.out.printf(String.format("%.3f", (double) sdInteresectSize / sdsmallerSize) + "\t");

		// Clusters = Clustering.formClusters(threshold);

		out.write("\nCluster IDs\n" + Clusters + "\n\n");

		ArrayList<Integer> fp = new ArrayList<Integer>();
		ArrayList<Integer> sfp = new ArrayList<Integer>();

		setId = Clustering.maxClusId + 1;

		int[] hash1 = findHash(words1);
		out.write("File 1 hash sequence:\n\n");
		printHash(hash1, out);
		out.write("\n");

		int[] kgramHash1 = LexicalFingerprint.findKgramHashes(lex_base, lex_kgram, hash1);
		int[] sortedKgramHash1 = LexicalFingerprint.findSortedHashes(lex_base, lex_kgram, hash1);
		int[] fp1, sfp1;

		if (fp_opt)
		{
			fp = LexicalFingerprint.winnow(lex_win_size, kgramHash1);
			sfp = LexicalFingerprint.winnow(lex_win_size, sortedKgramHash1);
			fp1 = convertIntegers(fp);
			sfp1 = convertIntegers(sfp);
			out.write("File 1 Fingerprint: \n\n" + fp + "\n\n");
			out.write("File 1 Sorted Fingerprint: \n\n" + sfp + "\n\n");
		}

		else
		{
			fp1 = kgramHash1;
			sfp1 = sortedKgramHash1;

			out.write("File 1 kgrams: \n");
			for (int j = 0; j < fp1.length; j++)
			{
				out.write(fp1[j] + ", ");
			}

			out.write("\n\nFile 1 sorted kgrams: \n");
			for (int j = 0; j < sfp1.length; j++)
			{
				out.write(sfp1[j] + ", ");
			}
			out.write("\n\n");
		}

		fp.clear();
		sfp.clear();

		int[] hash2 = findHash(words2);
		out.write("File 2 hash sequence:\n\n");
		printHash(hash2, out);
		out.write("\n");

		int[] kgramHash2 = LexicalFingerprint.findKgramHashes(lex_base, lex_kgram, hash2);
		int[] sortedKgramHash2 = LexicalFingerprint.findSortedHashes(lex_base, lex_kgram, hash2);
		int[] fp2, sfp2;

		if (fp_opt)
		{
			fp = LexicalFingerprint.winnow(lex_win_size, kgramHash2);
			sfp = LexicalFingerprint.winnow(lex_win_size, sortedKgramHash2);
			fp2 = convertIntegers(fp);
			sfp2 = convertIntegers(sfp);
			out.write("File 2 Fingerprint: \n\n" + fp + "\n\n");
			out.write("File 2 Sorted Fingerprint: \n\n" + sfp + "\n");
		}

		else
		{
			fp2 = kgramHash2;
			sfp2 = sortedKgramHash2;

			out.write("File 2 kgrams: \n");
			for (int j = 0; j < fp2.length; j++)
			{
				out.write(fp2[j] + ", ");
			}

			out.write("\n\nFile 2 sorted kgrams: \n");
			for (int j = 0; j < sfp2.length; j++)
			{
				out.write(sfp2[j] + ", ");
			}
			out.write("\n\n");
		}

		fp.clear();
		sfp.clear();

		int smallerSize = fp1.length < fp2.length ? fp1.length : fp2.length;
		int largerSize = fp1.length >= fp2.length ? fp1.length : fp2.length;
		int InteresectSize = findIntersectionSize(fp1, fp2);
		int unionSize = fp1.length + fp2.length - InteresectSize;

		System.out.printf(String.format("%.3f", (double) InteresectSize / smallerSize) + "\t");

		out.write("\n");
		out.write("Size of intersection: " + InteresectSize + "\n");
		out.write("Size of union: " + unionSize + "\n");
		out.write("Size of smaller set: " + smallerSize + "\n");
		out.write("Size of larger set: " + largerSize + "\n");
		out.write("Jaccard: " + String.format("%.3f", (double) InteresectSize / unionSize) + "\n");
		out.write("Jaccard with smaller set: " + String.format("%.3f", (double) InteresectSize / smallerSize) + "\n");
		out.write("\n");

		int ssmallerSize = sfp1.length < sfp2.length ? sfp1.length : sfp2.length;
		int slargerSize = sfp1.length >= sfp2.length ? sfp1.length : sfp2.length;
		int sInteresectSize = findIntersectionSize(sfp1, sfp2);
		int sunionSize = sfp1.length + sfp2.length - sInteresectSize;

		System.out.printf(String.format("%.3f", (double) sInteresectSize / ssmallerSize) + "\t");

		out.write("Stats after sorting \n\n");
		out.write("Size of intersection: " + sInteresectSize + "\n");
		out.write("Size of union: " + sunionSize + "\n");
		out.write("Size of smaller set: " + ssmallerSize + "\n");
		out.write("Size of larger set: " + slargerSize + "\n");
		out.write("Jaccard: " + String.format("%.3f", (double) sInteresectSize / sunionSize) + "\n");
		out.write("Jaccard with smaller set: " + String.format("%.3f", (double) sInteresectSize / ssmallerSize) + "\n");

		int len1 = words1.size(), len2 = words2.size();
		System.out.print(words1.size() + "\t" + words2.size() + "\t");
		System.out.print(stringEditDistance(words1, words2) + "\t");
		System.out.print(findEditDistance(hash1, hash2) + "\t");
		int lc = lcs(words1, words2);
		int i_lc = intlcs(hash1, hash2);
		System.out.printf(lcs(words1, words2) + "\t" + String.format("%.3f", (double) (lc * 1.0 / len2)) + "\t");
		System.out.printf(intlcs(hash1, hash2) + "\t" + String.format("%.3f", (double) (i_lc * 1.0 / len2)) + "\n");

		ArrayList<ArrayList<String>> cl = new ArrayList<ArrayList<String>>();

		out.write("\n");
		for (int p = 0; p <= setId; p++)
		{
			ArrayList<String> arr = new ArrayList<String>();
			cl.add(arr);
		}

		for (String key : Clusters.keySet())
		{
			(cl.get(Clusters.get(key))).add(key);
		}

		for (int p = 0; p < cl.size(); p++)
		{
			if (cl.get(p).size() > 0)
			{
				out.write(p + ": ");
			}

			for (int j = 0; j < cl.get(p).size(); j++)
			{

				out.write(cl.get(p).get(j));
				if (j != cl.get(p).size() - 1)
				{
					out.write(", ");
				}
			}
			if (cl.get(p).size() > 0)
			{
				out.write("\n");
			}
		}

		out.close();
		System.out.println();

		// set everything bck to its original state afterwards
		System.setErr(err);
	}

	public static int intlcs(int[] X, int[] Y)
	{
		int m = X.length, n = Y.length;
		int L[][] = new int[m + 1][n + 1];
		int i, j;

		for (i = 0; i <= m; i++)
		{
			for (j = 0; j <= n; j++)
			{
				if (i == 0 || j == 0)
					L[i][j] = 0;

				else if (X[i - 1] == Y[j - 1])
					L[i][j] = L[i - 1][j - 1] + 1;

				else
					L[i][j] = max(L[i - 1][j], L[i][j - 1]);
			}
		}

		return L[m][n];
	}

	public static int lcs(ArrayList<String> X, ArrayList<String> Y)
	{
		int m = X.size(), n = Y.size();
		int L[][] = new int[m + 1][n + 1];
		int i, j;

		for (i = 0; i <= m; i++)
		{
			for (j = 0; j <= n; j++)
			{
				if (i == 0 || j == 0)
					L[i][j] = 0;

				else if (X.get(i - 1).equals(Y.get(j - 1)))
					L[i][j] = L[i - 1][j - 1] + 1;

				else
					L[i][j] = max(L[i - 1][j], L[i][j - 1]);
			}
		}

		return L[m][n];
	}

	public static int max(int i, int j)
	{
		// TODO Auto-generated method stub
		return i > j ? i : j;
	}

	public static int stringEditDistance(ArrayList<String> file1, ArrayList<String> file2)
	{
		// TODO Auto-generated method stub

		int[][] distance = new int[file1.size() + 1][file2.size() + 1];

		for (int i = 0; i <= file1.size(); i++)
		{
			distance[i][0] = i;
		}

		for (int j = 1; j <= file2.size(); j++)
		{
			distance[0][j] = j;
		}

		for (int i = 1; i <= file1.size(); i++)
		{
			for (int j = 1; j <= file2.size(); j++)
			{
				int cost = (file1.get(i - 1).equals(file2.get(j - 1))) ? 0 : 1;
				distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1] + cost);
			}
		}

		return distance[file1.size()][file2.size()];
	}

	public static int findEditDistance(int[] hash1, int[] hash2)
	{
		// TODO Auto-generated method stub

		int[][] distance = new int[hash1.length + 1][hash2.length + 1];

		for (int i = 0; i <= hash1.length; i++)
		{
			distance[i][0] = i;
		}

		for (int j = 1; j <= hash2.length; j++)
		{
			distance[0][j] = j;
		}

		for (int i = 1; i <= hash1.length; i++)
		{
			for (int j = 1; j <= hash2.length; j++)
			{
				int cost = (hash1[i - 1] == hash2[j - 1]) ? 0 : 1;
				distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1] + cost);
			}
		}

		return distance[hash1.length][hash2.length];
	}

	public static int minimum(int i, int j, int k)
	{
		return Math.min(i, Math.min(j, k));
	}

}