# batcher

Clojure library for batching operations on both buffer size limit and time limit, using channels.

It is a naive but working function.

## Usage

Include the dependency:

[![Clojars Project](http://clojars.org/batcher/latest-version.svg)](http://clojars.org/batcher)

Create a batch channel using the "batcher" function passing hashmap of options.

The options are all optional.

```clojure
{
  :size 20
  :time 1000
  :fn (fn [buffer] nil)
  :end (chan)
  :out (chan)
  :in (chan)
}
```

If you pass a function to fn, it will receive a vector of items each time the batch happens. It may be empty.

If you choose to provide your own in and out channels, they will be used and you can consume the batch buffer from the out channel. On end the channels will be closed and return nil. You should not consume the in channel, but you could use them in a tap or mult. 

The end channel is closed and return nil after the internal input channel is closed, in case want to wait on it.

The size and time limit tell when to process the buffer into the out channel and/or function. Time limit is in millis, and size is the number of elements.

The batcher function return the input channel, that can be closed to end the process. On close the batcher also flushed the remaining buffer to the output channel and function.

```clojure
(use ['batcher.core])
(require ['clojure.core.async :refer [>! >!! <!! <! go chan close! go-loop]])

(defn callback 
 [items] nil)

(def batch (batcher {:size 50 :time 60000 :fn callback}))

(>!! batch {:foo "bar"})

#_"Several puts later"
(close! batcher)

```

This batcher, that is just a channel, will call the callback function when it get 50 items or when 60 secs passes.

## License

MIT

