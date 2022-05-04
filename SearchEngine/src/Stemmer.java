
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
				if ("aeiouAEIOU".indexOf(beforeLetter) != -1)
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

	public static void main(String args[]) throws Exception {
		String a = "toy";
		Stemmer s = new Stemmer();
		boolean flag = s.endsWith(a,'s');
		System.out.println(flag);
				
		return;
	}
	
}
