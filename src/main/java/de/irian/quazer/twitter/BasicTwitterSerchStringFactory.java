package de.irian.quazer.twitter;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class BasicTwitterSerchStringFactory implements TwitterSearchStringFactory {
	
	public String createSearchString(String searchTerm, boolean exact) {

		if (exact) {
			return Optional.ofNullable(searchTerm)
					.map(String::trim)
					.map(this::wrapForExactIfNeeded)
					.orElse("");
		}

		return Optional.ofNullable(searchTerm)
				.map(s -> s.split(" "))
				.map(Arrays::stream)
				.orElseGet(Stream::empty)
				.collect(Collectors.joining(" OR "));
		
	}
	
	private String wrapForExactIfNeeded(String str) {
		
		String result = str;
		
		if (!str.startsWith("\"")) {
			result = "\"" + result;
		}
		if (!result.endsWith("\"")) {
			result += "\"";
		}
		return result;
		
	}
}
