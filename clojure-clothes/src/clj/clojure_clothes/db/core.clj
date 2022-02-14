(ns clojure-clothes.db.core
  (:require
   [monger.core :as mg]
   [monger.collection :as mc]
   [mount.core :refer [defstate]]
   [monger.operators :refer :all])
  (:import org.bson.types.ObjectId))

(let [conn (mg/connect)
      db   (mg/get-db conn "clojure-clothes")]

(defn create-user [user]
  (mc/insert db "users" user))

(defn update-user [id first-name last-name email]
  (mc/update db "users" {:_id id}
             {$set {:first_name first-name
                    :last_name last-name
                    :email email}}))

(defn get-user [id]
  (mc/find-one-as-map db "users" {:_id id}))

(defn populate-with-products []
  (mc/insert db "products" {:SKU "ee"
                            :name "sdf"
                            :size "sdf"
                            :colour "sdf"
                            :quantity 2}))

(defn update-product []
  (mc/update db "products" {:_id (ObjectId. "620aa2797edd2d2fc42361b5")}
             {$set {:SKU "EDITED LAD"
                    :name "sdf"
                    :size "sdf"
                    :colour "sdf"
                    :quantity 2}}))
)
