(ns cdlexam.app_servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use cdlexam.core)
  (:use [appengine-magic.servlet :only [make-servlet-service-method]]))


(defn -service [this request response]
  ((make-servlet-service-method cdlexam-app) this request response))
