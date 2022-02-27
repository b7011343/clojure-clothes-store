(ns clojure-clothes.util
  (:require
   [clojure-clothes.db.core :as db]
   [clojure.string :as str]
   [clojure.data.json :as json]))

;; Constants
(def STANDARD-PRICE 9.99)
(def SUPREME-PRICE 15.99)

;; Util functions
(defn parse-sku [product]
  (let [sku (get product :SKU)
        name (get product :name)
        quantity (get product :quantity)
        split-sku (str/split sku #"-")
        json-file (json/read-str (slurp "resources/sku-decoder.json") :key-fn keyword)
        quality (get (get json-file :quality) (keyword (get split-sku 2)))
        price (cond
                (= quality "Supreme") SUPREME-PRICE
                (= quality "Standard") STANDARD-PRICE)]
    {:SKU sku
     :name name
     :quantity quantity
     :color (get (get json-file :color) (keyword (get split-sku 0)))
     :size (get (get json-file :size) (keyword (get split-sku 1)))
     :quality quality
     :price price}))

(defn calculate-total-price [order]
  (apply + (map :price order)))

(defn check-stock [product-id quantity-to-buy]
  (let [quantity (get (db/get-product product-id) :quantity)]
    (> 0 (- quantity quantity-to-buy))))
