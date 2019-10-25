(ns bhatkhande.parts
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [re-frame.core :as re-frame :refer [subscribe]]
            [quil.core :as q]
            [bhatkhande.subs :as subs]))

(defn- incr-ith
  [k]
  (let [i (pop k)
        i1 (inc (peek k))]
    (if (empty? i)
      [i1]
      (conj i i1))) )

(def avg-swara-width 0.65)

(defn disp-octave
  "add a dot above or below a swara"
  [dispinfo inp]
  (if (or
       (= :taar inp)
       (= :mandra inp))
    (let [disptext "·"
          {:keys [x y font-size text-align spacing octave] :as di} dispinfo
          y1 (if (= :taar inp) (- y octave) (+ y octave))]
      (q/text disptext x y1)
      dispinfo)
    dispinfo))

(defn disp-kan
  "if a Kan swara is present, display it before the primary swara. Height indicated by
  `kan-raise` , `reduce-font-size` params"
  [dispinfo inp]
  (let [[oct note] inp
        {:keys [x y font-size spacing text-align kan octave] :as di} dispinfo
        {:keys [kan-raise reduce-font-size reduce-spacing reduce-octave-size]} kan
        y1 (- y kan-raise)
        _ (q/text-size (- font-size reduce-font-size))
        _ (q/text-align text-align)
        _ (disp-octave (assoc dispinfo :y y1 :octave (- octave reduce-octave-size)) oct)
        swaramap @(subscribe [::subs/swaramap])
        disptext (swaramap note)
        dip (update-in dispinfo [:x] (fn[i] (+ i (/ spacing reduce-spacing) (q/text-width disptext))))
        ]
    (q/text disptext x y1)
    dip))

(defn disp-sam-khaali
  "add a sam, khaali or taali for the indicated taal, below the main swara "
  [dispinfo inp]
  (let [{:keys [x y sam-khaali font-size]} dispinfo
        {:keys [beat]} inp]
    (when beat
      (do
        (q/text-size (- font-size 5))
        (q/text beat x (+ y sam-khaali))))
    dispinfo))

