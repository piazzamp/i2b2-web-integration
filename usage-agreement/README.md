## About

The usage-agreement plugin provides the ability to force users to agree to a usage agreement prior to being able to use the I2B2 web client.

## Deployment

To deploy the plugin, either embed it into the web-integrator WAR during it's build process, or drop the generated JAR onto into the ``lib`` directory on the JBoss 4 server.

In JBoss AS 7, you must add a module to ``<jboss home>/modules``, add the html to that module and declare that module as a dependency in ``wci.war/META-INF/MANIFEST.MF``. Check out the [JBoss guide](https://community.jboss.org/wiki/HowToPutAnExternalFileInTheClasspath).

## Configuration

The pluging requires that the usage agreement text be found in a file called ``i2b2-usage-agreement.htm`` at the root of the classpath.  On JBoss, the file can be placed in the ``conf`` directory.  The content of the file can be any valid HTML markup.

## License

Copyright © 2013 Health Sciences of South Carolina

Distributed under the Eclipse Public License, the same as Clojure.
