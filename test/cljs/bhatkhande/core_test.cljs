(ns bhatkhande.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [bhatkhande.spec :as us]
            [bhatkhande.parts :as p]))

(def iseq10 [[{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:taar :r]}]
            [{::us/note [:madhyam :g]}]
            [{::us/note [:madhyam :g]}]
            [{::us/note [:madhyam :g]}]
            [{::us/note [:madhyam :r]}]])

(def iseq16 [[{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:taar :r]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:madhyam :s]}]
            [{::us/note [:taar :r]}]
            [{::us/note [:madhyam :g]}]
            [{::us/note [:madhyam :g]}]
            [{::us/note [:madhyam :g]}]
            [{::us/note [:madhyam :g]}]
            [{::us/note [:madhyam :r]}]])

(def test1 
  {::us/m-noteseq iseq10
   ::us/taal {::us/num-beats 10 ::us/taal-name :jhaptaal
              ::us/taal-label "झपताल"
              ::us/sam-khaali {1 "x" 3 "2" 8 "4" 6 "o"}
              ::us/bhaags [2 3 2 3]}
   ::us/part-label "partname"
   })

(def test2 
  {::us/m-noteseq iseq16
   ::us/taal {::us/num-beats 16 ::us/taal-name :teentaal
              ::us/taal-label "teentaal"
              ::us/sam-khaali {1 "x" 5 "2" 13 "4" 9 "o"}
              ::us/bhaags [4 4 4 4 ]}
   ::us/part-label "partname"})

(defn get-beat-data
  [inp]
  (->> (p/add-sam-khali inp)
       ::us/m-noteseq
       (mapv #(assoc {} %1 (-> %2 first :beat)) (iterate inc 1))
       (remove (fn[v] (-> v vals first nil?)))
       (apply merge)))

(deftest sam-khaali
  (testing " sam khaali added at the right places "
    (->> test1 get-beat-data
         (= (-> test1 ::us/taal ::us/sam-khaali))
         is)
    (->> test2 get-beat-data
         (= (-> test2 ::us/taal ::us/sam-khaali))
         is)))

(defn count-matras-in-bhaag
  [inp]
  (->> inp
       p/add-sam-khali
       p/append-bhaags
       ::us/m-noteseq
       (mapv first)
       (partition-by :bhaag)
       (remove #(-> % first :bhaag))
       (mapv count)))

(deftest bhaags
  (testing "insert bhaags"
    (is (= (-> test1 ::us/taal ::us/bhaags)
           (count-matras-in-bhaag test1)))
    (is (= (-> test2 ::us/taal ::us/bhaags)
           (count-matras-in-bhaag test2)))))

(deftest incr-ith-test
  (testing "increment the last value"
    (is (= {:a [1]} (update-in {:a [0]} [:a] p/incr-ith)))
    (is (= {:a [0 1]} (update-in {:a [0 0]} [:a] p/incr-ith)))
    (is (= {:a [0 0 1]} (update-in {:a [0 0 0]} [:a] p/incr-ith)))
    )
  )
