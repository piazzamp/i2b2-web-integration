(ns org.healthsciencessc.i2b2.webclient.config
  (:require [pliant.configure.props :as props]
            [pliant.configure.sniff :as sniff]))

;; The properties configuration that is used by the web-integrator and it's plugins.
(def config (props/slurp-config "i2b2-wci.properties" (sniff/sniff "I2B2WCIPKEY")))

(defn lookup
  "A function for looking up values in"
  ([key] (lookup key nil))
  ([key default-value]
    (cond
      (keyword? key) (get config key default-value)
      (string? key) (or (get config key default-value) 
                        (get config (keyword key) default-value))
      :else (or (get config (str key) default-value) 
                        (get config (keyword (str key)) default-value)))))