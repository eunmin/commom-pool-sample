(ns commom-pool-sample.core
  (:import [org.apache.commons.pool2 BasePooledObjectFactory KeyedPooledObjectFactory]
           [org.apache.commons.pool2.impl DefaultPooledObject GenericKeyedObjectPool GenericObjectPool]))

(defn make-pool [make-f]
  (GenericObjectPool.
    (proxy [BasePooledObjectFactory] []
      (makeObject []
        (DefaultPooledObject. (make-f))))))

(defn make-keyed-pool [make-f]
  (GenericKeyedObjectPool.
    (proxy [KeyedPooledObjectFactory] []
      (makeObject [k]
        (DefaultPooledObject. (make-f)))
      
      (activateObject [k obj]
        (println "active" k obj))

      (validateObject [k obj]
        (println "validate" k obj))

      (passivateObject [k obj]
        (println "passive" k obj))

      (destroyObject [k obj]
        (println "destory" k obj)))))

(defn use-object [pool]
  (let [obj (.borrowObject pool "key")]
    (println obj)
    (.returnObject pool "key" obj)))

(defn pool-test []
  (let [pool (make-keyed-pool #(Integer. (rand-int 1000000)))]
    (dotimes [n 20]
      (.start (Thread. #(use-object pool))))))

(defn -main [& args]
  (println "Call function commom-pool-sample.core/pool-test in REPL"))
