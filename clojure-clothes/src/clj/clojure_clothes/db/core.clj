(ns clojure-clothes.db.core
  (:require [clojure.data.json]
            [monger.core :as mg]
            [monger.collection :as mc]
            [mount.core :refer [defstate]]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]
            [clojure-clothes.const :as c]
            [clojure-clothes.util :as util]
            [monger.operators :refer :all])
  (:import org.bson.types.ObjectId))

(def conn (mg/connect))
(def db (mg/get-db conn "clojure-clothes"))

;; User
(defn create-user [user]
  (mc/insert db "users" user))

(defn update-user [id first-name last-name email]
  (mc/update db "users" {:_id id}
             {$set {:first_name first-name
                    :last_name last-name
                    :email email}}))

(defn get-user [id]
  (mc/find-one-as-map db "users" {:_id id}))

;; Products
(defn get-products []
  (into [] (mc/find-maps db "products")))

(defn get-product [id]
  (if (util/valid-id? id)
    (mc/find-one-as-map db "products" {:_id (ObjectId. id)})
    nil))

(defn update-product-quantity [sku new-quantity]
  (mc/update db "products" {:SKU sku}
             {$set {:quantity new-quantity}}))

(defn get-product-by-sku [sku]
  (mc/find-one-as-map db "products" {:SKU sku}))

;; Orders
(defn create-order [order]
  (mc/insert-and-return db "orders" order))

(defn get-order [id]
  (if (util/valid-id? id)
      (mc/find-one-as-map db "orders" {:_id (ObjectId. id)})
      nil))

(defn get-orders []
  (into [] (mc/find-maps db "orders")))

(defn get-awaiting-orders []
  (mc/find-maps db "orders" {:status c/STATUS_ORDERED}))

(defn get-processed-orders []
  (mc/find-maps db "orders" {:status c/STATUS_SHIPPED}))

(defn process-order [order-id]
  (mc/update db "orders" {:_id (ObjectId. order-id)}
             {$set {:status c/STATUS_SHIPPED}}))

(defn get-products-full []
  (vec (map util/parse-sku (get-products))))

(defn in-stock? [sku quantity-to-buy]
  (let [quantity (get (get-product-by-sku sku) :quantity)]
    (>= (- quantity quantity-to-buy) 0)))

(defn get-stock [sku]
  (let [product (get-product-by-sku sku)]
    (get product :quantity)))

(defn adjust-stock [sku-quantities]
  (let [keys (vec (keys sku-quantities))]
    (loop [i 0]
      (when (< i (count sku-quantities))
        (let [key (get keys i)
              order-quantity (get sku-quantities key)
              current-quantity (get-stock key)
              new-quantity (- current-quantity order-quantity)]
          (update-product-quantity key new-quantity)
          (recur (inc i)))))))

(defn generate-order [params]
  (let [fullname (get params :fullname)
        email (get params :email)
        address1 (get params :address1)
        address2 (get params :address2)
        postcode (get params :postcode)
        products-with-designs (vec (util/parse-order (get params :sku) (get params :design)))
        sku-list (util/sku-filter products-with-designs)
        sku-quantities (frequencies sku-list)
        total-price (util/calculate-total sku-quantities)
        order {:status c/STATUS_ORDERED
               :fullname fullname
               :email email
               :address1 address1
               :address2 address2
               :postcode postcode
               :price total-price
               :order products-with-designs}]
    (adjust-stock sku-quantities)
    (create-order order)))

(defn get-total-awaiting-orders []
  (count (get-awaiting-orders)))

(defn get-total-processed-orders []
  (count (get-processed-orders)))

(defn get-total-profit []
  (reduce + (map #(:price %) (get-processed-orders))))

;; Seeds the db with products from the seed-data.json file if the table doesn't already exist
(defn seed-db []
  (if-not (mc/exists? db "products")
    (let [seed-data (json/read-str (slurp "resources/seed-data.json") :key-fn keyword)]
      (log/info "Products table doesn't exit - seeding")
      (log/info (:products seed-data))
      (mc/insert-batch db "products" (:products seed-data)))
    (log/info "Products table already exists - not seeding")))
(seed-db)

;; References
;; https://stackoverflow.com/a/48515598/7259551
