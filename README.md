# batcher

Clojure library for batching operations on both buffer size limit and time limit, using channels.

It is a naive but working function.

## Hot it works

Each "batcher" is a channel with a buffer size limit, a time limit and a callback function.

If the timer is 0 it will not be used. The size limit is mandatory.

It will pass to callback a vector of all the items it have in buffer, it can be an empty vector or with any number of items up to buffer size.

Once the time meet or limit is reached it will pass the buffer to the callback and empty the buffer.

It will only call the callback again if previous call finished.

It will block if buffer is full and previous callback has not finished, until the previous callback finishes.

Internally it is a buffer channel of a limited size, an output channel with buffer of size one and a scheduled function.

When channel is closed it flushes the buffer and end the timer.

## Usage

Include the dependency:

```clojure
[batcher "0.0.2"]
```

Create a batch channel using the "batcher" functions passing a buffer size, time limit and callback function.


```clojure
(use ['batcher.core])

(defn callback 
 [items] nil)

(def batch (batcher 50 60000 callback))

(put batch {:foo "bar"})
#_"Put is equivalent to >!!"
#_(>!! batch {:foo "baz"})

#_"Several puts later"
(end batcher)
#_(close! batcher)

```

This batcher, that is just a channel, will call the callback function when it get 50 items or when 60 secs passes.

## License

MIT

