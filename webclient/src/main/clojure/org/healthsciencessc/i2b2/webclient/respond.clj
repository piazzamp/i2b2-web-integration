(ns org.healthsciencessc.i2b2.webclient.respond
  (:require [ring.util.response :as ring]))

;; Success(200) Responses
(defn with-xml
  [xml] (ring/content-type (ring/response xml) "text/xml"))


(defn with-javascript
  [javascript] (ring/content-type (ring/response javascript) "application/javascript"))


;; Redirect(300) Responses
(defn redirect
  [request url]
  (ring/redirect url))

;; Error(400) Responses
(defn not-found
  ([] (not-found "The requested resource was not found."))
  ([message] (ring/not-found {:message message})))


(defn forbidden
  ([] (forbidden "You do not have the authority to execute the requested process."))
  ([message]
    (ring/status (ring/response {:message message}) 403)))


(defn forbidden-view
  ([] (forbidden "You do not have the authority to execute the requested view."))
  ([message]
    (ring/status (ring/response {:message message}) 403)))


(defn with-error
  ([] (with-error "Unable to process the request"))
  ([message]
    (ring/status (ring/response {:message message}) 400)))

