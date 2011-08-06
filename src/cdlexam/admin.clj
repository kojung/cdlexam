(ns cdlexam.admin
  (:use hiccup.core
        cdlexam.template)
  (:require [appengine-magic.core :as ae]
            [appengine-magic.services.user :as ae-user]
            [ring.util.response :as ring-resp]))

(defn admin
  "Admin page"
  [req]
  (with-page
    (html [:ul
           [:li [:a {:href "/admin/create-db"} "Create DB"]]
           [:li [:a {:href "/admin/list-db"} "List questions"]]
           [:li [:a {:href "/admin/delete-db"} "Delete questions"]]
           [:li [:a {:href "/_ah/admin"} "Development console"]]])))
