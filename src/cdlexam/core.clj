(ns cdlexam.core
  (:use compojure.core
        hiccup.core
        cdlexam.admin
        cdlexam.db
        cdlexam.template
        cdlexam.mail
        clojure.contrib.def)
  (:require [compojure.route :as route]
            [appengine-magic.core :as ae]
            [appengine-magic.services.user :as ae-user]
            [ring.util.response :as ring-resp]
            [ring.middleware.params :as ring-params]
            [ring.middleware.stacktrace :as ring-stack]))

(defvar *questions-per-category* 20 "Number of questions per category")

(defn default
  "Default page"
  [req]
  (with-page
    (html [:p "Click on one of the following sections to start the Commercial Driver License Exam.
Each section will present " *questions-per-category* " random questions.
The questions are based on California's Commercial Driver Handbook."]
          [:div {:class "test-category"} [:a {:href "/dotest?category=knowledge"} "Knowledge Test"]]
          [:div {:class "test-category"} [:a {:href "/dotest?category=passenger"} "Passenger Test"]]
          [:div {:class "test-category"} [:a {:href "/dotest?category=safety"} "Safety Test"]])))

(defn dotest
  "Web hook to do the next test"
  [req]
  (let [category (-> (:params req)
                     (get "category"))
        question (next-rand-question category)
        correct (if-let [n (-> (:params req)
                               (get "correct"))]
                  (Integer. n)
                  0)
        incorrect (if-let [n (-> (:params req)
                                 (get "incorrect"))]
                    (Integer. n)
                    0)
        n (if-let [n (-> (:params req)
                         (get "n"))]
            (Integer. n)
            1) 
        total *questions-per-category*]
    (with-page
      (html
       (if (<= n total)
         (display-question question correct incorrect n total)
         (display-summary question correct incorrect n total))
       ;; use a form to hold current information
       [:form {:action "/dotest", :method "post"}
        [:input {:type "hidden",
                 :name "correct-answer",
                 :value (:answer question)}]
        [:input {:type "hidden",
                 :name "correct",
                 :value correct}]
        [:input {:type "hidden",
                 :name "incorrect",
                 :value incorrect}]
        [:input {:type "hidden",
                 :name "n",
                 :value (inc n)}]
        [:input {:type "hidden",
                 :name "category",
                 :value category}]]))))

;; all routes are defined in here
;; this is the central place where admin priviledges are set
(defroutes main-routes
  (GET "/admin" _ admin)
  (GET "/admin/create-db" _ create-db)
  (GET "/admin/list-db" _ list-db)
  (GET "/admin/delete-db" _ delete-db)
  (GET "/login" _ (ring-resp/redirect (ae-user/login-url)))
  (GET "/logout" _ (ring-resp/redirect (ae-user/logout-url)))
  (ANY "/dotest" _ dotest)
  (POST "/_ah/mail/*" _ receive-mail)
  (GET "/" _ default)
  (route/not-found "Page not found"))

(ae/def-appengine-app cdlexam-app (-> main-routes
                                      ring-stack/wrap-stacktrace
                                      ring-params/wrap-params))
