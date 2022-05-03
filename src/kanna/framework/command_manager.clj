;;;; Command manager interface for Kanna

(ns kanna.framework.command-manager
  (:gen-class))

;; mutable command map
(def command-map (atom {}))

(defn register-command!
  "Register a command name with a command handler to the command map"
  [cmd-name cmd-handler]
  (swap! command-map assoc cmd-name (resolve cmd-handler)))

(defn get-command!
  "Get and return the command associated with the name"
  [cmd-name]
  (get @command-map cmd-name))

(defn get-commands [] (keys @command-map))