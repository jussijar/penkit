(defproject penkit "0.0.1"
	:url "https://github.com/jussijar/penkit"
	:dependencies [[org.clojure/clojure "1.7.0"]
					[org.clojure/clojurescript "0.0-3308" :classifier "aot"
					:exclusion [org.clojure/data.json]]
					[org.clojure/data.json "0.2.6" :classifier "aot"]
					[im.chit/purnam.core "0.5.2"]
					[im.chit/gyr "0.3.1"]]
	:jvm-opts ^:replace ["-Xmx1g" "-server"]
	:node-dependencies [[source-map-support "0.3.2"]]
	:plugins [[lein-cljsbuild "1.0.6"]
			[lein-ancient "0.6.7"]
			[lein-npm "0.5.0"]]
	:hooks [leiningen.cljsbuild]
	:clean-targets ["target"]
	:target-path "target"
	:cljsbuild {:builds [{:source-paths ["src/server"]
					    :id "server"
                        :compiler {	;:main 'penkit.core
									:output-dir "target"
									:output-to "target/main.js"
									:optimzations :advanced
									:verbose true
									:target :nodejs
									}}
						{:source-paths ["src/client"]
					    :id "client"
                        :compiler {	:main 'penkit.core
									:output-dir "target/js"
									:output-to "target/js/irrelevant/penkit.js"
									:optimzations :advanced
									:pretty-print true}}]})
									
;https://github.com/bodil/clojurescript-all-the-way-down