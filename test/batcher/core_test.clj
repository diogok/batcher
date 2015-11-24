(ns batcher.core-test
  (:require [clojure.core.async :refer [>! >!! <!! <! go chan close! go-loop]])
  (:use midje.sweet)
  (:use batcher.core))

(fact "By buffer size only"
  (let [total (atom 0)
        proc  (fn [xs] (swap! total (fn [t] (apply + t xs))))
        bat   (batcher {:size 5 :fn proc})]
    (dotimes [i 4]
      (>!! bat i))
    @total => 0
    (>!! bat 1)
    (Thread/sleep 10)
    @total => 7
    (dotimes [i 10]
      (>!! bat i))
    (Thread/sleep 10)
    @total => 52
    (>!! bat 1)
    (close! bat)
    (Thread/sleep 10)
    @total => 53
    ))

(fact "By time only"
  (let [total (atom 0)
        proc  (fn [xs] (swap! total (fn [t] (apply + t xs))))
        bat   (batcher {:time 3000 :fn proc})]
    (dotimes [i 15]
      (>!! bat i))
    @total => 0
    (Thread/sleep 100)
    @total => 0
    (Thread/sleep 1000)
    @total => 0
    (Thread/sleep 2000)
    @total => 105
    (>!! bat 1)
    (close! bat)
    (Thread/sleep 10)
    @total => 106
    ))

(fact "By time and size"
  (let [total (atom 0)
        proc  (fn [xs] (swap! total (fn [t] (apply + t xs))))
        bat   (batcher {:time 1000 :size 5 :fn proc})]
    (dotimes [i 4]
      (>!! bat i))
    @total => 0
    (>!! bat 1)
    (Thread/sleep 10)
    @total => 7
    (>!! bat 2)
    (Thread/sleep 10)
    @total => 7
    (Thread/sleep 1000)
    @total => 9
    (close! bat)
    @total => 9))

(fact "Using channels"
  (let [total  (atom 0)
        input  (chan 2)
        output (chan 2)
        end    (chan 1)
        done0  (atom false)
        done1  (atom false)
        bat    (batcher {:in input :out output :size 2 :end end})]
    (go-loop [xs (<! output)]
      (if (nil? xs)
        (swap! done0 (fn [_] true))
        (do
          (swap! total (fn [t] (apply + t xs)))
          (recur (<! output)))))
    (go-loop [_ (<! end)]
      (swap! done1 (fn [_] true)))
    (>!! input 1)
    (>!! input 2)
    (Thread/sleep 10)
    @total => 3
    (>!! input 3)
    (close! input)
    (Thread/sleep 30)
    @done0 => true
    @done1 => true
    @total => 6
    ))

