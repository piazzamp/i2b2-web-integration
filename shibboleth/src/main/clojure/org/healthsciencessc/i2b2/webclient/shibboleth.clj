(ns org.healthsciencessc.i2b2.webclient.shibboleth
  (:require [clojure.tools.logging :as logging]
            [org.healthsciencessc.i2b2.webclient.config :as config]
            [org.healthsciencessc.i2b2.webclient.auth :as auth]
	    [clojure.tools.logging :as logging])
  (:use     [pliant.process :only [deflayer]]))


;; The 'prefix' property represents the optional prefix value you can have shibboleth prepend to 
;; the shibboleth attribute names.  This will be prepended to any default or set value when 
;; looking for the shibboleth attributes.
(def prefix (config/lookup :plugin.shibboleth.prefix ""))

;; The 'userid.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the value to match as the I2B2 User ID.
(def userid-header (str prefix (config/lookup :plugin.shibboleth.userid.header "shib-eduperson-principal-name"))
	(logging/debug (str "User ID grabbed from header: " (get-in request [:headers userid-header]))))

;; The 'session.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the value to use as the password for the current session.  Due to how an SSO 
;; must integrate into the I2B2 client, it is safest to change this value every session.
(def session-header (str prefix (config/lookup :plugin.shibboleth.password.header "shib-session-id")))

;; The 'email.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the email address of the person being authorized.
(def email-header (str prefix (config/lookup :plugin.shibboleth.email.header "shib-inetorgpersonmail")))

;; The 'commonname.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the full common name of the person.
(def commonname-header (str prefix (config/lookup :plugin.shibboleth.name.header "shib-person-commonname")))

;; The 'firstname.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the first name of the person being authorized.
(def firstname-header (str prefix (config/lookup :plugin.shibboleth.firstname.header "shib-inetorgperson-givenname")))

;; The 'lastname.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the last name of the person being authorized.
(def lastname-header (str prefix (config/lookup :plugin.shibboleth.lastname.header "shib-person-surname")))

;; Provides a shibboleth implementation layer to obtain the requestors credentials from the 
;; shibboleth attributes available within the request.  This requires that any call into 
;; the client must already have been authenticated through shibboleth, unless the request 
;; is has a path starting with /unauthorized.
(deflayer auth/request->credentials shib-request->credentials
  [request]
  (let [user-id (get-in request [:headers userid-header])
        password (get-in request [:headers session-header])]
    (if (and user-id password)
      {:user-id user-id
       :provisional-password password
       :email (get-in request [:headers email-header])
       :name (or (get-in request [:headers commonname-header])
                 (str (get-in request [:headers firstname-header]) 
                      " " 
                      (get-in request [:headers lastname-header])))
       :full-name (str (get-in request [:headers firstname-header]) (get-in request [:headers lastname-header]))})))
