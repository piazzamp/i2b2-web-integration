(ns org.healthsciencessc.i2b2.webclient.auth
  (:require [org.healthsciencessc.i2b2.webclient.respond :as respond] 
            [pliant.webpoint.common :as common]
            [ring.util.response :as response]
            [sandbar.stateful-session :as sandbar]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as logging])
  (:use     [pliant.process :only [defprocess]]))


;; User State Methods
(defn set-user
  "Saves the provided data as the current user of the session.  Does not do any validation."
  [user]
  (sandbar/session-put! :user user))

(defn get-user
  "Gets the data session that represents the current user if set, else returns nil.  Does not do any validation."
  []
  (sandbar/session-get :user))

(defn to-hash
  "Uses MD5 to hash a value.  This is used to hash passwords prior to being inserted into the database."
  [^String value]
  (apply str
         (map (partial format "%x")
              (.digest (doto 
                         (java.security.MessageDigest/getInstance "MD5")
                         .reset
                         (.update (.getBytes value)))))))


;; The database configuration for accessing user and role records.  Utilizes the 
;; built in PM DataSource.
;;(def pm-db {:name "java:comp/env/PMBootStrapDS"})
(def pm-db {:name "java:PMBootStrapDS"})

(def select-user-sql 
  "SELECT USER_ID as \"user-id\", 
          FULL_NAME as \"full-name\", 
          PASSWORD as \"password\", 
          EMAIL as \"email\"
     FROM PM_USER_DATA
    WHERE USER_ID=?")


(def select-user-roles-sql 
  "SELECT r.PROJECT_ID as \"project-id\",
          p.PROJECT_NAME as \"project-name\", 
          r.USER_ROLE_CD as \"role\"
     FROM PM_PROJECT_USER_ROLES r,
          PM_PROJECT_DATA p
    WHERE r.PROJECT_ID = p.PROJECT_ID
      AND r.USER_ID=?")

(defn credentials->user
  "Uses the culled credentials to obtain the user record and it's available domain roles.  "
  [credentials]
  (let [user-id (:user-id credentials)
        password (:provisional-password credentials)]
    (if (and user-id password)
      (jdbc/with-connection pm-db
        (jdbc/with-query-results rs [select-user-sql user-id]
          (let [user (first rs)]
            (if user
              (do
                (jdbc/transaction 
                  (jdbc/update-values :PM_USER_DATA
                                      ["USER_ID=?" user-id]
                                      {:PASSWORD (to-hash password)
                                       :STATUS_CD "A"}))
                (assoc user :password password :projects 
                       (jdbc/with-query-results 
                         roles-rs [select-user-roles-sql user-id]
                         (reduce (fn [coll item]
                                   (let [proj (first (filter #(= (:id %) (:project-id item)) coll))]
                                     (if proj
                                       (conj (filter #(not= (:id %) (:project-id item)) coll) 
                                             (update-in proj  [:roles] #(conj % (:role item))))
                                       (conj coll {:id (:project-id item) :name (:project-name item) 
                                                   :roles [(:role item)]}))))
                                   [] roles-rs)))))))))))


(defn end-session
  "Destroys the current web session and deactivates the user in the database."
  []
  (let [user (get-user)]
    (if user
      (sandbar/destroy-session!)
      (jdbc/with-connection pm-db
         (jdbc/transaction 
           (jdbc/update-values :PM_USER_DATA
                               ["USER_ID=?" (:user-id user)]
                               {:STATUS_CD "D"}))))))


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
      (if user
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
