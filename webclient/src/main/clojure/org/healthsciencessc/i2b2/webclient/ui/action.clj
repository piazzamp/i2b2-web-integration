;; Provides helper functions for generating certain jquery scripts on the browser.
(ns org.healthsciencessc.i2b2.webclient.ui.action)


(defn button-link
  "Creates a link that represents a button on the client."
  ([url] (button-link url url nil))
  ([url label] (button-link url label nil))
  ([url label target]
    [:a.webclient-action {:target target :href url} label]))
