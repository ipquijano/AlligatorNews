package com.angbaboy.newsalligator;
import java.util.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.io.File;
import java.util.List;

public class Tokenizer { // Inverse Average Fragment Length
	
	public ArrayList<String> lines = new ArrayList<String>();
 
	public static void main(String[] args) {
		String statusListInput = "status12.txt";
		String likeListInput = "likes12.txt";

		List<String> likeList = new ArrayList<String>();
		List<Status> statusList = new ArrayList<Status>();
		List<Term> terms = new ArrayList<Term>(); 
		//Used to put all terms in one list; terms are currently saved per status
		
		statusList_fileHandling(statusListInput, statusList);
		likeList_fileHandling(likeListInput, likeList);
		//printLikes(likes);
		//printStats(statuses);
		//printTerms(terms);
		computeTermWeight(statusList, terms, likeList);
		//printTerms(statuses, terms);
	}
	
	public static void printStats(List<Status> statusList){
		for (int i = 0; i < statusList.size(); i++){
			System.out.println(statusList.get(i).getId() + " | " + statusList.get(i).getStatLength() +" | Status: " + statusList.get(i).getStat());
		}
	}
	
	public static void printLikes(List<String> likeList){
		for (int i = 0; i < likeList.size(); i++){
			System.out.println(likeList.get(i));
		}
	}
	
	public static void printTerms(List<Status> statusList, List<Term> termList){
		
		for (int i = 0; i< statusList.size(); i++){
			for(int j=0; j< statusList.get(i).getTerms().size(); j++){
				termList = statusList.get(i).getTerms();
			}
	   		System.out.println(termList.size());
	   	 }
		/*SIGE UG CHANGE ANG TERMS PER STATUS TERMS
		 * DILI MASAVE TANANG TERMS SA STATUSES
		 * I.save all terms
		 * EXCEPT NULL CHARACTERS
		 * TERMS ALREADY IN LIST*/
		
		//System.out.println(terms.size());

		Collections.sort(termList, new Comparator<Term>() {
			@Override
			public int compare(Term o1, Term o2) {
				return Float.valueOf(o1.totalWeight).compareTo(o2.totalWeight);
			}  
		 });
		
		System.out.println(termList.size());
		for (int i = 0; i < termList.size(); i++){
			System.out.println(termList.get(i).getRecency() + " | " + termList.get(i).getTerm());
		}
	}
	
	public static void likeList_fileHandling(String likeListInput, List<String> likeList) {
		try {
			Scanner s = new Scanner(new File(likeListInput)).useDelimiter("\n");
			while (s.hasNext()){
				likeList.add(s.next());
			}
			s.close();
		} catch (IOException e) {	
			System.out.println("File Read Error");
		}
	}
	
