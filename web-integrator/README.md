## About

The web-integrator project is the core platform module responsible generating the hooks that get embedded into the I2B2 webclient application.

## Deployment

The web-integrator project builds a JEE compliant WAR that is deployed to the JBoss server that the I2B2 Project Manager module is deployed to.  No database configuration is required for the WCI to interact with the PM database as it reuses the datastore that is already configured for the PM module.

After you have [built the WAR according to the instructions](../doc/BUILD.md), deploy the WAR to the JBoss server, renaming it to ``wci.war``.  Then follow the [installation and configuration instructions](../doc/INSTALL.md).  Each plugin that is embedded into the WAR or dropped on the server classpath will have it's own configuration instructions.

## License

Copyright © 2013 Health Sciences of South Carolina

Distributed under the Eclipse Public License, the same as Clojure.
