(ns kanna.core
  (:gen-class)
  (:require
   [kanna.framework.hooks :as hooks]
   [kanna.bot :as bot])
  ;; JDA imports
  (:import [net.dv8tion.jda.api JDABuilder]
           [net.dv8tion.jda.api.entities Activity]
           [net.dv8tion.jda.api.hooks ListenerAdapter]))

(defn -main
  "Main method for Kanna"
  [& args]
  (let
   [;; load token from system environment variable
    token (System/getenv "DISCORD_BOT")

    ;; event listeners
    listener (proxy
              [ListenerAdapter] []
               (onMessageReceived [event] (hooks/on-message event)))

    ;; create the JDA instance
    _jda (-> (JDABuilder/createDefault token)
             (.setActivity (Activity/playing "Powered by λ Clojure | k.help"))
             (.addEventListeners (into-array Object [listener]))
             (.build))]
    do (bot/setup-commands!)))