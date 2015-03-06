package com.gjorgiev.data;

public class Query {
	private long id;
	private String keyword;
	private String location;
	private String country;
	private long timestamp;
	
	public Query(String keyword, String location, String country, long timestamp)
	{
		this.keyword = keyword;
		this.location = location;
		this.country = country;
		this.timestamp = timestamp;
	}
	
	public long getId(){
		return id;
	}
	public void setId(long id){
		this.id = id;
	}
	
	public String getKeyword(){
		return keyword;
	}
	public String getLocation(){
		return location;
	}
	public long getTimestamp(){
		return timestamp;
	}
	public String getCountry() {
		return country;
	}
}
