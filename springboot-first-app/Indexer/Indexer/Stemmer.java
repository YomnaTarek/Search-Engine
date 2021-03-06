package Indexer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.Console;
import java.lang.reflect.*;
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
			if((letter == 'y' || letter == 'Y') && index != 0)
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
		if(word.lastIndexOf(letter) == word.length()-1)
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
	//Checks if the oldWord contains a certain ending, if it does, it replaces the ending with the passed replacement and returns the newWord
	//Else returns oldWord
	public String replace(String oldWord, String ending, String replacement)
	{
		boolean ends = oldWord.endsWith(ending);
		if(ends)
		{
			String subString = oldWord.substring(0,oldWord.length()-ending.length());
			String newWord = subString.concat(replacement);
			return newWord;
		}
		else
		{
			
			return oldWord;
		}
	}
	//Same as previous function but checks on the m value>0
	public String replaceM0(String oldWord, String ending, String replacement)
	{
		boolean ends = oldWord.endsWith(ending);
		if(ends)
		{
			String subString = oldWord.substring(0,oldWord.length()-ending.length());
			if(calcM(subString)>0)
			{
				String newWord = subString.concat(replacement);
				return newWord;
			}
			return oldWord;
			
		}
		else
		{
			return oldWord;
		}
	}
	//Same as previous function but checks on the m value >1
	public String replaceM1(String oldWord, String ending, String replacement)
	{
		boolean ends = oldWord.endsWith(ending);
		if(ends)
		{
			String subString = oldWord.substring(0,oldWord.length()-ending.length());
			if(calcM(subString)>1)
			{
				String newWord = subString.concat(replacement);
				return newWord;
			}
			return oldWord;
			
		}
		else
		{
			return oldWord;
		}
	}
	//Getting rid of plurals
	public String step1A(String word)
	{
		if(word.endsWith("sses"))
		{
			word =  replace(word, "sses", "ss");
		}
		else if(word.endsWith("ies"))
		{
			word =  replace(word, "ies", "i");
		}
		else if(word.endsWith("ss"))
		{
			word = replace(word, "ss", "ss");
		}
		else if(word.endsWith("s"))
		{
			word = replace(word, "s", "");
		}
		else
		{
			return word;
		}
		return word;
	}

	//handles words ending with eed,ed,ing.
	public String step1b(String word) {
			
		//flag to check it the second or the third conditions are true.
		boolean secondThird=false;
		
		//FIRST CASE:
		//if the word ends with EED and m>0 for the substring before EED, replace EED with EE
		if(word.endsWith("eed"))
		{
			int index =word.length()-3;
			String stringBeforeEED=word.substring(0, index);
			if(calcM(stringBeforeEED)>0)
            {
				word=stringBeforeEED.concat("ee");
            }
		}
		//SECOND CASE:
		//if word ends with ED and the substring before ED has a vowel, remove ED from the word.
		else if(word.endsWith("ed"))
		{		
			int index2 =word.length()-2;
			String stringBeforeED=word.substring(0, index2);
			if(hasVowel(stringBeforeED))
			{
				word=stringBeforeED;
				secondThird=true;
			}
		}
		//THIRD CASE:
		//if word ends with ING and the substring before ING has a vowel, remove ING from the word.
		else if(word.endsWith("ing"))
		{
			int index3 =word.length()-3;
			String stringBeforeING=word.substring(0, index3);
			if(hasVowel(stringBeforeING))
			{
				word=stringBeforeING;
				secondThird=true;
			}
			//System.out.println("step1b"+word);
		}
		
		
		//if the second or third cases are true:
		if(secondThird)
		{
			//if the word ends with "at" or "bl" or "iz" add 'e' --->ate, ble, ize
			if(word.endsWith("at") || word.endsWith("bl") || word.endsWith("iz"))
			{
				word=word.concat("e");
			}
			//if the word ends with 2 consonants and its does not end with 'l' or 's' or 'z', remove last letter.
			else if(endsWithDoubleCons(word) && !endsWith(word,'z') && !endsWith(word,'s') && !endsWith(word,'l'))
			{
				word=word.substring(0,word.length()-1);
			}
			//if the m(word)=1 and the cvc(word) is true, add the letter 'e' to the word. 
			else if(calcM(word)==1 && checkPattern(word))
			{
				word=word.concat("e");
			}
		}

		return word;
			
	
	}
	
	
	//handels words ending with 'y' and having a vowel in the substring before 'y'.
	public String step1c(String word)
	{
		if(endsWith(word,'y'))
		{
			int indexStep1c =word.length()-1;
			String stringBeforeY=word.substring(0, indexStep1c);
			if(hasVowel(stringBeforeY))
			{
				word=stringBeforeY.concat("i"); //replace 'y' with 'i'
			}
		}

		return word;
	}

	//handles changing words with double suffixes. 
	//changes double suffixes to single suffixes, if m of the substring before the double suffix is >0
	public String step2(String word)
	{
           
		//FIRST CASE: (m>0)ational --->ate
		if(word.endsWith("ational"))
		{
          word=replaceM0(word,"ational","ate");
		}
		
		//SECOND CASE: (m>0)tional --->tion
		else if(word.endsWith("tional"))
		{
			word=replaceM0(word,"tional","tion");
		}
		
		//THIRD CASE: (m>0)enci --->ence
		else if(word.endsWith("enci"))
		{
		    word=replaceM0(word,"enci","ence");
		}
		//FOURTH CASE: (m>0)anci --->ance
		else if(word.endsWith("anci"))
		{
		    word=replaceM0(word,"anci","ance");
		}
		//FIFTH CASE: (m>0)izer --->ize
		else if(word.endsWith("izer"))
		{
			word=replaceM0(word,"izer","ize");
		}
		//SIXTH CASE: (m>0)abli --->able
		else if(word.endsWith("abli"))
		{
			word=replaceM0(word,"abli","able");
		}
		//SEVENTH CASE: (m>0)alli --->al
		else if(word.endsWith("alli"))
		{
			word=replaceM0(word,"alli","al");
		}
	    //EIGHTH CASE: (m>0)entli --->ent
		else if(word.endsWith("entli"))
		{
			word=replaceM0(word,"entli","ent");
		}
		//NINETH CASE: (m>0)eli --->e
		else if(word.endsWith("eli"))
		{
			word=replaceM0(word,"eli","e");
		}
		//TENTH CASE: (m>0)ousli --->ous
		else if(word.endsWith("ousli"))
		{
			word=replaceM0(word,"ousli","ous");
		}
		//ELEVENTH CASE: (m>0)ization --->ize
		else if(word.endsWith("ization"))
		{
			word=replaceM0(word,"ization","ize");
		}
		//TWELVETH CASE: (m>0)ation --->ate
		else if(word.endsWith("ation"))
		{
			word=replaceM0(word,"ation","ate");
		}
		//THIRTEENTH CASE: (m>0)ator --->ate
		else if(word.endsWith("ator"))
		{
			word=replaceM0(word,"ator","ate");
		}
		//FOURTEENTH CASE: (m>0)alism --->al
		else if(word.endsWith("alism"))
		{
			word=replaceM0(word,"alism","al");
		}
		//FIFTEENTH CASE: (m>0)iveness --->ive
		else if(word.endsWith("iveness"))
		{
			word=replaceM0(word,"iveness","ive");
		}
		//SIXTEENTH CASE: (m>0)fulness --->ful
		else if(word.endsWith("fulness"))
		{
			word=replaceM0(word,"fulness","ful");
		}		
       //SEVENTEENTH CASE: (m>0)ousness --->ous
		else if(word.endsWith("ousness"))
		{
			word=replaceM0(word,"ousness","ous");
		}
		//EIGHTEENTH CASE: (m>0)aliti --->al
		else if(word.endsWith("aliti"))
		{
			word=replaceM0(word,"aliti","al");
		}
		//NINETEENTH CASE: (m>0)iviti --->ive
		else if(word.endsWith("iviti"))
		{
			word=replaceM0(word,"iviti","ive");
		}
		//TWENTIETH CASE: (m>0)biliti --->ble
		else if(word.endsWith("biliti"))
		{
			word=replaceM0(word,"biliti","ble");
		}
		
		return word;
	}

	//changes the suffixes if the substring before the suffixes has m>0
	public String step3(String word)
	{
		//FIRST CASE: (m>0)icate --->ic
		if(word.endsWith("icate"))
		{
			word=replaceM0(word,"icate","ic");
		}
		//SECOND CASE: (m>0)ative --->""
		else if(word.endsWith("ative"))
		{
			word=replaceM0(word,"active","");
		}
		//THIRD CASE: (m>0)alize -->al
		else if(word.endsWith("alize"))
		{
			word=replaceM0(word,"alize","al");
		}
		//FOURTH CASE: (m>0)alize -->al
		else if(word.endsWith("iciti"))
		{
			word=replaceM0(word,"iciti","ic");
		}
		//FIFTH CASE: (m>0)ical -->ic
		else if(word.endsWith("ical"))
		{
			word=replaceM0(word,"ical","ic");
		}
		//SIXTH CASE: (m>0)ful -->""
		else if(word.endsWith("ful"))
		{
			word=replaceM0(word,"ful","");
		}
		//SEVENTH CASE:(m>0)ness -->""
		else if(word.endsWith("ness"))
		{
			word=replaceM0(word,"ness","");
		}		

		return word;
	}

    //changes the suffixes if the substring before the suffixes has m>1
    public String step4(String word)
	{
        //FIRST CASE:(m>1)al --->""
		if(word.endsWith("al"))
		{
			word=replaceM1(word,"al","");
		}
		//SECOND CASE:(m>1)ance --->""		
		else if(word.endsWith("ance"))
		{
			word=replaceM1(word,"ance","");
		}
		//THIRD CASE:(m>1)ence --->""
		else if(word.endsWith("ence"))
		{
			word=replaceM1(word,"ence","");
		}
		//FOURTH CASE:(m>1)er --->""
		else if(word.endsWith("er"))
		{
			word=replaceM1(word,"er","");
		}		
		//FIFTH CASE:(m>1)ic --->""
		else if(word.endsWith("ic"))
		{
			word=replaceM1(word,"ic","");
		}
		//SIXTH CASE:(m>1)able --->""
		else if(word.endsWith("able"))
		{
			word=replaceM1(word,"able","");
		}
		//SEVENTH CASE:(m>1)ible --->""
		else if(word.endsWith("ible"))
		{
			word=replaceM1(word,"ible","");
		}		
		//EIGHTH CASE:(m>1)ant --->""
		else if(word.endsWith("ant"))
		{
			word=replaceM1(word,"ant","");
		}
		//NINETH CASE:(m>1)ement --->""
		else if(word.endsWith("ement"))
		{
			word=replaceM1(word,"ement","");
		}
		//TENTH CASE:(m>1)ment --->""
		else if(word.endsWith("ment"))
		{
			word=replaceM1(word,"ment","");
		}				
		//ELEVENTH CASE:(m>1)ent --->""
		else if(word.endsWith("ent"))
		{
			word=replaceM1(word,"ent","");
		}								
		//TWELVETH CASE: if the word ends with ion, and the part before ion has m>1 and ends with the letter s or t, remove "ion". 
		else if(word.endsWith("ion"))
		{
			
			String stringBeforeIon=word.substring(0,word.length()-3);
			if(calcM(stringBeforeIon)>1 && (endsWith(stringBeforeIon,'s') || endsWith(stringBeforeIon,'t') ))
			{
				word=stringBeforeIon;
			}
		}
		//THIRTEENTH CASE: (m>1)ou --->""
		else if(word.endsWith("ou"))
		{
			word=replaceM1(word,"ou","");
		}
		//FOURTEENTH CASE: (m>1)ism --->""	
		else if(word.endsWith("ism"))
		{
			word=replaceM1(word,"ism","");
		}
		//FIFTEENTH CASE: (m>1)ate --->""			
		else if(word.endsWith("ate"))
		{
			word=replaceM1(word,"ate","");
		}
		//SIXTEENTH CASE: (m>1)iti --->""			
		else if(word.endsWith("iti"))
		{
			word=replaceM1(word,"iti","");
		}
		//SEVENTEENTH CASE: (m>1)ous --->""			
		else if(word.endsWith("ous"))
		{
			word=replaceM1(word,"ous","");
		}	
		//EIGTHEENTH CASE: (m>1)ive --->""			
		else if(word.endsWith("ive"))
		{
			word=replaceM1(word,"ive","");
		}
		//NINETEENTH CASE: (m>1)ize --->""			
		else if(word.endsWith("ize"))
		{
			word=replaceM1(word,"ize","");
		}												
					
		return word;
	}

	//handles removing the letter 'e' at the end of the word.
	public String step5a(String word)
	{
		if(endsWith(word,'e'))
		{
			String stringBeforeE=word.substring(0,word.length()-1); //the substring before the letter e.
			if((calcM(stringBeforeE)==1 && !checkPattern(stringBeforeE)) || (calcM(stringBeforeE)>1))
			{
               word=stringBeforeE;
			}
		}
		return word;
	}

	//if m>1 and the word ends with 2 consonants, the last letter is removed.
	public String step5b(String word)
	{
		if(calcM(word)>1 && endsWithDoubleCons(word) && endsWith(word,'l'))
		{
			word=word.substring(0,word.length()-1);
		}
		return word;
	}

    //Follows the steps to stem a word.
	public String StemWord(String word)
	{
		if(word.length()>1)
		{
			word=step1A(word);
			word=step1b(word);
			word=step1c(word);
			word=step2(word);
			word=step3(word);
			word=step4(word);
			word=step5a(word);
			word=step5b(word);
		
		}
		return word;
	}

	public static void main(String args[]) throws Exception {
		String a = "MULTIDIMENSIONAL";
		a=a.toLowerCase();
		Stemmer s = new Stemmer();
		//System.out.println(s.StemWord(a));
		return;
	}
	
}
