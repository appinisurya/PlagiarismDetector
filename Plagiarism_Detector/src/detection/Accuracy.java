package detection;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class Accuracy
{
	static ArrayList<String> cats = new ArrayList<String>();

	@SuppressWarnings("resource")
	public static double[] CalcAccuracy(Hashtable<String, Double> jacSim, Hashtable<String, Double> lcs,
			Hashtable<String, String> categories) throws IOException
	{
		cats.add("non");
		cats.add("heavy");
		cats.add("light");
		cats.add("cut");

		int i = 0;
		double pairWiseAcc = 0;
		double alpha = 0, beta = 0;
		double bestA = 0, bestB = 0;
		double[] setting = new double[4];
		double[] best_setting = new double[4];
		double[] sim = new double[95];
		String[] categ = new String[95];

		while (alpha < 1)
		{
			beta = 1 - alpha;

			i = 0;

			for (String doc : jacSim.keySet())
			{
				sim[i] = alpha * jacSim.get(doc) + beta * lcs.get(doc);
				categ[i] = categories.get(doc);
				i++;
			}

			setting = FindBestF1(sim, categ);

			if (setting[3] > best_setting[3])
			{
				best_setting = setting.clone();
				bestA = alpha;
				bestB = beta;
				pairWiseAcc = PairWiseAccuracy(categ, sim);
			}

			alpha = alpha + 0.05;
		}

		System.out.println("F1 best: " + best_setting[3] + "\talpha: " + bestA + "\tbeta: " + bestB + "\tpairWiseAcc: "
				+ pairWiseAcc);

		System.out.println("Threshold: " + best_setting[0] + "\tPrecision: " + best_setting[1] + "\tRecall: "
				+ best_setting[2]);

		for (String doc : jacSim.keySet())
		{
			Test.combinedSimilarities.put(doc, bestA * jacSim.get(doc) + bestB * lcs.get(doc));
		}

		return best_setting;
	}

	public static double PairWiseAccuracy(String[] categ, double[] sim)
	{
		int count = 0, total = 0, i = 0, j = 0;

		for (i = 0; i < categ.length; i++)
		{
			for (j = i + 1; j < categ.length; j++)
			{
				if (categ[i] != categ[j])
				{
					total++;

					if (cats.indexOf(categ[i]) < cats.indexOf(categ[j]))
					{
						if (sim[i] <= sim[j])
						{
							count++;
						}
					}

					else if (cats.indexOf(categ[i]) > cats.indexOf(categ[j]))
					{
						if (sim[i] >= sim[j])
						{
							count++;
						}
					}
				}
			}
		}

		return ((double) count / total);
	}

	public static double[] FindBestF1(double[] sim, String[] categ)
	{
		double f1 = 0.0, threshold = 0, precision = 0.0, recall = 0.0;
		double best_f1 = 0.0;
		double[] settings = new double[4];

		while (threshold < 1.0)
		{
			int tp = 0, fp = 0, tn = 0, fn = 0;

			for (int j = 0; j < sim.length; j++)
			{
				if (sim[j] > threshold)
				{
					if (!categ[j].equals(cats.get(0)))
					{
						tp++;
					}

					else
					{
						fp++;
					}
				}

				else if (categ[j].equals(cats.get(0)))
				{
					tn++;
				}

				else
				{
					fn++;
				}

			}

			// System.out.println(tp + "\t" + fp + "\t" + tn + "\t" + fn);
			precision = (double) tp / (tp + fp);
			recall = (double) tp / (tp + fn);
			f1 = (double) (2 * precision * recall) / (precision + recall);
			// System.out.println(precision + "\t" + recall + "\t" + f1);

			if (f1 > best_f1)
			{
				settings[0] = threshold;
				settings[1] = precision;
				settings[2] = recall;
				settings[3] = f1;
				best_f1 = f1;
			}

			threshold = threshold + 0.0001;
		}

		// returns threshold, precision, recall and f1 in that order
		return settings.clone();
	}

	public static void main(String[] args)
	{

	}
}