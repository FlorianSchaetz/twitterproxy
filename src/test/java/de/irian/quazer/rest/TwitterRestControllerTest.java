package de.irian.quazer.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.irian.quazer.twitter.TwitterException;
import de.irian.quazer.twitter.TwitterSearch;
import de.irian.quazer.twitter.TwitterService;

@RunWith(MockitoJUnitRunner.class)
public class TwitterRestControllerTest {
	
	private MockMvc mockMvc;

	@Mock
	private TwitterService twitterService;
	
	private TwitterRestController restController;
	 
    @Before
    public void setup() {
    	
    	this.restController = new TwitterRestController(twitterService);
    	
        this.mockMvc = MockMvcBuilders.standaloneSetup(restController).build();
    }
    
    @Test
    public void missing_searchTerm_should_throw_400() throws Exception {

    	this.mockMvc.perform(get("/api/v1/search"))
    		.andExpect(status().isBadRequest())
    		.andExpect(status().reason("Required String parameter 'searchTerm' is not present"));
    }
    
    @Test
    public void empty_searchTerm_should_throw_400() throws Exception {

    	this.mockMvc.perform(get("/api/v1/search?searchTerm={searchTerm}", ""))
    		.andExpect(status().isBadRequest())
    		.andExpect(status().reason("Required String parameter 'searchTerm' may not be empty"));
    }
    
    @Test
    public void maxEntries_less_than_1_should_throw_400() throws Exception {

    	this.mockMvc.perform(get("/api/v1/search?searchTerm={searchTerm}&maxResults={maxEntries}", "bla", -1))
    		.andExpect(status().isBadRequest())
    		.andExpect(status().reason("Optional Integer parameter 'maxEntries' must be between 1 and 100"));
    }
    
    @Test
    public void maxEntries_greater_than_100_should_throw_400() throws Exception {

    	this.mockMvc.perform(get("/api/v1/search?searchTerm={searchTerm}&maxResults={maxEntries}", "bla", 101))
    		.andExpect(status().isBadRequest())
    		.andExpect(status().reason("Optional Integer parameter 'maxEntries' must be between 1 and 100"));
    }
    
    @Test
    public void twitterException_should_throw_500() throws Exception {
    	TwitterSearch search = mockTwitterSearch();
    	when(search.search()).thenThrow(TwitterException.class);
    	when(twitterService.newSearch(ArgumentMatchers.anyString())).thenReturn(search);
    	
    	this.mockMvc.perform(get("/api/v1/search?searchTerm={searchTerm}", "bla"))
    		.andExpect(status().isInternalServerError());
    }
    
    @Test
    public void defaults_should_be_correct() throws Exception {
    	TwitterSearch search = mockTwitterSearch();
    	
    	when(twitterService.newSearch(ArgumentMatchers.anyString())).thenReturn(search);
    	
    	this.mockMvc.perform(get("/api/v1/search?searchTerm={searchTerm}", "bla"))
    		.andExpect(status().is2xxSuccessful());
    	
    	verify(twitterService).newSearch("bla");
    	verify(search).withExact(false);
    	verify(search).withMaxResults(20);
    }
    
    @Test
    public void valid_request_should_call_twitter_service() throws Exception {
    	
    	TwitterSearch search = mockTwitterSearch();
    	
    	when(twitterService.newSearch(ArgumentMatchers.anyString())).thenReturn(search);
    	
    	this.mockMvc.perform(get("/api/v1/search?searchTerm={searchTerm}&maxResults={maxEntries}&exact={exact}", "bla", 99, true))
    		.andExpect(status().is2xxSuccessful());
    	
    	verify(twitterService).newSearch("bla");
    	verify(search).withExact(true);
    	verify(search).withMaxResults(99);
    	
    }
    
    @Test
    public void accepting_only_non_JSON_should_throw_406() throws Exception {
    	this.mockMvc.perform( get("/api/v1/search?searchTerm={searchTerm", "bla").accept(MediaType.APPLICATION_XML))
    		.andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }
    
    private TwitterSearch mockTwitterSearch() {
    	TwitterSearch search = mock(TwitterSearch.class);
    	when(search.withExact(ArgumentMatchers.anyBoolean())).thenReturn(search);
    	when(search.withMaxResults(ArgumentMatchers.anyInt())).thenReturn(search);
    	return search;
    }
    
}
