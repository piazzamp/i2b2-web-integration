(ns org.healthsciencessc.i2b2.webclient.text
  (:require [pliant.configure.props :as props]))

(def ^:private ui-content (props/overlay-all-props 
                            "i2b2-wci-text.properties" 
                            "i2b2-wci-text-override.properties"))

;; Text Retrieval Functions
(defn text
  ([key] (text key {}))
  ([key options]
    (or (ui-content key) (name key))))


(defn format-text
  [key {:keys [args] :as options}]
  (let [value (text key options)]
    (apply format value args)))
