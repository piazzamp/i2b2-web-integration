(ns org.healthsciencessc.i2b2.webclient.process.home
  (:require [org.healthsciencessc.i2b2.webclient.respond :as respond]
            [pliant.webpoint.request :as endpoint]
	    [clojure.tools.logging :as logging])
  (:use     [pliant.process :only [defprocess as-method]]))


;; Override this when functionality is needed to ensure a user has done something prior to being given access 
;; to I2B2
(defprocess view-home
  "Provides a placeholder to the root path.  By default, this will just redirect to the viewer.htm page"
  [request]
  (respond/redirect request "/viewer.htm") 
  )

(as-method view-home endpoint/endpoints "get")
