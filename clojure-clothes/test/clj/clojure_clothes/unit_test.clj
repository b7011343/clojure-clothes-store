(ns clojure-clothes.unit-test
  (:require
   [clojure.test :refer :all]
   [ring.mock.request :refer :all]
   [clojure-clothes.handler :refer :all]
   [clojure-clothes.middleware.formats :as formats]
   [clojure-clothes.forms :as forms]
   [muuntaja.core :as m]
   [monger.core :as mg]
   [clojure.tools.logging :as log]
   [monger.collection :as mc]
   [clojure-clothes.predicate :as pred]
   [ring.util.http-predicates :as rpred]
   [mount.core :as mount]))

;; (def checkout-form-params {:design {0 ["https://gateway.pinata.cloud/ipfs/QmcLjsUDHVuy8GPUPmj4ZdGa3FWTfGnsKhWZ4dxqUaGGXk/cat.jpg"]}
;;                            :sku {0 "BL0-SM0-ST"}
;;                            :email "test@test.com"
;;                            :fullname "Test Test"
;;                            :address1 "1 Test Street"
;;                            :address2 "Testville"
;;                            :postcode "T35 T"})

;; ;; (def x {:postcode "S10 2DH", :sku {1 "BL0-SM0-SU", 0 "BL0-SM0-SU"}, :email "j.whitehead143@hotmail.co.uk", :address2 , :design {0 ["https://gateway.pinata.cloud/ipfs/QmcLjsUDHVuy8GPUPmj4ZdGa3FWTfGnsKhWZ4dxqUaGGXk/cat.jpg" ], 1 ["https://gateway.pinata.cloud/ipfs/QmcLjsUDHVuy8GPUPmj4ZdGa3FWTfGnsKhWZ4dxqUaGGXk/doge.jpg" ]}, :address1 "1 Wharncliffe Road", :__anti-forgery-token iLr1JGt9WcGuYaVNMg143J4VLcDM/dRMCyXs1fg0kDitHd45RDYHQtnyAhELHmE77H/Oa0/8kxHg4hSI, :fullname "Jacob Whitehead"})

;; (def test-product {:SKU "BL0-SM0-ST"
;;                    :name "Standard blue S"
;;                    :quantity 2})

;; (def test-order {:email "test@test.com"
;;                  :fullname "Test Test"
;;                  :address1 "1 Test Street"
;;                  :address2 "Testville"
;;                  :postcode "T35 T"
;;                  :price 9.99
;;                  :order [{:sku "BL0-SM0-ST"
;;                           :design "https://gateway.pinata.cloud/ipfs/QmcLjsUDHVuy8GPUPmj4ZdGa3FWTfGnsKhWZ4dxqUaGGXk/cat.jpg"}]})

;; (defn insert-test-product [db]
;;   (mc/insert-and-return db "products" test-product))

;; (defn delete-test-product [db _id]
;;   (mc/remove-by-id db "products" _id))

;; (defn insert-test-order [db]
;;   mc/insert-and-return db "orders" test-order)

;; (defn delete-test-order [db _id]
;;   (mc/remove-by-id db "orders" _id))

(deftest unit-tests
  (testing "database connection"
    (let [conn (mg/connect)
          db (mg/get-db conn "clojure-clothes")]
      (is pred/not-empty? conn)
      (is pred/not-empty? db)))
  
  ;; (testing "checkout-form function"
  ;;   (let [conn (mg/connect)
  ;;         db (mg/get-db conn "clojure-clothes")
  ;;         new-product (insert-test-order db)
  ;;         new-product-id (:_id new-product)]
  ;;   (log/info new-product)
  ;;   (is (rpred/ok? ((app) (-> (request :post "/checkout")
  ;;                             (assoc :headers {})
  ;;                             (assoc :form-params {"__anti-forgery-token" 3})))))
  ;;   (let [test-order (mc/find-one-as-map db "orders" {:order {:sku (:SKU new-product)}})]
  ;;     (delete-test-product db new-product-id)
  ;;     (delete-test-order db (:_id test-order)))))

  (testing "track-order function")

  (testing "update-order-status function"))
