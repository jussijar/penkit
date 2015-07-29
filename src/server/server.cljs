(ns penkit.server
  (:require [cljs.nodejs :as node]))
(enable-console-print!)
(def express (node/require "express"))
(def app (express))
(def staticResources (. express (static "resources")))
(def compiledJs (. express (static "target/js")))
(def http (. (node/require "http") (Server app)))
(def socketio (node/require "socket.io"))
(def io (socketio http))

(defn -main [& args]
	(. io (on "connection" (fn [socket] 
		(. socket (on "NOSTO!" (fn [msg] 
			(.emit io "NOSTOT" (clj->js [{:name "OPH"
				 :totalScore {:kg 1234 :sinclair 435.44 :wilks 546.44}
				 :difference {:kg 345.55 :sinclair 34.55 :wilks 564} }]))
			)))
		(.emit socket "NOSTOT" (clj->js [{:name "OPH"
					 :totalScore {:kg 1234 :sinclair 435.44 :wilks 546.44}
					 :difference {:kg 345.55 :sinclair 34.55 :wilks 564} } 
					{:name "LIHA"
					 :totalScore {:kg 1234.44 :sinclair 435 :wilks 546.44}
					 :difference {:kg 345.55 :sinclair 34.55 :wilks 564} }
					{:name "VISIO"
					 :totalScore {:kg 1234.44 :sinclair 435.44 :wilks 546}
					 :difference {:kg 345.55 :sinclair 34.55 :wilks 564} }]))
		(print "It's on!"))))
	(. app (use staticResources))
	(. app (use compiledJs))
	(. http (listen 3000 (fn [] (print "Upsy-daisy"))))
	)

(set! *main-cli-fn* -main)
