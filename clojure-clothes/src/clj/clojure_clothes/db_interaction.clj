(ns clojure-clothes.db-interaction
  (:require
   [clojure-clothes.db.core :as db]
   [clojure-clothes.util :as util]))

(defn get-products-full []
  (vec (map util/parse-sku (db/get-products))))

(defn create-order [products]
  (map db/get-product))
