(defproject kanna "0.1.0-SNAPSHOT"
  :main kanna.core
  :description "Discord bot"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[net.dv8tion/JDA "4.3.0_346"]
                 [org.clojure/clojure "1.10.3"]
                 [com.sedmelluq/lavaplayer "1.3.77"]]
  :repositories [["dv8tion" "https://m2.dv8tion.net/releases"]]
  :repl-options {:init-ns kanna.core}

  ;; compiler options
  :java-source-paths ["src/kanna_ext/java"]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})