package detection;

import java.util.*;

public class Confusion
{
	static ArrayList<String> cats = new ArrayList<String>();
	static int[][] confusionMatrix = new int[4][4];
	static int[][] bestConfusionMatrix = new int[4][4];
	static double[] precision = new double[4];
	static double[] bestF1Precision = new double[4];
	static double[] recall = new double[4];
	static double[] bestF1Recall = new double[4];
	static double avgPrecision = 0;
	static double avgRecall = 0;
	static double f1 = 0;

	public static int[][] findConfusionMatrix(Hashtable<String, Double> sim, Hashtable<String, String> categories)
	{
		cats.add("non");
		cats.add("heavy");
		cats.add("light");
		cats.add("cut");

		double threshold[] = new double[3];
		threshold[0] = 0;

		double a = 0, b = 0, c = 0;
		double f1 = 0, bestF1 = 0;

		while (threshold[0] < 1.0)
		{
			threshold[1] = threshold[0] + 0.005;

			while (threshold[1] < 1.0)
			{
				threshold[2] = threshold[1] + 0.005;

				while (threshold[2] < 1.0)
				{
					buildConfusionMatrix(threshold[0], threshold[1], threshold[2], sim, categories);
					f1 = findMacroMeasures();

					if (f1 > bestF1)
					{
						bestF1 = f1;
						bestF1Precision = precision.clone();
						bestF1Recall = recall.clone();
						bestConfusionMatrix = confusionMatrix.clone();
						a = threshold[0];
						b = threshold[1];
						c = threshold[2];
					}

					threshold[2] = threshold[2] + 0.005;
				}

				threshold[1] = threshold[1] + 0.005;
			}

			threshold[0] = threshold[0] + 0.005;
		}

		System.out.println("Best F1: " + bestF1 + "\ta: " + a + "\tb: " + b + "\tc: " + c);

		for (int i = 0; i < precision.length; i++)
		{
			double pre = bestF1Precision[i], rec = bestF1Recall[i];
			System.out.print(cats.get(i) + "\t" + pre + "\t" + rec);
			double f = (2 * pre * rec) / (pre + rec);
			System.out.println(f);
		}

		System.out.println();

		printConfusionMatrix();

		return confusionMatrix;
	}

	public static void printConfusionMatrix()
	{
		for (int i = 0; i < bestConfusionMatrix.length; i++)
		{
			for (int j = 0; j < bestConfusionMatrix.length; j++)
			{
				System.out.print(bestConfusionMatrix[i][j] + "\t");
			}

			System.out.println();
		}
	}

	public static void buildConfusionMatrix(double a, double b, double c, Hashtable<String, Double> sim,
			Hashtable<String, String> categories)
	{
		int x = 0;
		double similarity = 0;
		confusionMatrix = new int[4][4];

		for (String doc : sim.keySet())
		{
			similarity = sim.get(doc);

			if (similarity < a)
			{
				x = 0;
			}

			else if ((similarity >= a) && (similarity < b))
			{
				x = 1;
			}

			else if ((similarity >= b) && (similarity < c))
			{
				x = 2;
			}

			else
			{
				x = 3;
			}

			if (categories.get(doc).equals(cats.get(0)))
			{
				confusionMatrix[0][x]++;
			}

			else if (categories.get(doc).equals(cats.get(1)))
			{
				confusionMatrix[1][x]++;
			}

			else if (categories.get(doc).equals(cats.get(2)))
			{
				confusionMatrix[2][x]++;
			}

			else
			{
				confusionMatrix[3][x]++;
			}
		}
	}

	public static double findMacroMeasures()
	{
		double f1 = 0;
		avgPrecision = 0;
		avgRecall = 0;

		for (int i = 0; i < confusionMatrix.length; i++)
		{
			int actual = 0, labelled = 0;

			for (int j = 0; j < confusionMatrix.length; j++)
			{
				actual += confusionMatrix[i][j];
				labelled += confusionMatrix[j][i];
			}

			precision[i] = (double) confusionMatrix[i][i] / labelled;
			recall[i] = (double) confusionMatrix[i][i] / actual;
		}

		for (int i = 0; i < precision.length; i++)
		{
			avgPrecision += precision[i];
			avgRecall += recall[i];
		}

		avgPrecision /= 4;
		avgRecall /= 4;
		f1 = (2 * avgPrecision * avgRecall) / (avgPrecision + avgRecall);

		return f1;
	}
}