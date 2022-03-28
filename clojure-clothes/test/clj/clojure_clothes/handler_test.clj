(ns clojure-clothes.handler-test
  (:require
   [clojure.test :refer :all]
   [ring.mock.request :refer :all]
   [clojure-clothes.handler :refer :all]
   [clojure-clothes.middleware.formats :as formats]
   [muuntaja.core :as m]
   [clojure-clothes.predicate :as pred]
   [clojure.tools.logging :as log]
   [clojure-clothes.db.core :as db]
   [mount.core :as mount]))

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'clojure-clothes.config/env
                 #'clojure-clothes.handler/app-routes)
    (f)))

(deftest test-app-routes
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))
  
  (testing "dashboard route"
    (let [response ((app) (request :get "/dashboard"))]
      (is (= 200 (:status response)))))
  
  (testing "shop route"
    (let [response ((app) (request :get "/shop"))]
      (is (= 200 (:status response)))))
  
  (testing "checkout route"
    (let [response ((app) (request :get "/checkout"))]
      (is (= 200 (:status response)))))
  
  (testing "order-tracker route"
    (let [response ((app) (request :get "/order-tracker"))]
      (is (= 200 (:status response)))))
  
  (testing "confirm-order route"
    (let [response ((app) (request :get "/order-tracker"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response))))))

(deftest test-app-api
  (testing "/api/products endpoint"
    (let [response ((app) (request :get "/api/products"))
          products (parse-json (:body response))]
      (is (= 200 (:status response)))
      (is (pred/products? products))))

  (testing "/api/product/:id endpoint"
    (let [valid-product-id (:_id (first (db/get-products)))
          request-url (str "/api/product/" valid-product-id)
          response ((app) (request :get request-url))
          product (parse-json (:body response))]
      (is (= 200 (:status response)))
      (is (pred/product? product))))

  (testing "/api/product/:id endpoint 404"
    (let [response ((app) (request :get "/api/product/invalid-id"))]
      (is (= 404 (:status response)))))

  (testing "/api/orders endpoint"
    (let [response ((app) (request :get "/api/orders"))
          orders (parse-json (:body response))]
      (is (= 200 (:status response)))
      (is (pred/orders? orders))))

  (testing "/api/order/:id endpoint"
    (let [valid-order-id (:_id (first (db/get-orders)))
          request-url (str "/api/order/" valid-order-id)
          response ((app) (request :get request-url))
          order (parse-json (:body response))]
      (is (= 200 (:status response)))
      (is (pred/order? order))))

  (testing "/api/order/:id endpoint 404"
    (let [response ((app) (request :get "/api/order/invalid-id"))]
      (is (= 404 (:status response))))))
