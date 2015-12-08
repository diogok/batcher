(ns batcher.core
  (:require [clojure.core.async :refer [>! >!! <!! <! go chan close! go-loop]]))

(defn batcher
  ""
  [{limit :size 
    timer :time 
    proc  :fn 
    end   :end
    uout  :out
    in    :in}] 
   (let [l   (or limit 1024)
         in  (or in (chan l))
         on  (atom true)
         out (chan 1)
         buf (atom [])]

     (if (and (not (nil? timer)) (> timer 0))
       (future
         (while @on
           (do
             (Thread/sleep timer)
             (>!! out @buf)
             (swap! buf empty)))))

     (go-loop [items (<! out)]
        (if (nil? items) 
          (when (not (nil? end))
            (do
              (when (not (nil? uout)) (close! uout))
              (close! end)))
          (do
            (when (not (nil? proc)) (proc items))
            (when (not (nil? uout)) (>! uout items))
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
           (if (>= (count @buf) l) 
            (do
              (>!! out @buf)
              (swap! buf empty)))
          (recur (<! in)))))

     in))

