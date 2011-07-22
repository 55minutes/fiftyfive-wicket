# Proposed overhaul and simplification of wicket-shiro

## GOALS

The wicketstuff-core wicket-shiro project was written back when when Shiro was
known as jSecurity or Ki. A lot has changed since then.

I propose replacing wicket-shiro with an improved project that does the
following:

1. Switch to using Shiro's own annotations (@RequiresRole,
@RequiresAuthentication, etc.) rather than inventing our own. Less code to
maintain means fewer potential bugs, which is an especially good thing for a
security framework. This also will mean more consistent security annotations
throughout an application that uses Shiro on multiple tiers.

2. Move the various pagestore related classes into a separate module,
wicketstuff-shiro-pagestore. These classes implement a Wicket second-level
pagestore on top of Shiro's native cluster-friendly sessions. This strikes me as
an advanced and uncommon deployment scenario; I don't think it belongs in the
core wicket-shiro JAR.

3. The current wicket-shiro provides custom annotations and an API for mapping
Shiro security constraint violations to error pages and messages. Instead of
having a custom construct, I think we should just throw an appropriate exception
when constraints are violated. With exceptions, there are already powerful
facilities in Wicket for handling exception mapping, namely Wicket 1.5's
IRequestCycleListener. I think the wicket-shiro project should take care of core
Shiro concerns, and as much possible leave customization in the hands of
existing Wicket facilities.

4. Provide an easy-to-use set of scaffolding in the form of login page, login
link, logout link, etc. to allow someone new to the framework to get up and
running quickly.


## SOLUTION PROPOSALS

### Single Class

For Wicket and Shiro to work together, we need to supply Wicket with both an
IAuthorizationStrategy and an IUnauthorizedInstantiationListener (and in Wicket
1.5, probably an IRequestCycleListener as well). Furthermore these components
need to share data and logic in order to be effective.

Since it doesn't make sense for an application to use one without the other, I
propose we create a single class that implements both interfaces. This keeps the
Shiro-Wicket logic together rather than artificially splitting it apart.

It also makes life easier for the application developer, since she will only
need to instantiate and configure a single object. I propose naming this
all-in-one class ShiroWicketPlugin, but I am open to other suggestions.


### Builder Pattern

Since this single class needs to be installed in two places within a Wicket
application (authz strategy and unauthz listener), and because there are
configuration options (login page and unauthorized page), installation has the
potential to be verbose and error-prone.

A builder pattern would be appropriate, because:

1. a builder can perform all the necessary installation steps with a single
convenience method, eliminating the possibility of erroneous or incomplete
installs

2. new configuration options can be added in future versions of the builder as
additional setter methods without breaking backwards compatibility

3. application code is more readable

Proposal:

    public class MyApplication extends WebApplication
    {
        @Override
        protected void init()
        {
            super.init();
            new ShiroWicketPlugin()
                .mountLoginPage("login", MyLoginPage.class)
                .setUnauthorizedPage(MyAccessDeniedPage.class)
                .install(this);
        }
    }


### Exception Handling

Many application developers will likely wish to use Shiro at all tiers of their
application, not just at the Wicket layer. Authorization checks in particular
could happen at the service/business logic tier, especially in cases where
multiple presentation tiers (e.g. Wicket, Flex) are sharing the same backend.

Wicket's IUnauthorizedInstantiationListener takes care of components that are
explicitly blocked due to logic at the Wicket layer, but it does not handle
exceptions thrown by the backend that indicate authorization failures, namely
Shiro's AuthorizationException.

Wicket 1.5 offers a clean way to handle this in a plugin fashion by using the
IRequestCycleListener interface. Our ShiroWicketPlugin can therefore implement
this interface, and since it already has knowledge of the login and
unauthorized error pages, it redirect to these based on the exception being
handled.
