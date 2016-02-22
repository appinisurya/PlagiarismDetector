package results;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AccuracyWithFeatures
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
		double[] edit = new double[95];
		double[] jac = new double[95];
		int i = 0;
		double alpha = 0, beta = 0;
		double bestA = 0, bestB = 0, max = 0.0, acc = 0.0;

		while (in.hasNext())
		{
			categ[i] = in.next();
			jac[i] = Double.parseDouble(in.next());
			edit[i] = Double.parseDouble(in.next());
			i++;
		}

		alpha = 1;
		while (alpha == 1)
		{
			beta = 1 - alpha;
			for (int k = 0; k < sim.length; k++)
			{
				sim[k] = alpha * jac[k] + beta * edit[k];
			}

			acc = accuracy(categ, sim);
			if (acc > max)
			{
				max = acc;
				bestA = alpha;
				bestB = beta;
			}

			System.out.println(alpha + "\t" + beta + "\t" + acc);
			alpha = alpha + 0.05;
		}

		System.out.println(max + "\t" + bestA + "\t" + bestB);
	}

	public static double accuracy(String[] categ, double[] sim)
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
}