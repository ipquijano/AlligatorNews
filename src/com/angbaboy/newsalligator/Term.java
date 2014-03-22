package com.angbaboy.newsalligator;
import java.util.ArrayList;
import java.util.List;

public class Term{
	
	List<Status> statuses = new ArrayList<Status>();

	String term;
	float fragLength; //Inverse average fragment length
	float catProbability; //Category probability of the word
	float docProbability; //Bi-Normal Separation
	float icCount; // Fragment Length Weighted Category Distribution
					//Combination of Above three
	float recency; //Time Sensitive Term Weighting
	float totalWeight;
	
	public Term(String term, float fragLength, float catProbability,
			float docProbability, float icCount, float recency,
			float totalWeight) {
		super();
		this.term = term;
		this.fragLength = fragLength;
		this.catProbability = catProbability;
		this.docProbability = docProbability;
		this.icCount = icCount;
		this.recency = recency;
		this.totalWeight = totalWeight;
	}
	
	/**
	 * STATUS UPDATE 
	 * */
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * Inverse average fragment length
	 * Parsing the string using
	 * Stop words: and, or, and both
	 * Stop characters: comma (,), exclamation mark (!), question mark (?), full stop (.), colon (:), and semicolon (;)
	 * 
	 **/
	public float getFragLength() {
		return fragLength;
	}

	public void setFragLength(float fragLength) {
		this.fragLength = fragLength;
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
	public float getCatProbability() {
		return catProbability;
	}

	public void setCatProbability(float catProbability) {
		this.catProbability = catProbability;
	}

	/**
	 * Bi-Normal Separation
	 * Use the assumption that among positive and negative samples; 
	 * the words that occur in the positive samples get a higher weight.
	 **/
	public float getDocProbability() {
		return docProbability;
	}

	public void setDocProbability(float docProbability) {
		this.docProbability = docProbability;
	}

	/**
	 * Fragment Length Weighted Category Distribution
	 * Combining the above three tasks through multiplication
	 * BNS(t,c) x P(c|d) x ifl(t)
	 **/
	public float getIcCount() {
		return icCount;
	}

	public void setIcCount(float icCount) {
		this.icCount = icCount;
	}

	/**
	 * Time Sensitive Term Weighting
	 * Considers post time of the status update
	 **/
	public float getRecency() {
		return recency;
	}

	public void setRecency(float recency) {
		this.recency = recency;
	}
	
	/**
	 * Total Weight of Status Update
	 * Calculated by multiplying icCount with the receny count.
	 * 
	 **/
	public float getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(float totalWeight) {
		this.totalWeight = totalWeight;
	} 
	
}