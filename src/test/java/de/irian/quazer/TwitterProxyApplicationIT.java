package de.irian.quazer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import de.irian.quazer.twitter.TwitterStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class TwitterProxyApplicationIT {
	
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    private String urlTemplate;
    
    @Before
    public void init(){
    	this.urlTemplate = String.format("http://localhost:%s/api/v1/search?", port);
    }
	
    @Test
    public void contextLoads() throws Exception {
    }

    @Test
    public void default_search_should_get_less_than_21_results() {
    	ResponseEntity<TwitterStatus[]> status = restTemplate.getForEntity(this.urlTemplate + "searchTerm={searchTerm}", TwitterStatus[].class, "bla");
    	TwitterStatus[] response = status.getBody();
    	
    	assertThat(response).isNotEmpty();
    	assertThat(response.length).isLessThanOrEqualTo(20);    	
    }
    
    @Test
    public void fuzzy_search_should_work() {
    	ResponseEntity<TwitterStatus[]> status = restTemplate.getForEntity(this.urlTemplate + "searchTerm={searchTerm}&maxEntries={maxEntries}", TwitterStatus[].class, "bla blub", 99);
    	TwitterStatus[] response = status.getBody();
    	
    	assertThat(response).isNotEmpty();
    	assertThat(response.length).isLessThanOrEqualTo(99);    	
    }
    
    @Test
    public void exact_search_should_work() {
    	ResponseEntity<TwitterStatus[]> status = restTemplate.getForEntity(this.urlTemplate + "searchTerm={searchTerm}&maxEntries={maxEntries}&exact={exact}", TwitterStatus[].class, "bla blub", 99, true);
    	TwitterStatus[] response = status.getBody();
    	
    	assertThat(response).isNotEmpty();
    	assertThat(response.length).isLessThanOrEqualTo(99);    	
    }
}
