package results;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PrecisionRecall
{
	static ArrayList<String> cats = new ArrayList<String>();

	public static void main(String[] args) throws FileNotFoundException
	{
		cats.add("non");
		cats.add("heavy");
		cats.add("light");
		cats.add("cut");

		Scanner in = new Scanner(new File("baseline.txt"));

		String[] categ = new String[95];
		double[] sim = new double[95];
		double[] lcs = new double[95];
		double[] jac = new double[95];
		int i = 0;
		double alpha = 1, beta = 0;
		double bestA = 0, bestB = 0;
		double[] best_setting = new double[4];
		double[] setting = new double[4];

		while (in.hasNext())
		{
			categ[i] = in.next();
			jac[i] = Double.parseDouble(in.next());
			// lcs[i] = Double.parseDouble(in.next());
			i++;
		}

		while (alpha == 1)
		{
			beta = 1 - alpha;
			for (int k = 0; k < sim.length; k++)
			{
				sim[k] = alpha * jac[k] + beta * lcs[k];
			}

			setting = findBestF1(sim, categ);

			if (setting[3] > best_setting[3])
			{
				best_setting = setting.clone();
				bestA = alpha;
				bestB = beta;
			}

			// System.out.println(alpha + "\t" + beta + "\t" + acc);
			alpha = alpha + 0.01;
		}

		// System.out.println("\nthreshold: " + best_thresh + "\trecall: " +
		// maxRecall + "\tprecision: " + prec_rec
		// + "\tf1: " + best_f1);

		System.out.println(bestA + "\t" + bestB);
		System.out.println("t: " + best_setting[0] + "\tp: " + best_setting[1] + "\tr: " + best_setting[2] + "\tf: "
				+ best_setting[3]);
	}

	public static double[] findBestF1(double[] sim, String[] categ)
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

		return settings.clone();
	}

}