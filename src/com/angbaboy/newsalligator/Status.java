package com.angbaboy.newsalligator;
import java.util.List;

public class Status{
	String stat;
	List<Term> terms; 
	int statLength; //Inverse average fragment length
	int id; //Time Sensitive Term Weighting
	
	public Status(String stat, List<Term> terms, int statLength, int id) {
		super();
		this.stat = stat;
		this.terms = terms;
		this.statLength = statLength;
		this.id = id;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public List<Term> getTerms() {
		return terms;
	}

	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}

	public int getStatLength() {
		return statLength;
	}

	public void setStatLength(int statLength) {
		this.statLength = statLength;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void categoryProbability(Term term){
		
	}
	
	public void rankRecency(Term term){
		
	}
	
}