package de.irian.quazer.twitter.twitter4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.irian.quazer.twitter.TwitterSearchStringFactory;
import de.irian.quazer.twitter.TwitterStatus;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@RunWith(MockitoJUnitRunner.class)
public class Twitter4JServiceTest {

	@Mock
	private Twitter twitter;
	
	@Spy
	private TwitterSearchStringFactory searchStringFactory = new TestTwitterSearchStringFactory();
	
	@InjectMocks
	private Twitter4JService twitterService;
	
	@Test
	public void twitterSearch_should_return_empty_list_for_no_results() throws TwitterException, de.irian.quazer.twitter.TwitterException {
		QueryResult mockedResult = mock(QueryResult.class);		
		when(twitter.search(any(Query.class))).thenReturn(mockedResult);
		
		List<TwitterStatus> result = twitterService.newSearch("test").search();
		
		assertThat(result).isEmpty();
	}
	
	@Test
	public void twitterSearch_should_return_empty_list_for_empty_searchString() throws TwitterException, de.irian.quazer.twitter.TwitterException {
		List<TwitterStatus> result = twitterService.newSearch("").search();
		
		assertThat(result).isEmpty();
		
		verify(twitter, never()).search(any(Query.class));
	}
	
	@Test
	public void twitterSearch_should_return_empty_list_for_null_searchString() throws TwitterException, de.irian.quazer.twitter.TwitterException {
		List<TwitterStatus> result = twitterService.newSearch(null).search();
		
		assertThat(result).isEmpty();
		
		verify(twitter, never()).search(any(Query.class));
	}
	
	@Test
	public void simpleSearchTerm_should_create_query_with_basic_parameters() throws TwitterException, de.irian.quazer.twitter.TwitterException {
				
		when(twitter.search(any(Query.class))).thenReturn(mock(QueryResult.class));
		
		twitterService.newSearch("test").search();
				
		ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
		verify(twitter).search(captor.capture());
		
		Query query = captor.getValue();
		assertThat(query.getQuery()).isEqualTo("test");
		assertThat(query.getCount()).isEqualTo(20);
	}

	@Test
	public void maxEntries_less_than_1_should_become_1() throws TwitterException, de.irian.quazer.twitter.TwitterException {
		when(twitter.search(any(Query.class))).thenReturn(mock(QueryResult.class));
		
		twitterService.newSearch("test tist tast").withMaxResults(0).search();	
		
		ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
		verify(twitter).search(captor.capture());
		
		Query query = captor.getValue();
		assertThat(query.getCount()).isEqualTo(1);
	}
	
	@Test
	public void maxEntries_between_1_and_100_should_be_used() throws TwitterException, de.irian.quazer.twitter.TwitterException {
		when(twitter.search(any(Query.class))).thenReturn(mock(QueryResult.class));
		
		twitterService.newSearch("test tist tast").withMaxResults(50).search();	
		
		ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
		verify(twitter).search(captor.capture());
		
		Query query = captor.getValue();
		assertThat(query.getCount()).isEqualTo(50);
	}
	
	@Test
	public void maxEntries_greater_than_100_should_become_100() throws TwitterException, de.irian.quazer.twitter.TwitterException {
		when(twitter.search(any(Query.class))).thenReturn(mock(QueryResult.class));
		
		twitterService.newSearch("test tist tast").withMaxResults(101).search();	
		
		ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
		verify(twitter).search(captor.capture());
		
		Query query = captor.getValue();
		assertThat(query.getCount()).isEqualTo(100);
	}
	
	@Test
	public void twitterSearchStringFactory_should_be_called_with_correct_parameters() throws TwitterException, de.irian.quazer.twitter.TwitterException {
		when(twitter.search(any(Query.class))).thenReturn(mock(QueryResult.class));
		
		twitterService.newSearch("test tist tast").withExact(true).search();	
		
		verify(searchStringFactory).createSearchString("test tist tast", true);
	}
	
	@Test(expected=de.irian.quazer.twitter.TwitterException.class)
	public void twitterException_should_be_caught_and_converted() throws TwitterException, de.irian.quazer.twitter.TwitterException {
		when(twitter.search(any(Query.class))).thenThrow(TwitterException.class);
		
		twitterService.newSearch("test tist tast").search();		
	}
	
}

class TestTwitterSearchStringFactory implements TwitterSearchStringFactory {

	@Override
	public String createSearchString(String searchTerm, boolean exact) {
		return searchTerm;
	}
	
}
