(ns org.healthsciencessc.i2b2.webclient.auth
  (:refer-clojure :exclude [first])
  (:require [org.healthsciencessc.i2b2.webclient.pm :as pm]
            [org.healthsciencessc.i2b2.webclient.respond :as respond]
            [pliant.webpoint.common :as common]
            [ring.util.response :as response]
            [sandbar.stateful-session :as sandbar])
  (:use     [pliant.process :only [defprocess]]))


(defprocess set-user
  "Saves the provided data as the current user of the session.  Does not do any validation."
  [user]
  (sandbar/session-put! :user user))


(defprocess get-user
  "Gets the data session that represents the current user if set, else returns nil.  Does not do any validation."
  []
  (sandbar/session-get :user))


(defprocess credentials->user
  "Uses the culled credentials to obtain the user record and it's available domain roles.  "
  [credentials]
  (let [user-id (:user-id credentials)
        password (:provisional-password credentials)]
    (if (and user-id password)
      (if-let [user (pm/user user-id)]  ;;could collapse into a when-let
        (do
          (pm/change-password user-id password)
          (assoc user :password password :roles
            (if-let [roles (pm/user-roles user-id)]
              roles
              [])))))))


(defprocess request->credentials
  "This method is responsible for culling the user information from the request.  By default, it returns
   nil causing all authentication to fail.

   At a minimum, the credentials returned must have a :user-id value to match againts the user records
   in the database, and a :provisional-password which is used to replace the current password in the system."
  [request]
  {:user-id "demo" :provisional-password "demouser"})


(defn authenticated?
  "Checks if the session has a valid user associated with it."
  []
  (not (nil? (get-user))))


(defn authenticate
  "Attempts to authenticate the current session by using the request to find valid credentials."
  ([request]
    (let [user (credentials->user (request->credentials request))]
      (if user  ;;consider replacing with when
        (do
          (set-user user)
          user)))))


(defn authentication-handler
  "A Ring/Compojure handler for ensuring the current session is either authenticated or the request path
   starts with '/unauthorized'."
  [handler]
  (fn [request]
    (let [path (common/path request)]
      (if (or (authenticated?)
              (authenticate request)
              (re-find #"^\/unauthorized" path)
              (re-find #"^\/scripts" path)
              (re-find #"^\/css" path))
        (handler request)
        (respond/redirect request "/unauthorized")))))
