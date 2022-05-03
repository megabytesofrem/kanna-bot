(ns kanna.framework.util
  (:import [net.dv8tion.jda.api EmbedBuilder]))

(defn send-channel
  "Send a message to a channel"
  [channel message]
  (.. channel (sendMessage message) queue))

;;; Embed helpers
(defn build-embed
  "Helper function to build an embed"
  [title color text]
  (-> (EmbedBuilder.)
      (.setTitle title nil)
      (.setDescription text)
      (.setColor color)))

(defn finalize-embed
  "Convert the embed into a MessageEmbed object that can be used in a message"
  [embed]
  (.build embed))

(defn send-embed
  [channel embed]
  (.. channel (sendMessage (finalize-embed embed)) queue))