# An important note about Compass

This project uses [Compass][1] for stylesheet authoring. All styles are written in `.scss` files
and then compiled into `.css` during the `mvn compile` step. For this to work **you must install
the following ruby gems**:

    gem install compass compass-colors sassy-buttons

Whenever you use maven to compile or package this project, the `.scss` will be recompiled
automatically. If you want the `.scss` files to be compiled on the fly as you edit them during
development, simply run these commands:

    cd src/main/resources/${package.replace('.','/')}/styles
    compass watch

If you do not wish to use compass, be sure to remove the `compile-scss` execution from the
`pom.xml` file in order to eliminate the `mvn compile` magic.

[1]:http://compass-style.org/
