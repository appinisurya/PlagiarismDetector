package results;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class OrderResults
{
	public static void main(String[] args) throws FileNotFoundException
	{
		HashMap<String, String> output = new HashMap<String, String>();
		ArrayList<String> keys = new ArrayList<String>();

		Scanner in = new Scanner(new File("output.txt"));

		while (in.hasNext())
		{
			String fileName = in.next();
			keys.add(fileName);

			String src = in.next();
			String val1 = in.next();
			String val2 = in.next();
			String val3 = in.next();
			String val4 = in.next();
			String val5 = in.next();
			String val6 = in.next();
			String val7 = in.next();
			String val8 = in.next();
			String val9 = in.next();
			String val10 = in.next();
			String val11 = in.next();
			String val12 = in.next();

			output.put(fileName, val1 + "," + val2 + "," + val3 + "," + val4 + "," + val5 + "," + val6 + "," + val7
					+ "," + val8 + "," + val9 + "," + val10 + "," + val11 + "," + val12);
		}

		Collections.sort(keys);

		for (String key : keys)
		{
			System.out.println(output.get(key));
		}
	}
}