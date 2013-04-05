(ns geocommit.parser-test
  (:use clojure.test
        geocommit.parser
        clojure.tools.trace))

(deftest parse-geocommit-test
  (testing "Parse a geocommit"
    (let [commit (parse-geocommit "x" "a5a3a2"
                                  "hans" "commit message"
                                  "geocommit(1.0): long 2.3, lat 3.2;")]
      (is (and (= (:author commit) "hans")
               (= (:longitude commit) 2.3)
               (nil? (:altitude commit)))))))
