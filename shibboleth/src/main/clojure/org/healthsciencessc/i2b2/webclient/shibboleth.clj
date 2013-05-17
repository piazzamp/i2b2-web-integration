(ns org.healthsciencessc.i2b2.webclient.shibboleth
  (:require [clojure.tools.logging :as logging]
            [pliant.configure.props :as props]
            [pliant.configure.sniff :as sniff]
            [org.healthsciencessc.i2b2.webclient.auth :as auth])
  (:use     [pliant.process :only [deflayer]]))

;; The optional properties configuration file that can change the default settings.
(def config (props/slurp-config "i2b2-shibboleth.props" (sniff/sniff "I2B2SHIB")))

;; The 'prefix' property represents the optional prefix value you can have shibboleth prepend to 
;; the shibboleth attribute names.  This will be prepended to any default or set value when 
;; looking for the shibboleth attributes.
(def prefix (:prefix config))

;; The 'userid.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the value to match as the I2B2 User ID.
(def userid-header (str prefix (or (:userid.header config) "Shib-EduPerson-Principal-Name")))

;; The 'session.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the value to use as the password for the current session.  Due to how an SSO 
;; must integrate into the I2B2 client, it is safest to change this value every session.
(def session-header (str prefix (or (:session.header config) "Shib-Session-ID")))

;; The 'email.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the email address of the person being authorized.
(def email-header (str prefix (or (:email.header config) "Shib-InetOrgPerson-mail")))

;; The 'firstname.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the first name of the person being authorized.
(def firstname-header (str prefix (or (:firstname.header config) "Shib-InetOrgPerson-givenName")))

;; The 'lastname.header' is the name of the property to find that name of the shibboleth attribute
;; that contains the last name of the person being authorized.
(def lastname-header (str prefix (or (:lastname.header config) "Shib-Person-surname")))

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
       :first-name (get-in request [:headers firstname-header])
       :last-name (get-in request [:headers lastname-header])
       :full-name (str (get-in request [:headers firstname-header]) (get-in request [:headers lastname-header]))})))
