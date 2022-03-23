(ns clojure-clothes.util
  (:require
   [clojure.tools.logging :as log]
   [clojure.string :as str]
   [clojure-clothes.const :as c]
   [clojure.spec.alpha :as s]
   [clojure.data.json :as json]))

;; parse-sku specs
(s/def ::_id some?)
(s/def ::SKU (s/and string? #(= 10 (count %))))
(s/def ::name string?)
(s/def ::quantity number?)
(s/def ::color string?)
(s/def ::size string?)
(s/def ::quality string?)
(s/def ::price float?)
(s/def ::product-spec (s/keys :req-un [::_id ::SKU ::name ::quantity]))
(s/def ::parse-sku-return-spec (s/keys :req-un [::SKU ::_id ::name ::quantity ::color ::size ::quality ::price]))

(defn parse-sku
  "Takes a product map containing a :SKU value and returns
   a new map containing additional :color, :size and :quality values"
  [product]
  {:pre [(s/valid? ::product-spec product)]
   :post [(s/valid? ::parse-sku-return-spec %)]}
  (let [sku (get product :SKU)
        name (get product :name)
        id (get product :_id)
        quantity (get product :quantity)
        split-sku (str/split sku #"-")
        json-file (json/read-str (slurp "resources/sku-decoder.json") :key-fn keyword)
        quality (get (get json-file :quality) (keyword (get split-sku 2)))
        price (cond
                (= quality "Supreme") c/SUPREME-PRICE
                (= quality "Standard") c/STANDARD-PRICE)]
    {:SKU sku
     :_id id
     :name name
     :quantity quantity
     :color (get (get json-file :color) (keyword (get split-sku 0)))
     :size (get (get json-file :size) (keyword (get split-sku 1)))
     :quality quality
     :price price}))

;; get-sku-price specs
(s/def ::sku (s/and string? #(= 10 (count %))))

(defn get-sku-price
  "Takes a string SKU and returns its price"
  [sku]
  {:pre [(s/valid? ::sku sku)]
   :post [(s/valid? ::price %)]}
  (let [split-sku (str/split sku #"-")
        json-file (json/read-str (slurp "resources/sku-decoder.json") :key-fn keyword)
        quality (get (get json-file :quality) (keyword (get split-sku 2)))
        price (cond
                (= quality "Supreme") c/SUPREME-PRICE
                (= quality "Standard") c/STANDARD-PRICE)]
    price))

;; parse-order specs
(s/def ::design (s/or :s string? :n nil?))
(s/def ::sku-map-param (s/map-of string? string?))
(s/def ::design-map-vector (s/and vector? (s/coll-of string?)))
(s/def ::design-map-param (s/map-of string? ::design-map-vector))
(s/def ::sku-design-map (s/keys :req-un [::sku ::design]))
(s/def ::parse-order-return (s/coll-of ::sku-design-map))

(defn parse-order
  "Takes 2 maps and combines them into a single
   vector of maps"
  [sku design]
  {:pre [(s/valid? ::sku-map-param sku)
         (s/valid? ::design-map-param design)]
   :post [(s/valid? ::parse-order-return %)]}
  (let [sku-sorted (vec (into (sorted-map) sku))
        design-sorted (vec (into (sorted-map) design))]
    (for [x (range (count (keys sku)))]
      {:sku (last (get sku-sorted x)) :design (get (last (get design-sorted x)) 0)})))

;; sku-filter specs
(s/def ::sku-design-map (s/keys :req-un [::sku ::design]))
(s/def ::sku-param (s/coll-of ::sku-design-map))
(s/def ::sku-filter-return (s/coll-of string?))

(defn sku-filter
  "Takes a vector of maps and returns a vector of maps only
   containing the key :sku"
  [sku]
  {:pre [(s/valid? ::sku-param sku)]
   :post [(s/valid? ::sku-filter-return %)]}
  (map #(get % :sku) sku))

;; calculate-total specs
(s/def ::sku-quantities-param (s/map-of ::sku pos-int?))
(s/def ::calculate-total-return float?)

(defn calculate-total
  "Takes a map where the key is a SKU and the value is the
   number in the order and returns a total price"
  [sku-quantities]
  {:pre [(s/valid? ::sku-quantities-param sku-quantities)]
   :post [(s/valid? ::calculate-total-return %)]}
  (let [mapped-prices (map (fn [[k v]] (* v (get-sku-price k))) sku-quantities)]
    (apply + mapped-prices)))

;; valid-id? specs
(s/def ::id-param string?)
(s/def ::valid-id boolean?)

(defn valid-id?
  "Takes a string and checks whether it is a valid
   MongoDb ObjectId"
  [id]
  {:pre [(s/valid? ::id-param id)]
   :post [(s/valid? ::valid-id %)]}
  (= (count id) 24))

;; References
;; https://stackoverflow.com/a/5724131/7259551
;; https://stackoverflow.com/questions/71573257
