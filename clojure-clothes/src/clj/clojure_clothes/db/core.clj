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

(defn get-products
  "Returns all products"
  []
  (into [] (mc/find-maps db "products")))

(defn get-product
  "Takes an string ID and returns a product map, if
   it doesn't exist it returns nil"
  [id]
  (if (util/valid-id? id)
    (mc/find-one-as-map db "products" {:_id (ObjectId. id)})
    nil))

(defn update-product-quantity
  "Takes a SKU and a new quantity and updates
   that record in the db"
  [sku new-quantity]
  (mc/update db "products" {:SKU sku}
             {$set {:quantity new-quantity}}))

(defn get-product-by-sku
  "Takes a string SKU and returns product map"
  [sku]
  (mc/find-one-as-map db "products" {:SKU sku}))

(defn create-order
  "Inserts and returns order map into db"
  [order]
  (mc/insert-and-return db "orders" order))

(defn get-order
  "Takes an string ID and returns an order map, if
   it doesn't exist it returns nil"
  [id]
  (if (util/valid-id? id)
      (mc/find-one-as-map db "orders" {:_id (ObjectId. id)})
      nil))

(defn get-orders
  "Returns all orders"
  []
  (into [] (mc/find-maps db "orders")))

(defn get-awaiting-orders
  "Returns all orders that have not been processed"
  []
  (mc/find-maps db "orders" {:status c/STATUS_ORDERED}))

(defn get-processed-orders
  "Returns all orders that have been processed"
  []
  (mc/find-maps db "orders" {:status c/STATUS_SHIPPED}))

(defn process-order
  "Mark an order record as processed"
  [order-id]
  (mc/update db "orders" {:_id (ObjectId. order-id)}
             {$set {:status c/STATUS_SHIPPED}}))

(defn get-products-full
  "Returns all products with combined SKU information"
  []
  (vec (map util/parse-sku (get-products))))

(defn in-stock?
  "Predicate for if a product of a given SKU have enough
   stock to allow for an order of quantity quantity-to-buy"
  [sku quantity-to-buy]
  (let [quantity (get (get-product-by-sku sku) :quantity)]
    (>= (- quantity quantity-to-buy) 0)))

(defn get-stock
  "Returns the quantity in stock for a product given
   a SKU"
  [sku]
  (let [product (get-product-by-sku sku)]
    (get product :quantity)))

(defn adjust-stock
  "Takes a vector of maps where SKU is key and quantity
   is value and then adjusts their stock level in the db"
  [sku-quantities]
  (let [keys (vec (keys sku-quantities))]
    (loop [i 0]
      (when (< i (count sku-quantities))
        (let [key (get keys i)
              order-quantity (get sku-quantities key)
              current-quantity (get-stock key)
              new-quantity (- current-quantity order-quantity)]
          (update-product-quantity key new-quantity)
          (recur (inc i)))))))

(defn generate-order
  "Takes an order request, generates an order in the db,
   adjusts the stock for each of the items on the order
   and returns the order ID"
  [params]
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

(defn get-total-awaiting-orders
  "Returns the number of awaiting orders"
  []
  (count (get-awaiting-orders)))

(defn get-total-processed-orders
  "Returns the number of processed orders"
  []
  (count (get-processed-orders)))

(defn get-total-profit 
  "Returns the total profit from all historical orders"
  []
  (reduce + (map #(:price %) (get-processed-orders))))

(defn seed-db
  "Seeds the db with products from the seed-data.json
   file if the table doesn't already exist"
  []
  (if-not (mc/exists? db "products")
    (let [seed-data (json/read-str (slurp "resources/seed-data.json") :key-fn keyword)]
      (log/info "Products table doesn't exit - seeding")
      (log/info (:products seed-data))
      (mc/insert-batch db "products" (:products seed-data)))
    (log/info "Products table already exists - not seeding")))

(seed-db)

;; References
;; https://stackoverflow.com/a/48515598/7259551
