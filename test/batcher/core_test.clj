(ns batcher.core-test
  (:require [clojure.core.async :refer [>! >!! <!! <! go chan close! go-loop]])
  (:use midje.sweet)
  (:use batcher.core))

(fact "It works!"
  (let [counter (atom 0)
        total   (atom 0)
        proc (fn [stuff]
               (swap! counter + (count stuff))
               (swap! total + (apply + stuff))) 
        wait (chan 1)
        bat  (batcher {:size 5 :time 10 :fn proc :end wait})]
    (>!! bat 1)
    (>!! bat 2)
    (>!! bat 3)
    (>!! bat 4)
    @counter => 0
    @total => 0
    (>!! bat 5)
    (Thread/sleep 250)
    @counter => 5
    @total => 15
    (>!! bat 6)
    @counter => 5
    @total => 15
    (Thread/sleep 4700)
    @total => 21
    (Thread/sleep 500)
    (>!! bat 7)
    (>!! bat 8)
    (>!! bat 9)
    (>!! bat 10)
    @counter => 6
    @total => 21
    (Thread/sleep 5000)
    @counter => 10
    @total => 55
    (Thread/sleep 5000)
    @counter => 10
    @total => 55
    (>!! bat 1)
    (close! bat)
    (Thread/sleep 250)
    @counter => 11
    @total => 56
    (<!! wait) => nil
    ))

