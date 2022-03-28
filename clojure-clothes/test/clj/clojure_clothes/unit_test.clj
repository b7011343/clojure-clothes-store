(ns clojure-clothes.unit-test
  (:require
   [clojure.test :refer :all]
   [ring.mock.request :refer :all]
   [clojure-clothes.handler :refer :all]
   [clojure-clothes.middleware.formats :as formats]
   [clojure-clothes.forms :as forms]
   [muuntaja.core :as m]
   [monger.core :as mg]
   [clojure-clothes.predicate :as pred]
   [mount.core :as mount]))

(deftest unit-tests
  (testing "database connection"
    (let [conn (mg/connect)
          db (mg/get-db conn "clojure-clothes")]
      (is pred/not-empty? conn)
      (is pred/not-empty? db)))
  
  (testing "checkout-form function"
    )
  
  (testing "track-order function"
    )
  
  (testing "update-order-status function"
    ))
