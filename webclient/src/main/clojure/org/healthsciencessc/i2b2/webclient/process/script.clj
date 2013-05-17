(ns org.healthsciencessc.i2b2.webclient.process.script
  (:require [clojure.string :refer [join]]
            [clojure.data.json :as json]
            [org.healthsciencessc.i2b2.webclient.auth :as auth]
            [org.healthsciencessc.i2b2.webclient.respond :as respond]
            [pliant.configure.resources :as resources]
            [pliant.webpoint.request :as endpoint]
            [pliant.process :refer [defprocess as-method deflayer callback]]))


(defprocess session-links
  "A process to register layers which can modify the links that are shown while a user is in 
   an authenticated session.  Links can be added to the by using either:

    (callback #(cons ['url' 'label'] %))
   
   -or-

    (callback #(conj % ['url' 'label']))"
  []
  '())

(defprocess nosession-links
  "A process to register layers which can add links that are shown while a user is not 
   in an authenticated session.  These are links that are shown until the user chooses a project 
   and submits it.  Links can be added to the bootstrap file by using either:

    (callback #(cons ['url' 'label'] %))
   
   -or-

    (callback #(conj % ['url' 'label']))"
  []
  '())

(defprocess prerequisites
  "A process to register descriptions of prerequisites that have not been met yet. This provides 
   a way to stop the user from using I2B2 if they have somehow circumvented the normal workflow, 
   and that workflow has a prerequisite like a data usage agreement. Prerequisites can be added 
   to the bootstrap file by using either:

    (callback #(cons \"You are required to sign off on the Data Usage Agreement prior to using I2B2\" %))
   
   -or-

    (callback #(conj % \"You are required to sign off on the Data Usage Agreement prior to using I2B2\"))"
  []
  '())


(defprocess before-init-events
  "A process to register layers which can add javascript functions that will be executed during 
   the BeforeInit event.  Event functions can be added to the bootstrap file by using either:

    (callback #(cons \"function(){doSomething()}\" %))
   
   -or-

    (callback #(conj % \"function(){doSomething()}\"))"
  []
  '())


(defprocess before-login-events
  "A process to register layers which can add javascript functions that will be executed during 
   the BeforeInit event.  Event functions can be added to the bootstrap file by using either:

    (callback #(cons \"function(){doSomething()}\" %))
   
   -or-

    (callback #(conj % \"function(){doSomething()}\"))"
  []
  '())


(defprocess after-login-events
  "A process to register layers which can add javascript functions that will be executed during 
   the BeforeInit event.  Event functions can be added to the bootstrap file by using either:

    (callback #(cons \"function(){doSomething()}\" %))
   
   -or-

    (callback #(conj % \"function(){doSomething()}\"))"
  []
  '())

;; Default New Line Character
(def nl "\n")

(defn seq->lined
  "Joins strings in a sequence using a new line."
  [s]
  (join nl s))

(defn vargs->lined
  "Joins var arg strings using a new line."
  [& s]
  (if (> (count s) 1)
    (seq->lined s)
    (first s)))

(defn build-push
  [array-path process]
  (if-let [items (process)]
    (seq->lined (map #(str "i2b2WCI." array-path ".push(" % ");") items))))

(defn build-event-registration
  [event-name event-process]
  (let [event-path (str "Events." event-name "")]
    (vargs->lined (str "if (i2b2WCI." event-path " == null){i2b2WCI." event-path " = []};")
                  (build-push event-path event-process))))

(defn credentials
  []
  (if-let [user (auth/get-user)]
    (do
      (vargs->lined (str "i2b2WCI.Credentials.UserID = \"" (:user-id user) "\";") 
                  (str "i2b2WCI.Credentials.Password = \"" (:password user) "\";") 
                  (str "i2b2WCI.Events.AfterLogin.push(function(){i2b2WCI.Credentials = null;})")
                  (build-push "Projects" #(map json/write-str (:projects user)))))))

;; The default bootstrap script. This is placed at the beginning of the file.
(def bootstrap-template (resources/with-resource "scripts/webclient/bootstrap.js" slurp))

;; Responsible for generating the script that is bootstrapped into the normal 
;; I2B2 WebClient to enable integration into this platform
(defprocess webclient-bootstrap
  [request]
  (respond/with-javascript (vargs->lined bootstrap-template
                                         (build-event-registration "BeforeInit" before-init-events)
                                         (build-event-registration "BeforeLogin" before-login-events)
                                         (build-event-registration "AfterLogin" after-login-events)
                                         (if (> (count (prerequisites)) 0)
                                           (build-push "Prerequisites" #(map json/write-str (prerequisites)))
                                           (credentials))
                                         (build-push "SessionLinks" #(map json/write-str (session-links)))
                                         (build-push "NoSessionLinks" #(map json/write-str (nosession-links))))))

(as-method webclient-bootstrap endpoint/endpoints "get-scripts-webclient-bootstrap.js")

;;(deflayer session-links test-session-links
;;  []
;;  (callback #(conj % ["http://www.healthsciencessc.org" "HSSC"] ["http://www.yahoo.com" "Yahoo!" "_blank"])))

;;(deflayer nosession-links test-nosession-links
;;  []
;;  (callback #(conj % ["http://www.musc.edu" "MUSC"] ["http://www.google.com" "Google" "_blank"])))


;;(deflayer before-init-events test-before-init-events-first
;;  []
;;  (callback #(conj % "function(){alert(\"First Before Init Event.\")}")))

;;(deflayer before-init-events test-before-init-events-second
;;  []
;;  (callback #(conj % "function(){alert(\"Second Before Init Event.\")}")))


;;(deflayer before-login-events test-before-login-events-first
;;  []
;;  (callback #(conj % "function(){alert(\"First Before Login Event.\")}")))

;;(deflayer before-login-events test-before-login-events-second
;;  []
;;  (callback #(conj % "function(){alert(\"Second Before Login Event.\")}")))


;;(deflayer after-login-events test-after-login-events-first
;;  []
;;  (callback #(conj % "function(){alert(\"First After Login Event.\")}")))

;;(deflayer after-login-events test-after-login-events-second
;;  []
;;  (callback #(conj % "function(){alert(\"Second After Login Event.\")}")))
