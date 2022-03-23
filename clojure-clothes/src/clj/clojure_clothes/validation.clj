(ns clojure-clothes.validation
  (:require
   [clojure.tools.logging :as log]
   [clojure-clothes.predicate :as pred]
   [clojure-clothes.util :as util]
   [clojure-clothes.db.core :as db]
   [struct.core :as stc]))

(def order-schema
  "Schema for validation of order checkout form"
  {:email [stc/required stc/email]
   :fullname [stc/required stc/string]
   :address1 [stc/required stc/string]
   :address2 [stc/string]
   :postcode [stc/required stc/string]
   :design [stc/required stc/map]
   :sku [stc/required stc/map]})

(defn validate-order
  "Validates an order request"
  [params]
  (first (stc/validate params order-schema)))

(defn get-not-in-stock
  "Takes a vector of SKUs and returns a vector of all the SKUs
   that are not in stock"
  [params]
  (let [SKUs (get params :sku)
        sku-quantities (frequencies (vals SKUs))
        sku-not-in-stock (filter (fn [[k v]] (false? (db/in-stock? k v))) sku-quantities)]
    sku-not-in-stock))

(defn validate-order-in-stock
  "Validates that enough stock exists to fulfill
   an order"
  [params]
  (let [sku-not-in-stock (get-not-in-stock params)]
    (= (count sku-not-in-stock) 0)))

(defn validate-order-exists
  "Validates that an order exists"
  [params]
  (pred/not-empty? (db/get-order (get params :oid))))
