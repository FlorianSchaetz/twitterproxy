package de.irian.quazer.twitter;

import java.util.List;

public interface TwitterSearch {
	
	TwitterSearch withMaxResults(int amount);

	TwitterSearch withExact(boolean value);	

	List<TwitterStatus> search() throws TwitterException;
}