	public static void statusList_fileHandling(String input, List<Status> statusList) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(input));
			String str;
			int count = 0;
			str = new String();

			while ((str = in.readLine()) != null) {
				List<Term> thisToken = new ArrayList<Term>();
				
				Status status = new Status(str, thisToken, str.length(), count);
				
				String[] tokens = str.split("[!,.?:; ]|and|or");
				
				for (int i = 0; i < tokens.length; i++){
					Term term = new Term(tokens[i].trim(), 0,  0,  0,  0, i,  0 ); // added .trim() to tokens[]
					tokens[i] = tokens[i].trim();
					//System.out.println("term at " + i + " = " + term.getTerm());
					status.getTerms().add(term);
				}

				statusList.add(status);
				count++;

			}
			in.close();
		} catch (IOException e) {	
			System.out.println("File Read Error");
		}
	}
	
	
	
	/**
	 * Inverse average fragment length
	 * Parsing the string using
	 * Stop words: and, or, and both
	 * Stop characters: comma (,), exclamation mark (!), question mark (?), full stop (.), colon (:), and semicolon (;)
	 **/
	public static void ifl(List<Status> statuses){
		for (int i = 0; i< statuses.size(); i++){
	   		 List<Term> termsList = statuses.get(i).getTerms();
	   		 for( int j = 0; j < termsList.size(); j++ ) {
	   			termsList.get(j).setFragLength((float) 1/ ((((float) 1)/termsList.size()) * termsList.get(j).getTerm().length()));
	   			//System.out.println(termsList.size() + " | " + termsList.get(j).getTerm().length());
	   			//System.out.println(termsList.get(j).getTerm() + " | " + termsList.get(j).getFragLength());
	   		 }
	   	 }
	}
	
	/**
	 * Category Probability
	 * Calculates the word's importance in a given category
	 * P(c|d)
	 * Estimated simply by taking the number of documents in the category that contain the word 
	 * t (|{d : t d; d c}|) 
	 * and dividing it by the total number of documents that contain word 
	 * t (|{d : t d}|)
	 **/
	public static void categoryProbability(List<Status> statuses){
	   	 for (int i = 0; i< statuses.size(); i++){
	   		 List<Term> termsList = statuses.get(i).getTerms();
	   		 for( int j = 0; j < termsList.size(); j++ ) {
	   			 int ctr = 0;
	   			 
	   			for (int k = 0; k< statuses.size(); k++){
	   				String str1 = statuses.get(k).getStat();
	   				String str2 = termsList.get(j).getTerm();
	   				
	   				if(str1.toLowerCase().contains(str2.toLowerCase()))
	   					ctr++;
	   			}
	   			
	   			termsList.get(j).setCatProbability(((float) statuses.get(i).getTerms().size()) / (float) ctr);
	   			//System.out.println(termsList.get(j).getCatProbability() + " | " + termsList.get(j).getTerm());
	   		 }
	   	 }
	   }
	
	/**
	 * Bi-Normal Separation
	 * Use the assumption that among positive and negative samples; 
	 * the words that occur in the positive samples get a higher weight.
	 * 1/number of postive samples that contains terms
	 **/
	public static void bns(List<Status> statuses, List<String> likes){
		for (int i = 0; i < statuses.size(); i++){
	   		 List<Term> termsList = statuses.get(i).getTerms();
	   		 for( int j = 0; j < termsList.size(); j++ ) {
	   			 int ctr = 0;
	   			 
	   			for (int k = 0; k < statuses.size(); k++){
	   				String str1 = termsList.get(j).getTerm();
	   				String str2 = likes.get(0);
	   				// hi!
	   				if(str2.toLowerCase().contains(str1.toLowerCase())){
	   					ctr++;
	   				}	   					
	   			}
	   			
	   			if(ctr == 0){
	   				termsList.get(j).setDocProbability(.005f);	
	   			} else {
	   				termsList.get(j).setDocProbability(((float)1)/ ctr);	
	   			}
	   			
	   			//System.out.println(termsList.get(j).getDocProbability() + " | " + termsList.get(j).getTerm());
	   		 }
	   	 }
	}
	
	
	/**
	 * Time Sensitive Term Weighting
	 * Considers post time of the status update
	 * 1 dvided by the recency rank
	 **/
	public static void rankRecency(List<Status> statuses){
		for (int i = 0; i< statuses.size(); i++){
	   		 List<Term> termsList = statuses.get(i).getTerms();
	   		 for( int j = 0; j < termsList.size(); j++ ) {
	   			 
	   			termsList.get(j).setRecency(((float)1)/ (float) (statuses.get(i).getId() + 1));
	   			//System.out.println(termsList.get(j).getRecency());
	   		 }
	   	 }
	}
	
	/**
	 * Fragment Length Weighted Category Distribution
	 * Combining the above three tasks through multiplication
	 * BNS(t,c) x P(c|d) x ifl(t)
	 **/
	public static void computeIC(List<Status> statuses, List<String> likes){
		ifl(statuses);
		categoryProbability(statuses);
		bns(statuses, likes);
		
		for (int i = 0; i< statuses.size(); i++){
	   		 List<Term> termsList = statuses.get(i).getTerms();
	   		 for( int j = 0; j < termsList.size(); j++ ) {
	   			termsList.get(j).setIcCount((float) 1 / ((float) termsList.get(j).getFragLength() * (float) termsList.get(j).getDocProbability() * (float) termsList.get(j).getCatProbability()));
	   		 }
	   	 }
	}
	
	public static void computeTermWeight(List<Status> statusList, List<Term> termList, List<String> likeList){
		computeIC(statusList, likeList);
		rankRecency(statusList);
		
		for (int i = 0; i< statusList.size(); i++){
	   		 List<Term> termsList = statusList.get(i).getTerms();
	   		 for( int j = 0; j < termsList.size(); j++ ) {
	   			termsList.get(j).setTotalWeight(termsList.get(j).getIcCount() * termsList.get(j).getRecency());
	   			if(termsList.get(j).getTerm().length() > 2)
	   				System.out.println(termsList.get(j).getTotalWeight() + " - " + termsList.get(j).getTerm());
	   		 }
	   	 }
	}
	

}

