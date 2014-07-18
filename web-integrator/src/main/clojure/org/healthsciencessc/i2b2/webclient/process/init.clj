;;a namespace that requires all namespaces in the process directory
;;	mainly to overcome the lack of importing packages with wildcards like in Java
;;	ie: import Java.util.*

(ns org.healthsciencessc.i2b2.webclient.process.init
  (:require [org.healthsciencessc.i2b2.webclient.process.home]
            [org.healthsciencessc.i2b2.webclient.process.script]
            [org.healthsciencessc.i2b2.webclient.process.unauthorized]
            [org.healthsciencessc.i2b2.webclient.process.whoami]))
