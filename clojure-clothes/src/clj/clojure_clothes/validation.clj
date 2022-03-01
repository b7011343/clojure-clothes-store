(ns clojure-clothes.validation
  (:require
   [struct.core :as stc]))

(def order-schema
  {:email [stc/required stc/email]
   :fullname [stc/required stc/string]
   :address1 [stc/required stc/string]
   :address2 [stc/string]
   :postcode [stc/required stc/string]
   :design [stc/required stc/map]
   :sku [stc/required stc/map]})

(defn validate-order [params]
  (first (stc/validate params order-schema)))

(defn validate-order-stock [params]
  (let [products ()]))
