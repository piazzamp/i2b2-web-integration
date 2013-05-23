## About

The I2B2 WebClient Integration platform is developed using [Clojure](http://clojure.org), and manages it's projects using the [Leiningen](http://leiningen.org/) build tool.  [Leiningen](http://leiningen.org/) provides core development management functionality on top of a plugin based architecture to extend the core functionality.

## Packaging

The I2B2 WebClient Integration Platform contains projects that generate either JARs or a WAR.  Since commands for building different artifacts in Leiningen are different, we are using the lein-package plugin to provide a single command for packaging up projects, whether they are JARs or WARs.

The version of lein-package used requires that the version of Leiningen be 2.0.  If you plan to use the native ``lein jar`` or ``lein ring uberwar`` commands to build the individual projects then you do not need to worry about this compatibility.  If you plan to use ``lein package`` to generate the artifacts, then you do need to be version compliant.

## Building

The following examples make the assuption that your are starting in the base directory of the project source.

#### Build All Projects

To build all of the projects at the same time, go to the root of the project and issue the command:

    lein sub do clean, package
    
The WAR that is generated will be a plugin-free war.

#### Build And Install All Projects

To build all of the projects at the same time and install them in your local repository, go to the root of the project and issue the command:

    lein sub do clean, install

The WAR that is generated will be a plugin-free war.

#### Build WAR With All Core Plugins

To build the WAR with all of the default plugins embedded in the WAR, you will need to first run the command to build and install all of the projects, then issue the following commands:

    cd web-integrator
    lein with-profiles all do clean, package


## License

Copyright © 2013 Health Sciences of South Carolina

Distributed under the Eclipse Public License, the same as Clojure.

