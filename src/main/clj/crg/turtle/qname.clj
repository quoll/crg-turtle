(ns ^{:doc "Utilities for automatically converting IRIs strings into QNames."
      :author "Paul Gearon"}
    crg.turtle.qname)

(defn in-range?
  [^Character c low high]
  (let [ch (long c)]
    (and (>= ch low) (<= ch high))))

(def anum (long \a))
(def znum (long \z))
(def Anum (long \A))
(def Znum (long \Z))

(defn pn-chars-u?
  "Can't support #x10000-#xEFFFF"
  [^Character c]
  (let [ch (long c)]
    (or (#{\_\.} c)
        (in-range? ch anum znum)
        (in-range? ch Anum Znum)
        (in-range? ch 0x00C0 0x00D6)
        (in-range? ch 0x00D8 0x00F6)
        (in-range? ch 0x00F8 0x02FF)
        (in-range? ch 0x0370 0x037D)
        (in-range? ch 0x037F 0x1FFF)
        (in-range? ch 0x200C 0x200D)
        (in-range? ch 0x2070 0x218F)
        (in-range? ch 0x2C00 0x2FEF)
        (in-range? ch 0x3001 0xD7FF)
        (in-range? ch 0xF900 0xFDCF)
        (in-range? ch 0xFDF0 0xFFFD))))

(defn pn-local-esc?
  [^String s offset]
  (and (= (nth s offset) \\)
       (#{\_\~\.\-\!\$\&\'\(\)\*\+\,\;\=\/\?\#\@\%} (nth s (inc offset)))))

(def hex #{\0\1\2\3\4\5\6\7\8\9\a\b\c\d\e\f\A\B\C\D\E\F})

(defn percent? [^String s offset]
  (and (= (nth s offset) \%)
       (hex (nth s (inc offset)))
       (hex (nth s (+ offset 2)))))

(defn plx? [^String s offset] (or (percent? s offset) (pn-local-esc? s offset)))

(defn split-char?
  "return tuple of [flag d], where flag is if the character splits an IRI
   and d is how far back to step for the next test"
  [^String s offset]
  (let [ch (nth s offset)]
    (if (or (#{\.\:\-\0\1\2\3\4\5\6\7\8\9\u00b7} ch)
            (pn-chars-u? ch)
            (in-range? ch 0x0300 0x036F)
            (in-range? ch 0x203F 0x2040))
      [false 1]
      (if (or (zero? offset) (pn-local-esc? s (dec offset)))
        [false 2]
        (if (or (< offset 2) (percent? s (- offset 2)))
          [false 3]
          [true 0])))))

(defn start-local?
  "Does the indicated character start a local name?"
  [^String s offset]
  (let [ch (nth s offset)]
    (or (#{\:\0\1\2\3\4\5\6\7\8\9} ch)
        (plx? s offset)
        (pn-chars-u? ch))))

(defn hack-split
  "Fallback split. If there was no character to split on,
   check if there is a : character and split on that."
  [^String u]
  (let [i (inc (.indexOf u (int \:)))]
    (if (zero? i)
      [u ""]
      [(subs u 0 i) (subs u i)])))

(defn split-iri
  "Splits an IRI into a prefix and local name pair"
  [^String u]
  (letfn [(split-from-start [i]
            (loop [start (inc i)]
              (if (= start (count u))
                [u ""]
                (if (start-local? u start)
                  [(subs u 0 start) (subs u start)]
                  (recur (inc start))))))]
    (let [endpos (dec (count u))]
      (if (= (nth u endpos) \.)
        [u ""]
        (loop [i endpos]
          (if (< i 0)
            (hack-split u)
            (let [[split back-step] (split-char? u i)]
              (if split
                (split-from-start i)
                (recur (- i back-step))))))))))

