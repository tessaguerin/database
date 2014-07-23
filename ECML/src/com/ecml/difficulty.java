package com.ecml;

// This class is our model and contains the data we will save in the DB.

public class difficulty{
	
		  private long id;
		  private String note;
		  
		  public difficulty() {
			  
		  }
		  
		  public difficulty(String text) {
			  note = text;
		  }

		  public long getId() {
		    return id;
		  }

		  public void setId(long id) {
		    this.id = id;
		  }

		  public String getNoteDifficulty() {
		    return note;
		  }

		  public void setNoteDifficulty(String note) {
		    this.note = note ;
		  }

		  // Will be used by the ArrayAdapter in the ListView
//		  @Override
//		  public int toString() {
//		    return note;
//		  }
		} 



