;;;; Clojure wrapper around Lavaplayer to hopefully make it more cleaner to use
;;;;
;;;; This depends on one Java file (KannaAudioSendHandler.java), which handles the buffering code
;;;; externally and is then compiled as part of the bot.

(ns kanna.framework.lavaplayer
  (:require [kanna.framework.util :refer [send-channel]])
  (:import
   [com.sedmelluq.discord.lavaplayer.player DefaultAudioPlayerManager]
   [com.sedmelluq.discord.lavaplayer.player AudioLoadResultHandler]
   [com.sedmelluq.discord.lavaplayer.player.event AudioEventAdapter]
   [com.sedmelluq.discord.lavaplayer.source.youtube YoutubeAudioSourceManager]

   [kanna_ext.java KannaAudioSendHandler]

   ;; Events
   [com.sedmelluq.discord.lavaplayer.player.event TrackStartEvent TrackEndEvent]
   [com.sedmelluq.discord.lavaplayer.track AudioTrackEndReason]))

;;; Shared player manager atom
(def shared-player-manager (atom nil))

;;; Map of guilds to instances of players
(def guild-players (atom {}))

;;; Map of guilds to music queues
(def guild-queues (atom {}))

(defn add-guild-to-players
  [guild player]
  (swap! guild-players assoc guild player))

;;; Queue management code
(defn get-queue
  "Get the current queue for the given guild"
  [guild]
  (get-in @guild-queues [guild :queue]))

(defn add-guild-to-queue
  "Add the guild to a map of guilds->queues. This should only be done once,
   along with init-player-manager."
  [guild]
  (swap! guild-queues assoc guild {:queue clojure.lang.PersistentQueue/EMPTY}))

(defn enqueue-song
  "Add the given song URL to the queue for the given guild"
  [guild url]
  (swap! guild-queues update-in [guild :queue] conj url))

(defn peek-song
  [guild]
  (peek (get-queue guild)))

(defn pop-song
  "Remove a song from the queue, returning that song"
  [guild]
  (let [queue (get-queue guild)
        top (peek queue)
        queue (pop queue)]
    (swap! guild-queues update-in [guild :queue] pop)
    top))

;;; Lavaplayer core code
(defn init-player-manager
  "Initialize the player manager. This should be done when the bot starts up"
  []
  (reset! shared-player-manager (DefaultAudioPlayerManager.))
  (.registerSourceManager @shared-player-manager (YoutubeAudioSourceManager.)))

(declare join-voice-channel)
(defn get-player?
  "Either creates a new player object from joining the current voice channel, or
   returns the existing audio player for the guild"
  [event guild]
  (if (not= (get @guild-players guild) nil)
    ;; we have a player
    (get @guild-players guild)
    ;; else, make a new connection
    (join-voice-channel event)))

(defn play-youtube-url
  "Play a URL from YouTube"
  [channel player url]

  (.loadItem @shared-player-manager url
             (proxy [AudioLoadResultHandler] []
               (trackLoaded [track]
                 (do
                   (send-channel channel (format ":white_check_mark: Now playing: %s" url))
                   (.playTrack player track)))
               (noMatches []
                 (send-channel channel ":sob: Couldn't find that song"))
               (loadFailed [err]
                 (send-channel channel (format ":sob: An error occured: %s"
                                               (.getMessage err)))))))

(defn join-voice-channel
  "Join the connected voice channel"
  [event]
  (let [channel (-> (.getMember event)
                    (.getVoiceState)
                    (.getChannel))
        player (.createPlayer @shared-player-manager)
        guild (.getGuild event)
        manager (.getAudioManager guild)]
    (do
      ;; open an audio connection
      (.setSendingHandler manager (new KannaAudioSendHandler player))
      (.addListener player (proxy [AudioEventAdapter] []
                             (onTrackStart [player track]
                               ())
                             (onTrackEnd [player track why]
                               (try
                                 (play-youtube-url channel player (peek-song guild))
                                 (catch Exception e
                                   (println e))))))
      (.openAudioConnection manager channel)) player))