(defn bhaag-dispinfo
  [dispinfo inp]
  (let [{:keys [x y x-start x-end y-inc
                sam-khaali]} dispinfo]
    (if (>= x (* 0.9 x-end))
      (let [
            iseq (->> (get-in dispinfo [:part-coordinates])
                      reverse)
            ;;split it into 2 cos we need to join it back
            ;;the first one that has bhaag should split it
            [i1 i2] (split-with #(nil? (:bhaag %)) iseq)
            i3 (reverse i1)
            start-from (-> i3 first :x)
            k (mapv #(- (:x %) start-from) i3)
            ;;add the x-offsets and increment y to next line
            i4 (mapv #(assoc %1 :x (+ x-start %2) :y (+ y y-inc))
                     i3 k)
            i5 (into (vec (reverse i2)) i4)
            dinfo (assoc-in dispinfo [:part-coordinates] i5)
            ]
        {:dispinfo dinfo :coords [x-start (+ y y-inc)]})
      {:dispinfo dispinfo :coords [x y]})))

(defn disp-bhaag
  "display a bhaag, indicated by a vertical bar"
  [dispinfo inp]
  (let [{:keys [x y x-start x-end sam-khaali font-size spacing y-inc]} dispinfo
        {:keys [bhaag beat]} inp ]
    (if bhaag
      (let [{:keys [coords] :as d2} (bhaag-dispinfo dispinfo inp)
            [ix y1] coords]
        (do
          (q/stroke-weight 1)
          (q/line [ix (- y1 sam-khaali)] [ix (+ y1 sam-khaali)])
          (-> (:dispinfo d2)
              (assoc :x (+ ix spacing (q/text-width bhaag)) :y y1)
              (update-in [:part-coordinates]
                         #(conj % {:bhaag :bhaag})
                         ))))
      dispinfo)))

(defn comp-bhaag
  "comp dim for a bhaag, indicated by a vertical bar"
  [dispinfo inp]
  (let [{:keys [x y x-start x-end sam-khaali font-size spacing y-inc]} dispinfo
        {:keys [bhaag beat]} inp ]
    (if bhaag
      (let [{:keys [coords] :as d2} (bhaag-dispinfo dispinfo inp)
            [ix y1] coords]
        ;;bhaag width is usually 0.25 of font size 
        (assoc (:dispinfo d2) :x (+ ix spacing (* 0.25 font-size)) :y y1))
      dispinfo)))

(defn disp-meend
  "if a `meend` annotation exists, display a upper curly bracket."
  [dispinfo swaratext]
  (let [over-text "︵"
        {:keys [x y over font-size spacing]} dispinfo
        sw (* avg-swara-width font-size)
        ;;the approximate width is the sum of current note, spacing
        ;;the average width of a note
        approx-width ( + (q/text-width swaratext) sw spacing)
        #_(println " disp-meend " x "  2 swara width " approx-width
                 " swarawidth "
                 (q/text-width swaratext)
                                        ;" text width "
                                        ;(q/text-width over-text)
                ; " start at " k1 " - " (+ x k1)
                 )
        ;;increase the font size by a bit to make the meend wider
        _ (q/text-size (* 1.5 font-size))
        ;;push the x start by half of the difference between the swara and the meend over text
        k1 (/ (-  approx-width (q/text-width over-text)) 2)
        ]
    
    (do
      ;(println " over-width "(q/text-width over-text) " k1 " k1)
      (q/text over-text (+ x k1) (- y over))
      )

    ;;reset the font size back
    (q/text-size font-size)
    dispinfo))

(defn swara-dispinfo
  "utility fn for swara dispinfo."
  [dispinfo inp]
  (let [{:keys [:note :kan :khatka :meend-start :meend-end bhaag]} inp
        swaramap @(subscribe [::subs/swaramap])
        disptext (swaramap (note 1))
        dispinfo (-> dispinfo
                     (update-in [:part-coordinates] #(conj % {:x (:x dispinfo)
                                                              :y (:y dispinfo)
                                                              :text disptext
                                                              :ith (:ith dispinfo)})))]
    dispinfo))

(defn disp-swara
  "display a single swara, which may include kan swaras, meendss."
  [dispinfo inp]
  (let [{:keys [:note :kan :khatka :meend-start :meend-end bhaag]} inp
        swaramap @(subscribe [::subs/swaramap])
        disptext (swaramap (note 1))
        dispinfo (swara-dispinfo dispinfo inp) 
        dispinfo (if kan (disp-kan dispinfo kan) dispinfo)
        _ (if meend-start (disp-meend dispinfo disptext))
        ;;save the starting x y to save the part-coordinates

        {:keys [x y font-size spacing text-align ith] :as di} dispinfo
        _ (disp-sam-khaali dispinfo inp)
        _ (q/text-size font-size)
        _ (q/text-align text-align)
        _ (disp-octave dispinfo (note 0))
        dip (-> dispinfo
                (update-in [:x] (fn[i] (+ i spacing (q/text-width disptext))))
                (update-in [:ith] incr-ith))]
    (if (-> dispinfo :debug :disp-swara)
      (println " disp-swara text " disptext " x " x " y " y
               " spacing "spacing " width " (q/text-width disptext)))
    (q/text disptext x y)
    dip))

(defn comp-swara
  "display a single swara, which may include kan swaras, meendss."
  [dispinfo inp]
  (let [dispinfo (swara-dispinfo dispinfo inp) 
        {:keys [x x-end y font-size spacing ith] :as di} dispinfo
        ;;the average of the width of a swara is empirically about 0.65 times the font-size
        dip (-> dispinfo
                (update-in [:x] (fn[i] (+ i spacing (* avg-swara-width font-size) )))
                (update-in [:ith] incr-ith))]
    #_(if (> x x-end)
      (println " greater than end " [x x-end ith] " - "(-> (get-in dispinfo [:part-coordinates]) last :text)
               (drop 8(get-in dispinfo [:part-coordinates]))
               )
      (println " all good for " [x x-end ith] " - " (-> (get-in dispinfo [:part-coordinates]) last :text))
      )
    dip))

