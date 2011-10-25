# 55 Minutes Wicket Project

The 55 Minutes Wicket project is a set of tools and libraries we use for enhancing our productivity
with the [Apache Wicket Java web framework](http://wicket.apache.org/). We've made our code
available as open source to share with the Wicket community.

**This project requires JDK 6 and Wicket 1.5.** Refer to the
[fiftyfive-wicket-2.x](https://github.com/55minutes/fiftyfive-wicket-2.x) project if you need an
older release that can be used with Wicket 1.4.

## Quick start

The quickest way to get started with the 55 Minutes Wicket library is use our Maven archetype. This assumes that you have maven installed, preferably version 3.0.

First, make sure you have `compass` and its related ruby gems installed.

```
gem install compass compass-colors sassy-buttons
```

Then run the following command:

```
mvn archetype:generate -U \
    -DarchetypeGroupId=com.55minutes \
    -DarchetypeArtifactId=fiftyfive-wicket-archetype \
    -DarchetypeRepository=http://opensource.55minutes.com/maven-releases \
    -DarchetypeVersion=3.0.2
```

This creates a project directory with all the Java, Maven POM and web.xml scaffolding you need for a
Wicket application with Compass, Shiro and Spring integration, plus high-quality starters for your
HTML5, CSS and JavaScript. As with most maven archetypes, you'll be prompted for the project name,
group ID, artifact ID, version and package.

Next, to run the resulting project, simply change into the project directory and run:

```
mvn jetty:run
```

That's it! Your project is up and running at http://localhost:8080/. Explore the code included with
the archetype and then continue reading this page to learn about what is included in
fiftyfive-wicket.

## Compass

The fiftyfive-wicket archetype includes stylesheets that are written in [Sass][sass] using the SCSS
syntax, which are then compiled down to standard CSS using [Compass][compass]. This gives you
powerful tools like variables and mixins, and improves browser performance by merging separate CSS
files into a single response.

We've integrated Compass with the Maven build process, meaning `mvn compile` also compiles SCSS
to CSS, and `mvn clean` also deletes the compiled CSS. We recommend stylesheet authors run 
`compass watch` as part of their workflow so that CSS files are automatically regenerated the instant you save an SCSS file.

Read more about how we use Compass by visiting our [css3-foundation project][cssf].


## Shortcuts

We've identified the most commonly reused patterns when authoring Wicket pages and created static
helper methods for making this code much more concise. We call these [Shortcuts][shortcuts].

For example, often you will want to specify a CSS class for a Wicket component in Java. Here's
how you normally would do this in Wicket:

```java
add(new AttributeAppender("class", foo).setSeparator(" "));
```

With Shortcuts:

```java
add(cssClass(foo));
```

Or maybe you want your component to include a CSS file? Typical Wicket:

```java
@Override
public void renderHead(IHeaderResponse response)
{
    response.renderCSSReference(
        new PackageResourceReference(Application.get().getClass(), "screen.css"));
}
```

With Shortcuts:

```java
add(cssReference("screen.css"));
```

Or creating a LoadableDetachableModel? Normally in Wicket:

```java
new LoadableDetachableModel<User>() {
    @Override
    protected User load()
    {
        return userService.currentUser();
    }
};
```

With Shortcuts:

```java
loadedModel(userService, "currentUser");
```

## Handy components

The [fiftyfive-wicket-core][ffwcore] library includes a bunch of small but useful components for
making Wicket development much easier. Here are some examples:

* [CountLabel][countl] helps you pluralize labels for numeric data
* [TruncatedLabel][trunc] intelligently abbreviates long text data with an ellipsis
* [RadioChoicesListView][choices] is a more flexible way to build radio groups with custom markup
* [Prompt][prompt] lets you build nice forms by automatically wrapping form fields in appropriate 
  CSS classes with inline feedback messages when validation fails


## Testing tools

The [fiftyfive-wicket-test][ffwtest] library contains extra tools for writing good unit tests for 
Wicket pages and components. Here are some highlights:

* [assertValidMarkup][avalid] auto-detects XHTML and HTML5 flavors of markup and asserts the markup 
  is valid
* [startComponentWithHtml][startc] runs a Wicket component in isolation so that you don't have
  to create an entire page class and HTML file just to write unit tests
* [assertXPath][axpath] lets you assert the correctness of a Wicket component by using xpath
  expressions to match the component's rendered output


## Apache Shiro Security

The [fiftyfive-wicket-shiro][ffshiro] library provides simple and powerful login, logout and
authorization features by integrating Wicket with the [Apache Shiro][shiro] security framework. The
fiftyfive-wicket archetype generates a project with two hard-coded user accounts, one of which has
the "admin" role. See the security system in action by trying to visit the
[administration page][admin] in the sample project.

Projects generated by the fiftyfive-wicket archetype include:

* A sample login page that you can customize
* Standard setup procedures in `web.xml` and `WicketApplication.java`
* Plaintext user accounts defined in `src/main/resources/shiro.ini`
* Login/logout link at the top of every page, added in `BasePage.java`
* An example of how to restrict a page to authenticated admin users in `AdminstrationPage.java`
* `BaseWicketUnitTest.java`, which extends [AbstractShiroJUnit4Tests][shirotests] to allow Shiro 
  security state (logged in, logged out, current roles, permissions) to be mocked during testing of 
  your secured Wicket pages and components

Refer to the [fiftyfive-wicket-shiro documentation][ffshiro] for more information.


## JavaScript

We've streamlined how JavaScript works with Wicket by building the [fiftyfive-wicket-js][ffwjs]
library. This library focuses on solving three big problems that Wicket developers encounter:

1. **Components:** Wicket components that use JavaScript need an elegant way to glue the Java and
   JavaScript sides together. How do we accomplish this without mixing JavaScript strings into
   our Java code?
2. **Dependencies:** Components will often require a JavaScript library be present on the page. That 
   library in turn may require other JavaScript libraries, and so on. How do we keep track of these 
   dependencies and ensure the right files are included on the page in the correct order?
3. **Merging:** Now assuming we've got all the right JavaScript references on the page, how do we 
   deliver them efficiently to the browser as a single merged file?

Here are the solutions we offer:

1. [DomReadyTemplate][dready] for linking but cleanly separating Java and JavaScript
2. Sprockets-like syntax with [JavaScriptDependency][jsdep] for declaring library dependencies
3. [MergedJavaScriptBuilder][jsbuild] for declaratively merging JavaScript without needing to
   modify your component code


## Jetty

Our archetype includes the necessary [Jetty][jetty] magic in the `pom.xml` file, enabling you to run
the project with a simple `mvn jetty:run`. Plus:

* When run via `mvn jetty:run`, Wicket will be placed into development mode by default. However,
  when you package the project as a WAR, it will run in deployment mode. If for some reason you wish
  to run locally in deployment mode, use `mvn jetty:run -Dwicket.configuration=deployment`.
* An embedded Jetty is also provided in `Start.java`. If you are using an IDE like Eclipse, you can
  right-click this class and choose `Debug…` to run the project in your IDE debugger via Jetty. Not
  only do you get debugging tools, but in many cases changes to Java files will be picked up on the
  fly without requiring a server restart.

## Spring Framework

The dependency injection pattern is a key part of making Wicket applications easy to test and
maintain. [Spring][spring] is well supported by Wicket and is our framework of choice for this 
purpose. You'll find the following Spring integration in the fiftyfive-wicket archetype:

* By extending [FoundationSpringApplication][ffspring], `WicketApplication.java` initializes
  `@SpringBean` support, thereby allowing Spring-managed beans to be injected into your Wicket pages
  and components at runtime.
* `BaseWicketUnitTest.java` establishes a pattern for testing these Wicket pages and components in
  isolation by making it easy to provide [Mockito][mockito] mocks of `@SpringBean` injected objects.
* Spring and its appropriate servlet helpers are initialized in `web.xml`.
* The Spring context is defined and loaded from `src/main/resources/<project-name>-context.xml`.
  Your bean definitions go here.
* Spring's powerful [property placeholder][pplace] system can be used for application configuration.
  During development, properties are loaded from `<project-name>.properties` in the project root.
  During deployment, place appropriate deployment configuration in a `<project-name>.properties`
  file and instruct the servlet container to include this in the classpath.

See the official Wicket wiki for a good explanation of
[how Spring and Wicket work together][wicketspring].


## Maven Dependencies

If you aren't using the fiftyfive-wicket archetype (as described in the quick start section above),
you can add the core 55 Minutes Wicket library to an existing project using the following maven 
dependency:

```xml
<dependency>
  <groupId>com.55minutes</groupId>
  <artifactId>fiftyfive-wicket-core</artifactId>
  <version>3.0.2</version>
</dependency>
```

Repeat for the other artifacts of fiftyfive-wicket you wish to use:

* fiftyfive-wicket-js
* fiftyfive-wicket-shiro
* fiftyfive-wicket-test

Note that since our artifacts aren't in the central maven repository, you'll need to include the 
following snippet:

```xml
<repository>
  <id>fiftyfive-opensource-releases</id>
  <name>55 Minutes Open Source Maven Releases Repository</name>
  <url>http://opensource.55minutes.com/maven-releases</url>
  <releases><enabled>true</enabled></releases>
  <snapshots><enabled>false</enabled></snapshots>
</repository>
```



[jetty]:http://jetty.codehaus.org/jetty/
[shiro]:http://shiro.apache.org/
[ffshiro]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/shiro/ShiroWicketPlugin.html
[admin]:http://localhost:8080/admin
[shirotests]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/shiro/test/AbstractShiroJUnit4Tests.html
[pplace]:http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/beans.html#beans-factory-placeholderconfigurer
[spring]:http://www.springsource.org/about
[ffspring]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/spring/FoundationSpringApplication.html
[mockito]:http://code.google.com/p/mockito/
[wicketspring]:https://cwiki.apache.org/confluence/display/WICKET/Spring
[sass]:http://sass-lang.com/
[compass]:http://compass-style.org/
[cssf]:https://github.com/55minutes/css3-foundation
[shortcuts]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/util/Shortcuts.html
[ffwcore]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-core/3.0.2/
[prompt]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/feedback/Prompt.html
[countl]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/basic/CountLabel.html
[trunc]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/basic/TruncatedLabel.html
[choices]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/form/RadioChoicesListView.html
[ffwtest]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/test/package-summary.html
[avalid]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/fiftyfive/wicket/test/WicketTestUtils.html#assertValidMarkup(org.apache.wicket.util.tester.WicketTester)
[startc]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/fiftyfive/wicket/test/WicketTestUtils.html#startComponentWithHtml(org.apache.wicket.util.tester.WicketTester,%20org.apache.wicket.Component,%20java.lang.String)
[axpath]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/fiftyfive/wicket/test/WicketTestUtils.html#assertXPath(org.apache.wicket.util.tester.WicketTester,%20java.lang.String)
[ffwjs]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/js/package-summary.html
[dready]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/js/DomReadyTemplate.html
[jsdep]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/js/JavaScriptDependency.html
[jsbuild]:http://opensource.55minutes.com/apidocs/fiftyfive-wicket-all/3.0.2/index.html?fiftyfive/wicket/js/MergedJavaScriptBuilder.html
[readme]:https://github.com/55minutes/fiftyfive-wicket/blob/master/fiftyfive-wicket-archetype/src/main/resources/archetype-resources/README.md
