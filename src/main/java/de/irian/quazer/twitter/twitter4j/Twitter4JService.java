package de.irian.quazer.twitter.twitter4j;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.irian.quazer.twitter.TwitterException;
import de.irian.quazer.twitter.TwitterSearch;
import de.irian.quazer.twitter.TwitterSearchStringFactory;
import de.irian.quazer.twitter.TwitterService;
import de.irian.quazer.twitter.TwitterStatus;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;

@Service
public class Twitter4JService implements TwitterService {

	private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);
	
	private Twitter twitter;
	private TwitterSearchStringFactory searchStringFactory;

	@Autowired
	public Twitter4JService(Twitter twitter, TwitterSearchStringFactory searchStringFactory) {
		this.twitter = twitter;
		this.searchStringFactory = searchStringFactory;
		
		Assert.notNull(this.twitter, "No Twitter object injected.");
		Assert.notNull(this.searchStringFactory, "No TwitterSearchStringFactory injected");
	}

	public Twitter4JSearch newSearch(String searchString) {
		return new Twitter4JSearch(searchString);
	}

	public class Twitter4JSearch implements TwitterSearch {

		private String searchTerm;
		private boolean exact = false;
		private int maxResults = 20;

		private Twitter4JSearch(String searchTerm) {
			this.searchTerm = searchTerm;
		}

		public Twitter4JSearch withMaxResults(int amount) {
			if (amount < 1) {
				this.maxResults = 1;
			} else if (amount > 100) {
				this.maxResults = 100;
			} else {
				this.maxResults = amount;
			}
			return this;
		}

		public Twitter4JSearch withExact(boolean value) {
			this.exact = value;
			return this;
		}

		private boolean isEmptySearch() {
			return StringUtils.isEmpty(searchTerm);
		}
		
		public List<TwitterStatus> search() throws TwitterException {

			if (isEmptySearch()) {
				return Collections.emptyList();
			}

			Query query = prepareQuery();
			
			try {
				QueryResult result = twitter.search(query);
				
				List<TwitterStatus> status = result.getTweets().stream()
						.map(this::convert)
						.collect(Collectors.toList());
				
				logger.debug("Found {} tweets.", status.size());
				
				return status;
				
			} catch (twitter4j.TwitterException e) {
				logger.error(e.getMessage(), e);
				throw new TwitterException(e.getErrorMessage());
			}
		}
		
		private Query prepareQuery() {
			String searchString = searchStringFactory.createSearchString(searchTerm, exact);
			Query query = new Query(searchString);
			query.setCount(maxResults);
			
			logger.debug("Preparing query for <{}> with {} max results", searchString, maxResults);
			
			return query;
		}
		
		private TwitterStatus convert(Status status) {
			TwitterStatus result = new TwitterStatus();
			result.setCreatedAt( status.getCreatedAt());
			result.setUser(status.getUser().getName());
			result.setId(status.getId());
			result.setText(status.getText());
			return result;
		}
	}
	

}
