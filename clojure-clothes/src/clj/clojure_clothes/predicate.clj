(ns clojure-clothes.predicate
  (:require
   [clojure-clothes.const :as c]
   [clojure.spec.alpha :as s]))

(defn valid-id?
  "Takes a string and checks whether it is a valid
   MongoDb ObjectId"
  [id]
  {:pre [(s/valid? string? id)]
   :post [(s/valid? boolean? %)]}
  (= (count id) 24))

(defn product?
  "Predicate for a product"
  [product]
  {:post [(s/valid? boolean? %)]}
  (let [product-keys (keys product)
        correct-count? (= 8 (count product-keys))]
    (and correct-count?
         (some? (:_id product))
         ((and string? #(= 10 (count %))) (:SKU product))
         (string? (:name product))
         (number? (:quantity product))
         (string? (:color product))
         (string? (:size product))
         (string? (:quality product))
         (float? (:price product)))))

(defn products?
  "Predicate for a collection of products"
  [products]
  {:post [(s/valid? boolean? %)]}
  (and (coll? products)
       (every? product? products)))

(defn order?
  "Predicate for an order"
  [order]
  {:post [s/valid? boolean %]}
  (let [order-keys (keys order)
        correct-count? (= 9 (count order-keys))]
    (and correct-count?
         (some? (:_id order))
         (string? (:email order))
         (string? (:fullname order))
         (string? (:postcode order))
         (string? (:address1 order))
         (string? (:address2 order))
         (and string? (or (= c/STATUS_SHIPPED (:status order)) (= c/STATUS_ORDERED (:status order))))
         (coll? (:order order))
         (float? (:price order)))))

(defn orders?
  "Predicate for a collection of orders"
  [orders]
  {:post [(s/valid? boolean %)]}
  (and (coll? orders)
       (every? order? orders)))
