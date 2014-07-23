package com.ecml;

// This class is our model and contains the data we will save in the DB.

public class composer {
	
		  private long id;
		  private String name_composer;
		  
		  public composer() {
			  
		  }
		  
		  public composer(String text) {
			  name_composer = text;
		  }

		  public long getId() {
		    return id;
		  }

		  public void setId(long id) {
		    this.id = id;
		  }

		  public String getNameComposer() {
		    return name_composer;
		  }

		  public void setNameComposer(String composer) {
		    this.name_composer = composer;
		  }

		  // Will be used by the ArrayAdapter in the ListView
		  @Override
		  public String toString() {
		    return name_composer;
		  }
		} 


