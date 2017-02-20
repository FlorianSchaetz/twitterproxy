Quazer TwitterProxy

Since I am quite familiar with the Eclipse IDE and Maven, I tend to use those, but of course, I am not religiously attached to them (Gradle, for example, sounds great, too). I prefer the AssertJ style of assertions for JUnit, since they tend to be easier to read and use than the Hamcrest matchers, but that, too, is just a personal preference. Logging is done via SLF4J because it doesn't bind us to a specific logging framework. 

Providing an API which delivers JSON can of course be done in various way, but the typical approach would be REST (which was not explicitly defined in the task description, but it seems like a reasonable choice). I was considering making the whole thing a HATEOAS service, but decided it was not required and would probably also be simply too much for a simple application like this. 

Providing a REST service can be done with various frameworks, but since I am already familiar with Spring, especially Spring Boot, I chose that. It allows to concentrate on the actual classes and takes care of much of the boilerplate code that would otherwise be required around those classes. It also offers good testing capabilities for the whole thing via some simple JUnit annotations. Security was not required in the specification, so I omitted that, but of course, it could easily be added via the usual Spring Security methods. 

Assuming that the task didn't include coding a new twitter library, one of the first steps was to find a suitable one. The twitter developer site listed Twitter4J, so it was an easy choice, but I decided to wrap it into its own (Spring) service, since we will only need a single feature and this way we can switch out the library more easily later. I defined the library's core twitter4j.Twitter object a a (singleton) bean instead of hard-coding it into the service, to allow for easier mocking.

It was not defined if the JSON object returned should be the original twitter json, so I decided for a very simplified version, only containing basic information, but obviously it would be easy to change that and return much more data (or the original twitter json data).

The actual structure of such an application is pretty basic: A service that connects to twitter and does the actual searching and a REST controller that takes the user input, calls the service and returns the result to the user.

Fuzzy logic was a little bit tricky, but I assume that simply combining the words of the search with OR instead of searching for the exact phrase (wrapped into " ") will suffice for now. We could of course add our own algorithm, do multiple searches and aggregate the result, etc. But I assumed that this would be too much and since I had to do this task during a few hours on the weekend, I will submit those details with the finished version 0.01, offering to adjust it, if something was meant differently. In real life, we would have talked a little bit about the whole thing first anyway.
I decided to put the algorithm to create the fuzzy/exact versions of the search term into it's own Spring component, allowing us to re-use it for different libraries later. This way, the TwitterSearchStringFactory does one thing, produce search strings to give to twitter, and does that well, instead of adding that functionality to the actual Spring service.

It should be noted that, currently, the user is assumed to only enter simple search terms and not complex ones with logic already included. Of course, by enhancing the TwitterSearchStringFactory, we could integrate a small parser and also accept stuff like this. 

There can be a few errors: If the user enters an empty search string or tries to load more search results than supported (100), the REST controller will return an appropriate error code and reason. If something happens during the actual twitter search, a generic 500 status code will be sent out (since the outside world will not need to know about the internal specifics). Internally, the service will accept most stuff and try to do something useful, the error handling of user input
is handled through the REST controller.

The services, etc. are mostly based on interfaces to make them easier to test. Dependency injection is done via constructor injection, since it allows us the make those classes imutable which helps preventing problems and we don't need to complicate the interface with setter methods (field injection is possible, but it requires some "magic" behind the scenes - Mockito can handle that, too, but personally, to me, constructor injection is the most clean way - some people argue, that your constructors get to complicated, but that is mostly the case when your class does too much and thus gets too many dependencies).

Obviously, we would normally put the twitter api credentials into a external config file and not put it inside the code, but I wanted to provide a template to make it easier to execute.

The tests cover almost 100% of the code, the missing % are from the main method that isn't used by the spring tests, so the numerical coverage is slightly less than 100%. Of course, this doesn't mean that the code has to be bug free (it hopefully is), but it's a start, at least.

Basic API versioning was done via using a specific path (/api/v1), but of course, we could do much more, for example by defining an annotation that takes care of that, which can be done in Spring quite easily but felt like overkill in this example. As long as we provide versioned urls, we can add the actual management of the internals later.

I also provided a quick&dirty test page, that simply sends the GET request. We could improve that for more complicated queries by using JS (or a framework) to send the requests, if we don't want to enforce a separate REST client for testing.

How to run:

 * Fill in the /src/main/resources/twitter4j.properties file with valid data
 * Run TwitterProxyApplication.main()
 * Call, for example localhost:8080/api/v1/search?searchTerm=test&maxEntires=50&exact=true
 * Alternatively, use the simple test html page at http://localhost:8080/test.html






