(ns clojure-clothes.db-interaction
  (:require
   [clojure-clothes.db.core :as db]
   [clojure-clothes.util :as util]))

(defn get-products-full []
  (vec (map util/parse-sku (db/get-products))))

(defn in-stock?[sku quantity-to-buy]
  (let [quantity (get (db/get-product-by-sku sku) :quantity)]
    (> (- quantity quantity-to-buy) 0)))
