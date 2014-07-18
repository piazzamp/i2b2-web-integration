(ns org.healthsciencessc.i2b2.webclient.core
  (:require [org.healthsciencessc.i2b2.webclient.auth :as auth]
            [clojure.tools.logging :as logging]
            [compojure.handler :as handler]
            [hiccup.middleware :as hicware]
            [pliant.configure.runtime :as runtime]
            [pliant.webpoint.middleware :as webware]
            [pliant.webpoint.request :as webquest]
            [ring.middleware.content-type :as content-type]
            [sandbar.stateful-session :as sandbar]
            [org.healthsciencessc.i2b2.webclient.process.init])
  (:use     [pliant.process :only [defprocess]]))


(def app (-> 
           (webware/inject-routes           ;; Handler to map URI to process and execute
             auth/authentication-handler    ;; Handler to ensure session is authenticated
             sandbar/wrap-stateful-session) ;; Handler to enable session handling via sandbar
           (webware/wrap-resource "public") ;; Handler to make resources/public items in search path
           content-type/wrap-content-type   ;; Puts a content-type on the reponse if one is not set already.
           handler/site))                   ;; Standard wrapping for a JEE request to Compojure request.

(defn init
  "Initialization function for the web application."
  []
  (logging/info "Initializing I2B2 WebClient Integration")
  (runtime/load-resources "i2b2/webclient/plugin.clj"))

(defprocess destroy
  "Extensible process to handle the application destroy event."
  []
  (logging/info "Destroying I2B2 WebClient Integration"))