(defn disp-note
  [dispinfo inp]
  (let [{:keys [bhaag]} inp]
    (if bhaag
      (disp-bhaag dispinfo inp)
      (disp-swara dispinfo inp))))

(defn disp-underbrace
  "draws the underbrace under swaras where cnt > 1.
  The third argument is the width of the characters under which to draw the underbrace.
  If the number of swaras is 2, it shows an curly bracket, else it draws an arc"
  [dispinfo inp char-width]
  (let [under-m "︶"
        {:keys [x y under]} dispinfo
        cur-fill (q/current-fill)
        cnt (count inp)]
    (if (> cnt 2)
      (do
        (q/ellipse-mode :corner)
        (q/no-fill)
        (q/arc (- x 3) y char-width 20 0.4 2.5 )
        (q/fill cur-fill))
      (q/text under-m x (+ y under)))))

(defn s-note-dispinfo
  [dispinfo ]
  (let [ispa (:spacing dispinfo)]
    (-> dispinfo
        (assoc :spacing ispa :x (+ ispa (:x dispinfo)))
        (update-in [:ith] (comp incr-ith pop)))))

(defn note-dispinfo
  [dispinfo]
  (-> dispinfo
      (assoc :spacing 1)
      (update-in [:ith] #(conj % 0))))

(defn disp-s-note
  "Display an s-note, which is the set of notes in a single beat of the taal."
  [dispinfo inp]
  (if (= 1 (count inp))
    (disp-note dispinfo (first inp))
    (let [ispa (:spacing dispinfo)
          {:keys [x y ]} dispinfo
          res (reduce disp-note (note-dispinfo dispinfo) inp)
          ;;display underbrace after drawing swaras, so that the underbrace knows the swara width
          _ (disp-underbrace dispinfo inp (- (:x res) x))
          ]
      ;;remove spacing so that the swaras as close together
      (s-note-dispinfo (assoc res :spacing ispa)))))

(defn comp-note
  [dispinfo inp]
  (let [{:keys [bhaag]} inp]
    (if bhaag
      (comp-bhaag dispinfo inp)
      (comp-swara dispinfo inp))))

(defn comp-s-note
  "computes dim an s-note, which is the set of notes in a single beat of the taal."
  [dispinfo inp]
  (if (= 1 (count inp))
    (comp-note dispinfo (first inp))
    (let [ispa (:spacing dispinfo)
          {:keys [x y ]} dispinfo
          res (reduce comp-note (note-dispinfo dispinfo) inp)]
      ;;remove spacing so that the swaras as close together
      (s-note-dispinfo (assoc res :spacing ispa)))))

(defn add-cursor-at-end
  [{:keys [part-coordinates spacing] :as dispinfo}]
  (let [slist (reverse (sort-by (comp first :ith) part-coordinates))
        {:keys [x y ith ]} (first slist)
        tw (reduce str (mapv :text (take-while (fn[k] (= (first ith) (first (:ith k)))) slist)))]
    (update-in dispinfo [:part-coordinates]
               #(conj % {:x (+ x (q/text-width tw) spacing)
                         :y y :ith [(inc (first ith))]}))))

(defn disp-m-note
  "display the sequence of all notes"
  [dispinfo inp]
  (->> (reduce disp-s-note dispinfo inp)
       add-cursor-at-end))


(defn disp-part-label
  "if the part has a label, and `write-part-label` is true, display the name or label
  associated with the part"
  [dispinfo inp]
  (let [{:keys [part-header-font-size x y header-y-spacing write-part-label]} dispinfo]
    (if write-part-label 
      (do 
        (q/text-size part-header-font-size) 
        (q/text inp x y) 
        (assoc dispinfo :y (+ y header-y-spacing)))
      dispinfo)))

(defn split-into-bhaags
  "given a sequence of notes and bhaag information from the taal, interleave the bhaag
  indicated by `|` with the notes
  TODO: doesn't show bhaag for middle of teentaal
  "
  [inp bhaags]
  (let [redfn (fn[{:keys [pre post] :as m} i]
                (let [[f1 f2] (split-at i post)]
                  {:pre (conj pre (vec f1)) :post f2}))
        res (reduce redfn {:pre [] :post inp}
                    bhaags)
        intr (repeat (count bhaags) [[{:bhaag "|"}]])]
    (reduce into (->> (:pre res) (remove empty?) vec 
                      (interleave intr)))))

(defn append-bhaags
  [{:keys [:m-noteseq :taal :part-label] :as m}]
  (let [{:keys [:num-beats :sam-khaali :bhaags]} taal]
    (update-in m [:m-noteseq]
               #(->> % 
                     (partition-all num-beats )
                     (mapv (fn[i] (split-into-bhaags i bhaags)))
                     (reduce into)))))

