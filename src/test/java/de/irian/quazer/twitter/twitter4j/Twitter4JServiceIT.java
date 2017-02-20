package de.irian.quazer.twitter.twitter4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import de.irian.quazer.twitter.BasicTwitterSerchStringFactory;
import de.irian.quazer.twitter.TwitterException;
import de.irian.quazer.twitter.TwitterSearchStringFactory;
import de.irian.quazer.twitter.TwitterService;
import de.irian.quazer.twitter.TwitterStatus;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class Twitter4JServiceIT {

	private Twitter twitter = TwitterFactory.getSingleton();
	
	private TwitterSearchStringFactory searchStringFactory = new BasicTwitterSerchStringFactory();
	
	private TwitterService twitterService = new Twitter4JService(twitter, searchStringFactory);
	
	@Test
	public void fuzzy_search_should_work() throws TwitterException {
		List<TwitterStatus> result = twitterService.newSearch("twitter test").search();		
		assertThat(result.size()).isLessThanOrEqualTo(20);	
	}
	
	@Test
	public void exact_search_should_work() throws TwitterException {
		List<TwitterStatus> result = twitterService.newSearch("twitter test").withExact(true).search();		
		assertThat(result.size()).isLessThanOrEqualTo(20);	
	}
	
	@Test
	public void search_should_produce_only_max_results() throws TwitterException {
		List<TwitterStatus> result = twitterService.newSearch("twitter test").withMaxResults(62).search();		
		assertThat(result.size()).isLessThanOrEqualTo(62);	
	}
}

