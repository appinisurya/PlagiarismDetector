package detection;

import java.util.*;

public class DirectFingerprint
{
	static Hashtable<String, Integer> wordsList = new Hashtable<String, Integer>();
	static int hashval = 0;

	public static ArrayList<Integer> winnow(int w, int[] hash_values)
	{
		ArrayList<Integer> fingerprint = new ArrayList<Integer>();

		int[] hash_table = new int[w];

		for (int i = 0; i < w; i++)
		{
			hash_table[i] = -1;
		}

		int right_end = 0;
		int min_hash_index = 0;
		int hash_idx = 0;

		while (hash_idx < hash_values.length)
		{
			right_end = (right_end + 1) % w;
			hash_table[right_end] = hash_values[hash_idx];

			if (min_hash_index == right_end)
			{
				for (int i = (right_end - 1 + w) % w; i != right_end; i = (i - 1 + w) % w)
				{
					if (hash_table[i] < hash_table[min_hash_index])
					{
						min_hash_index = i;
					}
				}

				fingerprint.add(hash_table[min_hash_index]);
			}

			else
			{
				if (hash_table[right_end] <= hash_table[min_hash_index])
				{
					min_hash_index = right_end;
					fingerprint.add(hash_table[min_hash_index]);
				}
			}

			hash_idx++;
		}

		return fingerprint;
	}

	public static int[] findFileHash(ArrayList<String> file)
	{
		int[] hash = new int[file.size()];
		int idx = 0;

		for (String str : file)
		{
			hash[idx++] = wordsList.get(str);
		}

		return hash;
	}

	public static int findHashValue(int[] kgram, int k, int base)
	{
		int hash = 0;

		for (int i = 0; i < kgram.length; i++)
		{
			hash += (Math.pow(base, k - i) * kgram[i]);
		}

		return hash;
	}

	public static int[] findSortedHashes(int base, int k, int[] hashes)
	{
		int size = Math.max(1, hashes.length - k + 1);
		int hash = 0;
		int[] kgram = null;
		kgram = new int[Math.min(k, hashes.length)];

		for (int i = 0; i < Math.min(k, hashes.length); i++)
		{
			kgram[i] = hashes[i];
		}

		Arrays.sort(kgram);
		hash = findHashValue(kgram, k, base);

		int[] sortedKgramHashes = new int[size];
		sortedKgramHashes[0] = hash;

		for (int i = 1; i < hashes.length - k + 1; i++)
		{
			kgram = new int[k];
			for (int j = 0; j < k; j++)
			{
				kgram[j] = hashes[i + j];
			}
			Arrays.sort(kgram);

			for (int j = 0; j < k; j++)
			{
				// System.out.print(kgram[j] + ",");
			}

			sortedKgramHashes[i] = findHashValue(kgram, k, base);
			// System.out.println(": " + sortedKgramHashes[i]);
		}

		return sortedKgramHashes;
	}

	public static int[] findKgramHashes(int base, int k, int[] hashes)
	{
		int size = hashes.length - k + 1;
		int hash = 0;

		if (size < 0)
		{
			size = 1;
			for (int i = 0; i < hashes.length; i++)
			{
				hash += (Math.pow(base, k - i) * hashes[i]);
			}
		}

		else
		{
			for (int i = 0; i < k; i++)
			{
				hash += (Math.pow(base, k - i) * hashes[i]);
			}
		}

		int[] kgramHashes = new int[size];
		kgramHashes[0] = hash;

		// System.out.println();

		for (int i = 1; i < hashes.length - k + 1; i++)
		{
			hash = ((hash - hashes[i - 1] * (int) Math.pow(base, k)) + hashes[i + k - 1]) * base;
			for (int j = 0; j < k; j++)
			{
				// System.out.print(hashes[i + j] + ",");
			}
			kgramHashes[i] = hash;
			// System.out.println(": " + hash);
		}

		// System.out.println();

		return kgramHashes;
	}

	public static ArrayList<Integer> findFingerPrint(ArrayList<String> file, int winsize, int base, int k, boolean fp,
			Hashtable<String, Integer> wordHash)
	{
		wordsList = (Hashtable<String, Integer>) wordHash.clone();

		int[] hash_values = findFileHash(file);
		int[] kgramHashes = findKgramHashes(base, k, hash_values);

		if (fp)
		{
			return winnow(winsize, kgramHashes);
		}

		else
		{
			ArrayList<Integer> kgrams = new ArrayList<Integer>();
			for (int i = 0; i < kgramHashes.length; i++)
			{
				System.out.println("outout");
				kgrams.add(kgramHashes[i]);
			}
			return kgrams;
		}
	}

	public static ArrayList<Integer> findSortedFingerPrint(ArrayList<String> file, int winsize, int base, int k,
			boolean fp, Hashtable<String, Integer> wordHash)
	{
		wordsList = (Hashtable<String, Integer>) wordHash.clone();
		int[] hash_values = findFileHash(file);
		int[] sortedHashes = findSortedHashes(base, k, hash_values);

		if (fp)
		{
			return winnow(winsize, sortedHashes);
		}

		else
		{
			ArrayList<Integer> sortedGrams = new ArrayList<Integer>();
			for (int i = 0; i < sortedHashes.length; i++)
			{
				sortedGrams.add(sortedHashes[i]);
			}

			return sortedGrams;
		}

	}
}