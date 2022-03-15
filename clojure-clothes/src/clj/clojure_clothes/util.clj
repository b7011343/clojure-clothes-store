(ns clojure-clothes.util
  (:require
   [clojure.tools.logging :as log]
   [clojure.string :as str]
   [clojure-clothes.const :as c]
   [clojure.data.json :as json]))

(defn parse-sku
  "Takes a product map containing a :SKU value and returns
   a new map containing additional :color, :size and :quality values"
  [product]
  (let [sku (get product :SKU)
        name (get product :name)
        quantity (get product :quantity)
        split-sku (str/split sku #"-")
        json-file (json/read-str (slurp "resources/sku-decoder.json") :key-fn keyword)
        quality (get (get json-file :quality) (keyword (get split-sku 2)))
        price (cond
                (= quality "Supreme") c/SUPREME-PRICE
                (= quality "Standard") c/STANDARD-PRICE)]
    {:SKU sku
     :name name
     :quantity quantity
     :color (get (get json-file :color) (keyword (get split-sku 0)))
     :size (get (get json-file :size) (keyword (get split-sku 1)))
     :quality quality
     :price price}))

(defn get-sku-price
  "Takes a string SKU and returns its price"
  [sku]
  (let [split-sku (str/split sku #"-")
        json-file (json/read-str (slurp "resources/sku-decoder.json") :key-fn keyword)
        quality (get (get json-file :quality) (keyword (get split-sku 2)))
        price (cond
                (= quality "Supreme") c/SUPREME-PRICE
                (= quality "Standard") c/STANDARD-PRICE)]
    price))

(defn parse-order
  "Takes 2 vectors of maps and combines them into a single
   vector of maps"
  [sku design]
  (let [sku-sorted (vec (into (sorted-map) sku))
        design-sorted (vec (into (sorted-map) design))]
    (for [x (range (count (keys sku)))]
      {:sku (last (get sku-sorted x)) :design (get (last (get design-sorted x)) 0)})))

(defn sku-filter
  "Takes a vector of maps and returns a vector of maps only
   containing the key :sku"
  [sku]
  (map #(get % :sku) sku))

(defn calculate-total
  "Takes a sequence of maps where the key is a SKU and the
   value is the number in the order and returns a total price"
  [sku-quantities]
  (let [mapped-prices (map (fn [[k v]] (* v (get-sku-price k))) sku-quantities)]
    (apply + mapped-prices)))

(defn valid-id?
  "Takes a string and checks whether it is a valid
   MongoDb ObjectId"
  [id]
  (= (count id) 24))

;; References
;; https://stackoverflow.com/a/5724131/7259551
