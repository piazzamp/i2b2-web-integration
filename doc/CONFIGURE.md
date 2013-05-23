## About

The I2B2 WebClient Integration platform is provides configuration capabilities for itself and it's plugins through the use of the [Pliant Configure](https://github.com/pliant/configure) library.  It is configured to use a properties file at the root of the classpath named ``i2b2-wci.properties``, and to sniff for a key called ``I2B2WCIPKEY`` whose value is used to decrypt encrypted property values (read the [Pliant Configure](https://github.com/pliant/configure) documentation for further detail.

## Plugin Configuration

If a plugin utilizes the platform configuration to determine it's behavior, you only need to place the properties they require in to the ``i2b2-wci.properties`` file on the base of the classpath.  If no file exists already, you can create the new file in the servers ``conf`` directory, which is included as part of the classpath.

Example Configuration:

    # Web-Integrator Properties - There are none yet.
    
    # Awesome Plugin Properties
    plugin.awesome.dostuff=Yes
    
    # Lame Plugin Properties
    plugin.lame.key=notawesome

## Plugin Developement

If you are developing a plugin providing configuration capabilities is simple.  Where every you need configuration values just require the ``org.healthsciencessc.i2b2.webclient.config`` and use it's lookup function.  Example

```clojure
(ns org.faber.i2b2.wci.myplugin
  (:require [org.healthsciencessc.i2b2.webclient.config :as config]))

(def my-property (config/lookup :plugin.awesome.dostuff))
```

## License

Copyright © 2013 Health Sciences of South Carolina

Distributed under the Eclipse Public License, the same as Clojure.

