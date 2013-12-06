package com.mfino.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class StringWordDiffUtil {
	
	public static class NameLengthPair extends Pair<String, Integer> {
		public NameLengthPair(String s, Integer i) {
			super(s, i);
		}		
	}
	
	public static class DiffPair extends Pair<String, String> {
		public DiffPair(String s1, String s2) {
			super(s1, s2);
		}
		
		public String toString(){
			return firstElement + " = " + secondElement;
		}
	}
	
	List<NameLengthPair> nameLengthPairs = new ArrayList<NameLengthPair>();
	
	public void setVariableLength(String varname, int length){
		NameLengthPair p = new NameLengthPair(varname, length);
		nameLengthPairs.add(p);
	}
	
	public List<DiffPair> diff(String source, String dest){
		List<DiffPair> differenceWords = new ArrayList<DiffPair>(); 
		String[] sourceWords = source.trim().split(" ");
		String[] destWords = dest.trim().split(" ");
		int j, i; i = j = 0;
		while (i < sourceWords.length && j < destWords.length){
			int increment = 1;
			
			//there are additional spaces in the source text. 
			if(StringUtils.isBlank(sourceWords[i])) {
				i++;
				continue;
			}
				
			if (!sourceWords[i].equals(destWords[j])) {
				increment = getWordLength(sourceWords[i]);
				String destDiff = destWords[j];
				for(int k = 1; k < increment; k++)
					destDiff += " " + destWords[j + k];					
				differenceWords.add(new DiffPair(sourceWords[i], destDiff));
			} 
			i++; 
			j += increment;
		}
		return differenceWords;	
		
	}
	
	private int getWordLength(String variableName){
		for(NameLengthPair p : nameLengthPairs)
			if(p.getFirst().equals(variableName))
				return p.getSecond();
		return 1; //default length of any vaiable is 1 word			
	}
	
	public static void main(String[] args) {
		String source = "Your balance on $(date) is $cur$ $(amount)";
		String dest = "Your balance on 2010-06-15 15:45:00 is IDR 5000";
		StringWordDiffUtil util = new StringWordDiffUtil();
		util.setVariableLength("$(TransactionDateTime)", 2);
		
		List<DiffPair> diffs = util.diff(source, dest);
		for(Pair<String, String> diff : diffs)
			System.out.println(diff.getFirst() + " = " + diff.getSecond());
		
	}

}
