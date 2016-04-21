package com.wisenut.tea20.types;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Topic Info.
 * Description for a topic with some metadata related with it.
 * 
 * @author hkseo@wisenut.co.kr
 */
public class TopicInfo {
	private String id;
	private String label;
	private List<Pair<Integer>> keywords;
	private int docCount;
	private boolean needsAnalysis;

	public TopicInfo(String id, String label, List<Pair<Integer>> keywords, int docCount, boolean needsAnalysis) {
		this.id = id;
		this.label = label;
		if (null == keywords) {
			this.keywords = new ArrayList<Pair<Integer>>();
		} else {
			this.keywords = keywords;
		}
		this.docCount = docCount;
		this.needsAnalysis = needsAnalysis;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public List<Pair<Integer>> getKeywords() {
		return keywords;
	}

	public int getDocCount() {
		return docCount;
	}

	public boolean needsAnalysis() {
		return needsAnalysis;
	}

}
