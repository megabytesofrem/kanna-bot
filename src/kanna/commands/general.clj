(ns kanna.commands.general
  (:require 
   [clojure.string :as str]
   [kanna.framework.util :refer [send-channel]])
  (:import [net.dv8tion.jda.api.entities Message]))

(defn ping
  "Ping command"
  [event channel message command rest-args]
  (send-channel channel "Pong :ping_pong:"))

(defn say
  "Say something in the chat"
  {:params [{:name "message"
             :desc "message to say in the chat"}]}
  [event channel message command rest-args]
  (send-channel channel (str/join " " rest-args)))