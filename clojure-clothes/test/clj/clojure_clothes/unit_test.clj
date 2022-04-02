(ns clojure-clothes.unit-test
  (:require
   [clojure.test :refer :all]
   [ring.mock.request :refer :all]
   [clojure-clothes.handler :refer :all]
   [clojure-clothes.validation :as validate]
   [monger.core :as mg]
   [clojure-clothes.db.core :as db]
   [clojure-clothes.predicate :as pred]))

(def test-sku "BL0-SM0-SU")

(def generate-order-params {:fullname "Test Test"
                            :email "test@test.com"
                            :address1 "1 Test Lane"
                            :address2 "Testville"
                            :postcode "T35 T"
                            :sku {"0" test-sku}
                            :design {"0" ["https://gateway.pinata.cloud/ipfs/QmcLjsUDHVuy8GPUPmj4ZdGa3FWTfGnsKhWZ4dxqUaGGXk/yoda.jpg"]}})

(deftest unit-tests
  (testing "database connection"
    (let [conn (mg/connect)
          db (mg/get-db conn "clojure-clothes")]
      (is pred/not-empty? conn)
      (is pred/not-empty? db)))
  
  (testing "generate-order function"
    (let [product-initial-stock (db/get-stock test-sku)
          order-id (str (get (db/generate-order generate-order-params) :_id))
          product-after-stock (db/get-stock test-sku)
          order-exists (pred/not-nil? (db/get-order order-id))]
      (is true? (= product-after-stock (- product-initial-stock 1)))
      (is true? order-exists)
      (db/delete-order order-id)
      (db/update-product-quantity test-sku product-initial-stock)))
  
  (testing "validate-order-in-stock function"
    (let [order-id (str (get (db/generate-order generate-order-params) :_id))
          fn-params {:sku {0 test-sku}}
          product-initial-stock (db/get-stock test-sku)
          in-stock (validate/validate-order-in-stock fn-params)]
      (is true? in-stock)
      (db/delete-order order-id)
      (db/update-product-quantity test-sku (+ 1 product-initial-stock))))
  
  (testing "validate-order-exists function"
    (let [order-id (str (get (db/generate-order generate-order-params) :_id))
          product-initial-stock (db/get-stock test-sku)
          order-exists (validate/validate-order-exists {:oid order-id})]
      (is true? order-exists)
      (db/delete-order order-id)
      (db/update-product-quantity test-sku (+ 1 product-initial-stock)))))
