(ns cdlexam.mail
  (require [appengine-magic.services.mail :as ae-mail]))

(defn receive-mail
  "Handler to receive mail"
  [req]
  (let [in (ae-mail/parse-message req)
        out (ae-mail/make-message :from "kojung@gmail.com"
                                  :to "kojung@gmail.com"
                                  :subject (str "[cdlexam] " (:subject in))
                                  :text-body (:text-body in)
                                  :html-body (:html-body in)
                                  :attachments (:attachments in))]
    (ae-mail/send out)
    {:status 200}))
