The following directories and files should *not* be added to source control.
When adding this project to a subversion repository or other SCM system, make
sure these files are not included, and set up rules (e.g. svn:ignore) to
prevent them from being added in the future.

EXCLUDE THESE PATHS FROM SOURCE CONTROL:

  target
  ${artifactId}.properties
  log4j.properties
  src/test/resources/log4j.properties

The items should be excluded from source control because they are temporary
build products (the target directory), or they are environment-specific
configuration files (*.properties) that may vary per developer or runtime
environment. These files are automatically generated when maven is first
executed.