(defn add-sam-khali
  "Given taal information, add sam, taali and khaali to the note seq"
  [{:keys [:m-noteseq :taal :part-label] :as m}]
  (let [sam-khaali (taal :sam-khaali)
        nb (taal :num-beats)]
    (assoc m :m-noteseq
           (mapv #(if-let [iv (sam-khaali %2)]
                    (update-in %1 [0] (fn[i](assoc i :beat iv)))
                    %1)
                 m-noteseq
                 (iterate #(if (> nb %) (inc %) 1) 1)))))

(defn line-separator
  "if `write-line-separator` is true, add a line separator between parts"
  [dispinfo]
  (let [{:keys [x y x-end header-y-spacing write-line-separator]} dispinfo
        x2 (/ x-end 2)
        x3 (/ x-end 6)
        y1 (+ y (* 2 header-y-spacing))]
    (if write-line-separator
      (do
        (q/stroke-weight 1)
        (q/line [(- x2 x3 ) y1] 
                [(+ x3 x2) y1])
        (assoc dispinfo :y (+ y1 header-y-spacing)))
      (assoc dispinfo :y y1))))

(defn max-xy
  [pc]
  (let [e (map #(mapv % [:x :y]) pc)
        x (mapv first e)
        y (mapv second e)]
    [(apply max x) (apply max y)]))

(defn disp-part
  "display a single part"
  [dispinfo inp]
  (let [{:keys [x y x-start header-y-spacing]} dispinfo
        {:keys [:m-noteseq :taal :part-label] :as m1}
        (-> inp add-sam-khali append-bhaags)
        d1 
        (-> dispinfo
            (assoc :ith [0])
            (disp-part-label part-label)
            (disp-m-note m-noteseq))
        res 
        (-> d1
            line-separator
            (assoc :x x-start)
            (update-in [:part-coordinates] (comp vec reverse)))]
    res))


(defn compute-part-dim
  "compute dimensions of a part"
  [dispinfo inp]
  (let [{:keys [x y x-start header-y-spacing]} dispinfo
        {:keys [:m-noteseq :taal :part-label] :as m1}
        (-> inp add-sam-khali append-bhaags)
        d1 (reduce comp-s-note (assoc dispinfo :ith [0]) m-noteseq)
        res 
        (-> d1
            (assoc :x x-start)
            (update-in [:part-coordinates] (comp vec reverse)))
        mxy (max-xy (:part-coordinates res))]
    mxy))

(defn disp-comp-label
  "if `write-comp-label` is true, display the composition label"
  [dispinfo inp]
  (let [{:keys [x y x-end comp-label-font-size header-y-spacing write-comp-label]} dispinfo]
    (if write-comp-label
      (do 
        (q/text-size comp-label-font-size) 
        (q/text inp (/ x-end 3) y)
        (-> dispinfo 
            (assoc :y (+ y (* 2 header-y-spacing)))))
      dispinfo)))

(defn disp-comp
  "display the whole composition"
  [dispinfo inp]
  (let [{:keys [:parts :taal :comp-label :comp-id]} inp
        d1 (-> dispinfo
               (disp-comp-label comp-label))
        inp2 (mapv #(if (% :taal) %
                        (assoc % :taal taal))
                   parts)]
    (reduce disp-part d1 inp2)))
