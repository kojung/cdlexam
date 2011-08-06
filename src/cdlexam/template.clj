(ns cdlexam.template
  (:use hiccup.core hiccup.page-helpers)
  (:require [appengine-magic.services.user :as ae-user]
            [clojure.contrib.seq-utils :as seq-utils]))

(defmacro with-page
  "Macro to wrap user created content"
  [& body]
  `(ring-resp/response
    (html5 
     ;; header stuff
     [:head
      [:meta {:name "description",
              :content "Commercial Driver License Exam"}]
      [:meta {:name "keywords",
              :content "CDL, Commercial driver license, California, Free Online Test, Knowledge, Safety, Passenger, Endorsement"}]
      [:title "Commercial Driver License Exam"]
      (include-css "/css/cdlexam.css")
      (include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js")
      (include-js "https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.11/jquery-ui.min.js")
      (include-js "/js/jquery.corner.js")
      (include-js "/js/cdlexam.js")
      ;; google analytic code
      (str "
<script type=\"text/javascript\">
var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-7405123-3']);
_gaq.push(['_trackPageview']);

(function() {
  var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
  ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
  var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();
</script>
")]
     [:body
      [:div {:id "header"}
       [:a {:href "/"}
        [:img {:src "/images/logo.png" :id "logo"}]
        [:span {:id "title" } "Commercial Driver License Exam"]]]
      ;; actual body goes here
      [:hr]
      [:div {:id "content"} (html ~@body)]
      (str "
<script type=\"text/javascript\"><!--
google_ad_client = \"ca-pub-2494611563375889\";
/* Vertical Banner */
google_ad_slot = \"3343675249\";
google_ad_width = 728;
google_ad_height = 90;
//-->
</script>
<center>
<script type=\"text/javascript\"
src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\">
</script>
</center>
")
      [:hr]
      ;; footer stuff
      [:div {:id "footer"}
       ;; copyright stuff
       [:div {:class "footrow"}
        "Copyright &copy; 2011. "
        [:a {:href "mailto:webmaster@cdlexam.appspotmail.com"}
         "Commercial Driver License Exam"]
        ". All rights reserved."]
       ;; row of links
       (when (ae-user/user-logged-in?)
         (html
          [:div {:class "footrow"}
           [:span {:class "footlink"} [:a {:href "/logout"} "Logout"]]
           "|"
           [:span {:class "footlink"} (ae-user/current-user)]
           (when (ae-user/user-admin?)
             (html "|"
                   [:span {:class "footlink"} [:a {:href "/admin"} "Admin"]]
                   "(" [:span {:class "footlink"} (ae/appengine-environment-type)] ")"))]))]])))

(defn display-question
  "UI showing the question"
  [question correct incorrect n total]
  (html
   [:div {:class "question"}
    [:div {:class "question-category"}
     (condp = (:category question)
         "knowledge" "Knowledge Test"
         "passenger" "Passenger Transport Endorsement"
         "safety" "Vehicle Code and Safety Driving Practices"
         :default (throw (Error. (format "Unsupported category '%s'"
                                         (:category question)))))]
    [:div {:class "question-counter"}
     "Question: " n "/" total
     ", Correct: " [:span {:class "correct-counter"} correct]
     ", Incorrect: " [:span {:class "incorrect-counter"} incorrect]]
    [:div {:class "question-description"} (:description question)]
    (map (fn [[idx val]]
           (html [:div {:class "question-options"}
                  [:input {:type "radio",
                           :name "answer",
                           :value idx,
                           :class "question-radio"} val]]))
         (seq-utils/indexed (:choices question)))
    ;; use a button to interact with the user
    [:a {:href "#"} 
     [:div {:class "question-confirm"} "Confirm answer"]]
    ;; hidden button for next steps
    [:a {:href "#"}
     [:div {:class "next-button"} "Next"]]]))

(defn display-summary
  "UI showing the end of the test"
  [question correct incorrect n total]
  (html
   [:div {:class "question"}
    [:div {:class "question-category"}
     (condp = (:category question)
         "knowledge" "Knowledge Test"
         "passenger" "Passenger Transport Endorsement"
         "safety" "Vehicle Code and Safety Driving Practices"
         :default (throw (Error. (format "Unsupported category '%s'"
                                         (:category question)))))]
    [:div {:class "summary-header"}
     (if (> correct incorrect)
       "Congratulations!"
       "Nice try.")]
    [:ul {:class "summary-detail"}
     [:li "Questions answered: " total]
     [:li "Correct answers: " [:span {:class "correct-counter"} correct]]
     [:li "Incorrect answers: " [:span {:class "incorrect-counter"} incorrect]]]
    [:div {:class "summary-header"} "Score: " (int (* 100 (/ correct total))) "%"]]))   
