(ns clojure-clothes.db-interaction
  (:require
   [clojure-clothes.db.core :as db]
   [clojure-clothes.util :as util]))

(defn get-products-full []
  (vec (map util/parse-sku (db/get-products))))

(defn check-stock [product-id quantity-to-buy]
  (let [quantity (get (db/get-product product-id) :quantity)]
    (> 0 (- quantity quantity-to-buy))))
