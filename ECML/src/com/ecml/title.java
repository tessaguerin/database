package com.ecml;

// This class is our model and contains the data we will save in the DB.

public class title {
	
		  private long id;
		  private String title;
		  
		  public title() {
			  
		  }
		  
		  public title(String text) {
			  title = text;
		  }

		  public long getId() {
		    return id;
		  }

		  public void setId(long id) {
		    this.id = id;
		  }

		  public String getTitle() {
		    return title;
		  }

		  public void setTitle(String title) {
		    this.title = title;
		  }

		  // Will be used by the ArrayAdapter in the ListView
		  @Override
		  public String toString() {
		    return title;
		  }
		} 


