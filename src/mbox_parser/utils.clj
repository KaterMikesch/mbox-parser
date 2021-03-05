(ns mbox-parser.utils)

(defn string->stream
  ([s] (string->stream s "US-ASCII"))
  ([s encoding]
   (-> s
       (.getBytes encoding)
       (java.io.ByteArrayInputStream.))))
