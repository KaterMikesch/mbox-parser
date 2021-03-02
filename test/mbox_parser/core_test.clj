(ns mbox-parser.core-test
  (:require [clojure.test :refer :all]
            [mbox-parser.core :refer :all]
            [mbox-parser.utils :refer :all]
            [clojure.string :as str])
  (:import jakarta.mail.internet.MimeMessage
           jakarta.mail.Session
           java.util.Properties)
  #_(:import [javax.mail Session Transport Message$RecipientType]
             [javax.mail.internet MimeMessage InternetAddress]
             [java.util Properties]))

#_(def mbox1 "var/Folder.mbox/mbox")

(defn parse-message
  [los]
  (->> (clojure.string/join "\n" los)
       (string->stream)
       (MimeMessage. (-> (Properties.) (Session/getInstance)))))

(defn mbox->seq
  [path]
  (with-open [rdr (clojure.java.io/reader path)]
    (-> (parse-reader rdr)
        (doall))))

(defn mbox->messages
  [path]
  (->> (mbox->seq path)
       (map parse-message)))

(defn mbox->messages2
  [path]
  (with-open [rdr (clojure.java.io/reader path)]
    (->> (parse-reader rdr)
         (map parse-message)
         (doall))))


(def mbox (char-array (str/join "\n"
                                ["From telegramm@newsletter.tagesschau.de Mon Mar 31 14:59:29 2000"
                                 "Subject: =?iso-8859-1?Q?Tarifeinigung_im_=F6ffentlichen_Dienst_perfekt?="
                                 ""
                                 ">From "
                                 ">>From "
                                 ""
                                 ""])))

(def msg (first (mbox->messages2 mbox)))

(deftest from-quoting
  (let [content (.getContent msg)]
    (is (= "From \n>From " content))))

#_(deftest test-count-messages
    (testing "count messages"
      (is (= 5 (count (mbox->seq mbox1)))
          (= 5 (count (mbox->messages mbox1))))))


#_(deftest test-message-1
    (testing "message 1"
      (let [m (nth (mbox->messages mbox1) 0)
            f (aget (.getFrom m) 0)]
        (is (= "A formatted message" (.getSubject m))
            (= "diavoletto76@gmail.com" (.getAddress f))))))


#_(deftest test-message-2
    (testing "message 2"
      (let [m (nth (mbox->messages mbox1) 1)
            f (aget (.getFrom m) 0)]
        (is (= "Two attachments" (.getSubject m))
            (= "diavoletto76@gmail.com" (.getAddress f))))))


#_(deftest test-subjects
    (testing "subjects"
      (let [expected '("A formatted message"
                       "Two attachments"
                       "A message with an attachment"
                       "Second message"
                       "First message")
            subjects (->> (mbox->messages mbox1)
                          (map #(.getSubject %)))]
        (is (every? true? (map = expected subjects))))))
