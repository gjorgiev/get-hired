package com.gjorgiev.data;

import java.io.Serializable;

public class Job implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String jobTitle;
	private String snippet;
	private String formattedLocation;
	private String company;
	private String url;
	private Long datetime;
	
	public Job(String jobTitle, String snippet, String formattedLocation, String company, String url, Long datetime)
	{
		this.jobTitle = jobTitle;
		this.snippet = snippet;
		this.formattedLocation = formattedLocation;
		this.company = company;
		this.url = url;
		this.datetime = datetime;
	}
	
	public long getId(){
		return id;
	}
	public void setId(long id){
		this.id = id;
	}
	
	public String getJobTitle(){
		return jobTitle;
	}
	public String getSnippet(){
		return snippet;
	}
	public String getFormattedLocation(){
		return formattedLocation;
	}
	public String getCompany(){
		return company;
	}
	public String getUrl(){
		return url;
	}
	public Long getDatetime() {
		return datetime;
	}
}
