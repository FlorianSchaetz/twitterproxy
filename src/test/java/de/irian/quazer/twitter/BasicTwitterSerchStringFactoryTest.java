package de.irian.quazer.twitter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class BasicTwitterSerchStringFactoryTest {

	private BasicTwitterSerchStringFactory factory = new BasicTwitterSerchStringFactory();
	
	@Test
	public void createSearchString_must_return_wrapped_string_for_exact() {
		
		String result = factory.createSearchString("bla blub", true);
		
		assertThat(result).isEqualTo("\"bla blub\"");		
	}
	
	@Test
	public void createSearchString_must_not_wrap_if_already_wrapped() {
		assertThat(factory.createSearchString("\"bla blub", true)).isEqualTo("\"bla blub\"");
		assertThat(factory.createSearchString("bla blub\"", true)).isEqualTo("\"bla blub\"");
		assertThat(factory.createSearchString("\"bla blub\"", true)).isEqualTo("\"bla blub\"");
	}
	
	
	@Test
	public void createSearchString_must_return_OR_concatted_string_for_non_exact() {
		
		String result = factory.createSearchString("bla blub", false);
		
		assertThat(result).isEqualTo("bla OR blub");		
	}
	
	@Test
	public void createSearchString_must_accept_null_without_exception() {		
		assertThat(factory.createSearchString(null, false)).isEqualTo("");
		assertThat(factory.createSearchString(null, true)).isEqualTo("");
	}
	
}
