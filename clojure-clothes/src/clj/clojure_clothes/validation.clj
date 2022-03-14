(ns clojure-clothes.validation
  (:require
   [clojure.tools.logging :as log]
   [clojure-clothes.db-interface :as dbi]
   [clojure-clothes.db.core :as db]
   [struct.core :as stc]))

(def order-schema
  {:email [stc/required stc/email]
   :fullname [stc/required stc/string]
   :address1 [stc/required stc/string]
   :address2 [stc/string]
   :postcode [stc/required stc/string]
   :design [stc/required stc/map]
   :sku [stc/required stc/map]})

(defn validate-order [params]
  (first (stc/validate params order-schema)))

(def not-empty? (complement empty?))

(defn get-not-in-stock [params]
  (let [SKUs (get params :sku)
        sku-quantities (frequencies (vals SKUs))
        sku-not-in-stock (filter (fn [[k v]] (false? (dbi/in-stock? k v))) sku-quantities)]
    sku-not-in-stock))

(defn validate-order-in-stock [params]
  (let [sku-not-in-stock (get-not-in-stock params)]
    (= (count sku-not-in-stock) 0)))

(defn validate-order-exists [params]
  (not-empty? (db/get-order (get params :oid))))
