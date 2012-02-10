Place your custom JavaScript files in this directory so that they can be
referenced using sprockets syntax within your app. For example, if you place
a file in this directory called "mylib.js", your Wicket components can
reference it like this:

add(new JavaScriptDependency("mylib"));

And your JS files can depend on it like this:

//= require mylib
