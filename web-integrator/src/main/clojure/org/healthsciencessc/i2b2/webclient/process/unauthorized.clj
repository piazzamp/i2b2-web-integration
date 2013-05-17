(ns org.healthsciencessc.i2b2.webclient.process.unauthorized
  (:require [org.healthsciencessc.i2b2.webclient.ui.layout :as layout]
            [org.healthsciencessc.i2b2.webclient.text :as text]
            [pliant.webpoint.request :as endpoint])
  (:use     [pliant.process :only [defprocess as-method]]))


;; Generic Unauthorized Landing Page
(defprocess view-unauthorized
  "Generates the unauthorized page."
  [request]
  (layout/render-page request {:title (text/text :unauthorized.title) 
                           :links [["/unauthorized/register" "Register"]]}
                      (text/text :unauthorized.message)))

(as-method view-unauthorized endpoint/endpoints "get-unauthorized")


;; Generic Unauthorized Registration Landing Page
(defprocess view-unauthorized-register
  "Generates the unauthorized page."
  [request]
  (layout/render-page request {:title (text/text :unauthorized.register.title)}
                      (text/text :unauthorized.register.message)))

(as-method view-unauthorized-register endpoint/endpoints "get-unauthorized-register")