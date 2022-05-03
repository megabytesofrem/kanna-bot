(ns kanna.bot
  (:require
   [clojure.string :as str]
   [kanna.framework.util :refer [build-embed, send-embed, send-channel]]
   [kanna.framework.command-manager :refer [get-command!, register-command!, get-commands]]
   ;; commands
   [kanna.commands.general]
   [kanna.commands.music]))

;;; defn these functions as private since we dont need to use them outside of
;;; this file
(defn- list-commands
  "Show a list of registered commands in Kanna"
  [event channel _ _ _]
  (let
   [commands (str/join "," (sort (get-commands)))]
    (.. channel (sendMessage (format "Commands: `%s`" commands)) queue)))

(defn- show-help-for
  "Show help information for a specific command. Use 'list' to list commands"
  [event channel message _ rest-args]
  (let [command (first rest-args)
        handler (get-command! command)

        embed (build-embed command 0xa19ff3 (format "Documentation: ```%s```"
                                                    (:doc (meta handler))))]
    (.. embed (addField "Params"
                        (->> (:params (meta handler))
                             (map (juxt :name :desc))
                             (map #(str/join ": " %))
                             (str/join "\n")) false))
    (println handler)
    (if (not= handler nil)
      (send-embed channel embed)
      ;; else - the handler is null
      (send-channel channel (format ":eyes: Invalid command `%s`" command)))))

(defn foo
  [event a b c d]
  (.shutdown (.getJDA event)))

(defn setup-commands!
  "Setup commands here. This function modifies the atom 'command-map'"
  []
  ;; TODO: make this a map operation?
  (register-command! "ping" 'kanna.commands.general/ping)
  (register-command! "say" 'kanna.commands.general/say)
  (register-command! "list" 'kanna.bot/list-commands)
  (register-command! "help" 'kanna.bot/show-help-for)

  ;; Music bot
  (register-command! "add" 'kanna.commands.music/add-queue)
  (register-command! "play" 'kanna.commands.music/play)
  (register-command! "np" 'kanna.commands.music/now-playing)

  ;; for dummy repl debugging
  (register-command! "die" 'kanna.bot/foo))