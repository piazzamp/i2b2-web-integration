(ns org.healthsciencessc.i2b2.webclient.usage-agreement
  (:require [org.healthsciencessc.i2b2.webclient.process.home :as home]
            [org.healthsciencessc.i2b2.webclient.process.script :as script]
            [org.healthsciencessc.i2b2.webclient.ui.action :as action]
            [org.healthsciencessc.i2b2.webclient.ui.layout :as layout]
            [org.healthsciencessc.i2b2.webclient.auth :as auth]
            [org.healthsciencessc.i2b2.webclient.respond :as respond]
            [org.healthsciencessc.i2b2.webclient.text :as text]
            [sandbar.stateful-session :as sandbar]
            [pliant.webpoint.request :as endpoint]
            [pliant.process :refer [defprocess deflayer as-method continue callback]]
            [clojure.tools.logging :as logging]))


(def agreement-text (let [resource (clojure.java.io/resource "i2b2-usage-agreement.htm")]
                      (if resource
                        (slurp resource))))
(if (> (count agreement-text) 10) (logging/info (apply str (concat "i2b2-usage-agreement.htm located!!!!! size=" (str (count agreement-text))))) (logging/info "i2b2-usage-agreement.htm not found!!!"))

(defn agree
  "Saves the provided data as the current user of the session.  Does not do any validation."
  []
  (sandbar/session-put! :plugin.usage-agreement.agree true))

(defn agreed?
  "Gets the data session that represents the current user if set, else returns nil.  Does not do any validation."
  []
  (true? (sandbar/session-get :plugin.usage-agreement.agree)))

(defn requires-agreement?
  []
  (and (not (nil? agreement-text)) (not (agreed?))))

(if (requires-agreement?) (logging/info "requires-agreement?=true") (logging/info "requires-agreement?=false"))

;; Provides a shibboleth implementation layer to obtain the requestors credentials from the 
;; shibboleth attributes available within the request.  This requires that any call into 
;; the client must already have been authenticated through shibboleth, unless the request 
;; is has a path starting with /unauthorized.
(deflayer home/view-home view-home-usage-agreement
  [request]
  (if (requires-agreement?)
    (let [user (auth/get-user)]
      (layout/render-page request {:title (text/text :plugin.usage-agreement.title)
                                   :usage-agreement true}
                          [:div.dua-container
                           [:div.dua-title (text/text :plugin.usage-agreement.message)]
                           [:div.dua-agreement agreement-text]
                           [:div.dua-action 
                            (action/button-link (text/text :plugin.usage-agreement.dissent.url)
                                                (text/text :plugin.usage-agreement.dissent.label))
                            (action/button-link "/plugin/usage-agreement/agree"
                                                (text/text :plugin.usage-agreement.consent.label))
                            ]]))
    (continue)))
  
;; A simple api for accepting the agreement fram a user
(defprocess api-usage-agreement
  "Generates the unauthorized page."
  [request]
  (agree)
  (respond/redirect request "/viewer.htm"))

(as-method api-usage-agreement endpoint/endpoints "get-plugin-usage-agreement-agree")

(deflayer script/prerequisites prerequisites-usage-agreement
  []
  (if (and (requires-agreement?) (not (agreed?)))
    (callback #(conj % (text/text :plugin.usage-agreement.prerequisite.message)))
    (continue)))


(deflayer layout/stylesheets prerequisites-usage-agreement
  [request options]
  (if (:usage-agreement options)
    (callback #(conj % "/css/user-agreement.css"))
    (continue)))
