package results;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Accuracy
{
	public static void main(String[] args) throws FileNotFoundException
	{
		ArrayList<String> cats = new ArrayList<String>();
		cats.add("non");
		cats.add("heavy");
		cats.add("light");
		cats.add("cut");

		Scanner in = new Scanner(new File("accuracy.txt"));

		String[] categ = new String[95];
		double[] sim = new double[95];
		int i = 0, j = 0;
		int count = 0, total = 0;

		while (in.hasNext())
		{
			categ[i++] = in.next();
			sim[j++] = Double.parseDouble(in.next());
			in.next();
		}

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

		System.out.println((double) count / total);
	}
}
