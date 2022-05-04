import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stemmer {
	
	//Checks if the character in a word is consonant or not
	public Boolean isConsonant(String word, int index)
	{
		char letter = word.charAt(index);
		if ("aeiouAEIOU".indexOf(letter) != -1)
		{
			return false;
		}
		else
		{
			//Vowel = consonant +y
			if(letter == 'y' || letter == 'Y')
			{
				char beforeLetter = word.charAt(index-1);
				if ("aeiouAEIOU".indexOf(beforeLetter) == -1)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	//Checks if the character is vowel
	public Boolean isVowel(String word, int index)
	{
		char letter = word.charAt(index);
		if ("aeiouAEIOU".indexOf(letter) != -1)
		{
			return true;
		}
		return false;
	
	}
	
	//Checks if a word ends with a specific letter
	public Boolean endsWith(String word, char letter)
	{
		if(word.indexOf(letter) == word.length()-1)
		{
			return true;
		}
		return false;
	}
	
	//Checks if a word has a vowel
	public Boolean hasVowel(String word)
	{
		for(int i = 0;i<word.length();i++)
		{
			char letter = word.charAt(i);
			if ("aeiouAEIOU".indexOf(letter) != -1)
			{
				return true;
			}
		}
		return false;
	}
	
	//Checks if a word ends with 2 consonants
	public Boolean endsWithDoubleCons(String word)
	{
		if(word.length()>=2 && isConsonant(word, word.length()-1) && isConsonant(word, word.length()-2))
		{
			return true;
		}
		return false;
	}
	
	//Counts the occurrences of a substring within a string
	public int countMatches(String text, String str)
    {
        if (text.length() == 0 || str.length() == 0) {
            return 0;
        }
 
        int index = 0, count = 0;
        while (true)
        {
            index = text.indexOf(str, index);
            if (index != -1)
            {
                count ++;
                index += str.length();
            }
            else {
                break;
            }
        }
 
        return count;
    }
	//Gets the sequence of [C](CV)^m[V] according to Porter Stemmer algorithm and calculates the m value
	public int calcM(String word)
	{
		String seqStr = "";
		String seq = "";
		String[] seqArray = {};
		List<String>seqList = new ArrayList<String>(Arrays.asList(seqArray)); 
		for (int i = 0 ;i<word.length();i++)
		{
			if(isConsonant(word,i))
			{
				if(i != 0)
				{
					String prevSeq = seqList.get(seqList.size() - 1);
					if (prevSeq != "C")
					{
						seqList.add("C");
					}
					
				}
				else
				{
					seqList.add("C");
				}
			}
			else
			{
				if(i != 0)
				{
					String prevSeq = seqList.get(seqList.size() - 1);
					if (prevSeq != "V")
					{
						seqList.add("V");
					}
					
				}
				else
				{
					seqList.add("V");
				}
			}
		}
		 seq = String.join(seqStr, seqList);
		 int m = countMatches(seq, "VC");
		 return m;
	}
	//Checks if the last letter w,x,y -> false, returns true if last 3 letters follow the pattern : cons, vowel, cons
	public Boolean checkPattern(String word)
	{
		if(word.length()>=3)
		{
			if(isConsonant(word,word.length()-1) && isVowel(word,word.length()-2) && isConsonant(word,word.length()-3))
			{
				if(word.charAt(word.length()-1) != 'w' && word.charAt(word.length()-1) != 'x' && word.charAt(word.length()-1) != 'y')
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	public static void main(String args[]) throws Exception {
		String a = "ballpow";
		Stemmer s = new Stemmer();
		boolean m = s.checkPattern(a);
		System.out.println(m);
				
		return;
	}
	
}
