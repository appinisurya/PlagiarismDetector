package results;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Confusion
{
	static ArrayList<String> cats = new ArrayList<String>();

	public static void main(String[] args) throws FileNotFoundException
	{
		cats.add("non");
		cats.add("heavy");
		cats.add("light");
		cats.add("cut");

		Scanner in = new Scanner(new File("accuracy.txt"));

		String[] categ = new String[95];
		double[] sim = new double[95];
		double[] lcs = new double[95];
		double[] jac = new double[95];
		int i = 0;

		while (in.hasNext())
		{
			categ[i] = in.next();
			jac[i] = Double.parseDouble(in.next());
			lcs[i] = Double.parseDouble(in.next());
			i++;
		}

		double threshold[] = new double[3];
		threshold[0] = 0;
		threshold[1] = 0.001;
		threshold[2] = 0.002;
		double max = 0;
		double a = 0, b = 0, c = 0;
		double alpha = 0.28, beta = 0.72;
		double bestA = 0, bestB = 0;

		// while (alpha < 1)
		// {
		// beta = 1 - alpha;

		for (int k = 0; k < sim.length; k++)
		{
			sim[k] = alpha * jac[k] + beta * lcs[k];
		}

		threshold[0] = 0;
		threshold[1] = 0.001;
		threshold[2] = 0.002;

		while (threshold[0] < 0.5)
		{
			threshold[1] = threshold[0] + 0.01;

			while (threshold[1] < 0.8)
			{
				threshold[2] = threshold[1] + 0.01;

				while (threshold[2] < 1.0)
				{
					double acc = findAcc(threshold, sim, categ);

					if (acc > max)
					{
						max = acc;
						a = threshold[0];
						b = threshold[1];
						c = threshold[2];
						bestA = alpha;
						bestB = beta;
					}

					threshold[2] = threshold[2] + 0.02;
				}

				threshold[1] = threshold[1] + 0.02;
			}

			threshold[0] = threshold[0] + 0.01;
		}

		// alpha = alpha + 0.01;
		// }

		System.out.println(max + "\t" + a + "\t" + b + "\t" + c);
		printConfusionMatrix(a, b, c, sim, categ);
	}

	public static void printConfusionMatrix(double a, double b, double c, double[] sim, String[] categ)
	{
		int[][] conf = new int[4][4];
		int x = 0;

		for (int i = 0; i < sim.length; i++)
		{
			if (sim[i] < a)
			{
				x = 0;
			}

			else if ((sim[i] > a) && (sim[i] < b))
			{
				x = 1;
			}

			else if ((sim[i] > b) && (sim[i] < c))
			{
				x = 2;
			}

			else
			{
				x = 3;
			}

			if (categ[i].equals(cats.get(0)))
			{
				conf[x][0]++;
			}

			else if (categ[i].equals(cats.get(1)))
			{
				conf[x][1]++;
			}

			else if (categ[i].equals(cats.get(2)))
			{
				conf[x][2]++;
			}

			else
			{
				conf[x][3]++;
			}
		}

		for (int i = 0; i < conf.length; i++)
		{
			for (int j = 0; j < conf.length; j++)
			{
				System.out.print(conf[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println();
	}

	public static double findAcc(double[] t, double[] sim, String[] categ)
	{
		double acc = 0;
		int correct = 0;
		int total = 95;

		for (int i = 0; i < sim.length; i++)
		{
			if ((sim[i] < t[0]) && (categ[i].equals(cats.get(0))))
			{
				correct++;
			}

			else if ((sim[i] > t[0]) && (sim[i] < t[1]) && (categ[i].equals(cats.get(1))))
			{
				correct++;
			}

			else if ((sim[i] > t[1]) && (sim[i] < t[2]) && (categ[i].equals(cats.get(2))))
			{
				correct++;
			}

			else if ((sim[i] > t[2]) && (categ[i].equals(cats.get(3))))
			{
				correct++;
			}
		}

		acc = (double) correct / total;
		return acc;
	}
}