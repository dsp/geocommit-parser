; geocommit tag parser
; (c) 2013 David Soria Parra <dsp+geocommit@experimentalworks.net>
;          Nils Adermann <naderman+geocommit@naderman.de>
; Licensed under the terms of the MIT License
(ns #^{:doc "gecommit tag api",
       :author "David Soria Parra"}
  geocommit.parser
  (:use clojure.walk)
  (:require [clojure.string :as s]
            [clojure.tools.trace :as t])
  (:import (java.text SimpleDateFormat)
           (java.util Date TimeZone)))

(defrecord Commit [_id repository revision message author latitude longitude
                   horizontal-accuracy vertical-accuracy source altitude direction type])

(defn- parse-geocommit-exp
  ([s k]
     (k {:long  (parse-geocommit-exp s #"(?s)geocommit \(1\.0\)\n(.*?)(?:\n\n|$)" #"\n" #":\s+")
         :short (parse-geocommit-exp s #"geocommit\(1\.0\):\s(.*?);" #",\s+" #"\s+")}))
  ([s vers pairsep valsep]
   (if-let [st (re-find vers s)]
           (apply hash-map
                  (mapcat #(s/split % valsep)
                          (s/split (last st) pairsep))))))

(defn- tonumber [^String s]
  (if s
    (Double. s)))

(defn parse-geocommit
  "Parses a geocommit information and returns a geocommit structure
   including ident, author, hash and message."
  [ident hash author message geocommit]
  (let [{:keys [lat long hacc vacc src dir alt speed]}
        (keywordize-keys
          (merge {"hacc" nil "vacc" nil "src" nil "dir" nil "speed" nil "alt" nil}
                 (or (parse-geocommit-exp geocommit :short)
                     (parse-geocommit-exp geocommit :long))))]
    (if (not (or (nil? long) (nil? lat)))
      (Commit. (str "geocommit:" ident ":" hash)
               ident hash message
               author (tonumber lat) (tonumber long)
               (tonumber hacc) (tonumber vacc)
               src (tonumber alt) dir
               "geocommit"))))
