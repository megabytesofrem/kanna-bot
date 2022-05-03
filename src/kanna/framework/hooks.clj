;;;; Hooks for Kanna

(ns kanna.framework.hooks
  (:gen-class)
  (:require [clojure.string :as str]
            [kanna.framework.command-manager :refer [get-command!, register-command!]]))

(defn on-command
  "Handle a command passed to the handler function. The message object is also
   passed, and rest-args represent the rest of the arguments (excluding the command)
   itself."
  [event channel message command rest-args]
  (try
    ((var-get (get-command! command)) event channel message command rest-args)
    ;; catch
    (catch NullPointerException e
      (.. channel (sendMessage (str "Invalid command " command)) queue))
    (catch Exception e
      (.. channel (sendMessage (str "Got an exception " e)) queue))))

(defn on-message
  "Called when we receieve a message event from JDA."
  [event]
  (let [message (.. event getMessage)
        channel (.. message getChannel)
        content (.. message getContentDisplay)

        all-args (str/split content #" ")
        rest-args (rest all-args)
        command (str/replace (first all-args) #"k." "")
        has-prefix? (str/starts-with? content "k.")
        is-bot? (.. message getAuthor isBot)]

    ;; when we have a prefix and are not a bot user, call the event handler
    (when (and has-prefix? (not is-bot?))
      (println command)
      (on-command event channel message command rest-args))))