package de.irian.quazer.twitter;

public interface TwitterSearchStringFactory {

	String createSearchString(String searchTerm, boolean exact);
	
}
