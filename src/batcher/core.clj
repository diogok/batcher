(ns batcher.core
  (:require [clojure.core.async :refer [>! >!! <!! <! go chan close! go-loop]]))

(def put >!!)
(def end close!)

(defn batcher
  [limit timer proc] 
   (let [in  (chan limit)
         on  (atom true)
         out (chan 1)
         buf (atom [])]
     (if (> timer 0)
       (future
         (while @on
           (do
             (Thread/sleep timer)
             (>!! out @buf)
             (swap! buf empty)))))
     (go-loop [items (<! out)]
        (if (not (nil? items))
          (do
            (proc items)
            (recur (<! out)))))
     (go-loop [item (<! in)]
       (if (nil? item)
         (do
           (>!! out @buf)
           (close! out)
           (swap! buf empty)
           (swap! on (fn [_] false)))
         (do
           (swap! buf conj item)
           (if (>= (count @buf) limit) 
            (do
              (>!! out @buf)
              (swap! buf empty)))
          (recur (<! in)))))
     in))

