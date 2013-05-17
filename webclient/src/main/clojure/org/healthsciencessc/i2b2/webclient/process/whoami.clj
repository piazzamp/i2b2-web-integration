(ns org.healthsciencessc.i2b2.webclient.process.whoami
  (:require [org.healthsciencessc.i2b2.webclient.ui.layout :as layout]
            [org.healthsciencessc.i2b2.webclient.auth :as auth]
            [pliant.webpoint.request :as endpoint])
  (:use     [pliant.process :only [defprocess as-method]]))


;; Generic Unauthorized Landing Page
(defprocess view-whoami
  "Generates the unauthorized page."
  [request]
  (let [user (auth/get-user)]
    (layout/render-page request {:title "Who Am I? Well Let Me Tell You"}
                        (str "User ID is '" (:user-id user) "' and Provisional Password is '" 
                             (:provisional-password user) "'."))))

(as-method view-whoami endpoint/endpoints "get-whoami")
