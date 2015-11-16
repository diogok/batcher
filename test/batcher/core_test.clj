(ns batcher.core-test
  (:use midje.sweet)
  (:use batcher.core))

(fact "It works!"
  (let [counter (atom 0)
        total   (atom 0)
        proc (fn [stuff]
               (swap! counter + (count stuff))
               (swap! total + (apply + stuff))) 
        bat  (batcher {:size 5 :time 10 :fn proc})]
    (put bat 1)
    (put bat 2)
    (put bat 3)
    (put bat 4)
    @counter => 0
    @total => 0
    (put bat 5)
    (Thread/sleep 250)
    @counter => 5
    @total => 15
    (put bat 6)
    @counter => 5
    @total => 15
    (Thread/sleep 4700)
    @total => 21
    (Thread/sleep 500)
    (put bat 7)
    (put bat 8)
    (put bat 9)
    (put bat 10)
    @counter => 6
    @total => 21
    (Thread/sleep 5000)
    @counter => 10
    @total => 55
    (Thread/sleep 5000)
    @counter => 10
    @total => 55
    (put bat 1)
    (end bat)
    (Thread/sleep 250)
    @counter => 11
    @total => 56
    ))

