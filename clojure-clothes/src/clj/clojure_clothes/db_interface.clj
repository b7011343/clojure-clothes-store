(ns clojure-clothes.db-interface
  (:require
   [clojure.tools.logging :as log]
   [clojure-clothes.const :as c]
   [clojure-clothes.db.core :as db]
   [clojure-clothes.util :as util]))

(defn get-products-full []
  (vec (map util/parse-sku (db/get-products))))

(defn in-stock? [sku quantity-to-buy]
  (let [quantity (get (db/get-product-by-sku sku) :quantity)]
    (>= (- quantity quantity-to-buy) 0)))

(defn get-stock [sku]
  (let [product (db/get-product-by-sku sku)]
    (get product :quantity)))

(defn adjust-stock [sku-quantities]
  (let [keys (vec (keys sku-quantities))]
    (loop [i 0]
      (when (< i (count sku-quantities))
        (let [key (get keys i)
              order-quantity (get sku-quantities key)
              current-quantity (get-stock key)
              new-quantity (- current-quantity order-quantity)]
          (db/update-product-quantity key new-quantity)
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
    (db/create-order order)))
