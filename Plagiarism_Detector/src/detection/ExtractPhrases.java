package detection;

import java.io.PrintStream;
import java.util.*;

import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

class ExtractPhrases
{
	public static ArrayList<String> Parse(String file)
	{
		// this is your print stream, store the reference
		PrintStream err = System.err;

		// now make all writes to the System.err stream silent
		System.setErr(new PrintStream(new OutputStream()
		{
			public void write(int b)
			{
			}

			@Override
			public InputStream create_input_stream()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void write_Object(Object value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_TypeCode(TypeCode value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_any(Any value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_boolean(boolean value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_boolean_array(boolean[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_char(char value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_char_array(char[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_double(double value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_double_array(double[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_float(float value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_float_array(float[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_long(int value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_long_array(int[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_longlong(long value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_longlong_array(long[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_octet(byte value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_octet_array(byte[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_short(short value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_short_array(short[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_string(String value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_ulong(int value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_ulong_array(int[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_ulonglong(long value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_ulonglong_array(long[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_ushort(short value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_ushort_array(short[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_wchar(char value)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_wchar_array(char[] value, int offset, int length)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void write_wstring(String value)
			{
				// TODO Auto-generated method stub

			}
		}));

		ArrayList<String> phrases = new ArrayList<String>();
		LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		lp.setOptionFlags(new String[] { "-maxLength", "80", "-retainTmpSubcategories" });

		String[] sentences = file.split("\n|\\.(?!\\d)|(?<!\\d)\\.");

		for (int i = 0; i < sentences.length; i++)
		{
			String[] sent = sentences[i].split(" ");
			List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
			Tree parse = lp.apply(rawWords);
			TregexPattern NPpattern = TregexPattern.compile("@NP !<< @NP");
			TregexMatcher matcher = NPpattern.matcher(parse);

			while (matcher.findNextMatchingNode())
			{
				Tree match = matcher.getMatch();
				String np = Sentence.listToString(match.yield()).trim();
				String[] words = np.split(" ");
				String npWithoutStops = "";
				int numOfWords = 0;

				for (int j = 0; j < words.length; j++)
				{
					if (!Test.stopWords.contains(words[j]))
					{
						npWithoutStops = npWithoutStops + words[j] + " ";
						numOfWords++;
					}
				}

				npWithoutStops = npWithoutStops.trim();
				if (numOfWords > 1 && numOfWords < 4)
				{
					phrases.add(npWithoutStops);
				}
			}
		}

		// set everything back to its original state afterwards
		System.setErr(err);

		for (String ph : phrases)
		{
			System.out.println(ph);
		}

		return phrases;
	}
}
