package de.irian.quazer.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import de.irian.quazer.twitter.TwitterException;
import de.irian.quazer.twitter.TwitterService;
import de.irian.quazer.twitter.TwitterStatus;

@Controller
public class TwitterRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(TwitterRestController.class);
	
	private TwitterService twitterService;

	@Autowired
	public TwitterRestController(TwitterService twitterService) {
		this.twitterService = twitterService;
	}

	@RequestMapping(value="/api/v1/search", produces="application/json; charset=UTF-8")
	@ResponseBody
	public List<TwitterStatus> search(@RequestParam(required = true) String searchTerm,
			@RequestParam(defaultValue = "20") int maxResults, @RequestParam(defaultValue = "false") boolean exact)
			throws TwitterException {
		
		logger.debug("Received request for <{}>, {} max results, exact {}", searchTerm, maxResults, exact);

		if (StringUtils.isEmpty(searchTerm)) {
			logger.debug("Search term was empty");
			throw new SearchTermEmptyException();
		}
		
		if ( maxResults < 1 || maxResults > 100) {
			logger.debug("Max results {} were invalid", maxResults);
			throw new MaxResultsInvalidException();
		}
		
		return twitterService.newSearch(searchTerm).withMaxResults(maxResults).withExact(exact).search();

	}
	
	@ExceptionHandler(SearchTermEmptyException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="Required String parameter 'searchTerm' may not be empty")
	public void handleSearchTermEmptyException() {
		//
	}
	
	@ExceptionHandler(MaxResultsInvalidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="Optional Integer parameter 'maxEntries' must be between 1 and 100")
	public void handleMaxEntriesInvalidException() {
		//
	}

	@ExceptionHandler(TwitterException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleTwitterException() {
		//
	}

}

@SuppressWarnings("serial")
class MaxResultsInvalidException extends RuntimeException {}

@SuppressWarnings("serial")
class SearchTermEmptyException extends RuntimeException {}