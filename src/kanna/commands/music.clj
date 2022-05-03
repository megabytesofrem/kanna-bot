(ns kanna.commands.music
  (:require
   [kanna.framework.util :refer [send-channel, build-embed, finalize-embed]]
   [kanna.framework.lavaplayer :as lavaplayer]))

(defn add-queue
  "Add the given YouTube URL to the queue"
  {:params [{:name "url"
             :desc "youtube url to play"}]}
  [event channel message _command rest-args]
  (let
   [guild (.getGuild message)
    url (first rest-args)]
    (lavaplayer/enqueue-song guild url)))

(defn play
  "Play the queue in the current voice channel"
;;   {:params [{:name "url"
;;              :desc "youtube url to play"}]}

  [event channel message _command rest-args]
  (lavaplayer/init-player-manager)
  (let
   [guild (.getGuild message)
    player (lavaplayer/get-player? event guild)]
    (do
      (lavaplayer/add-guild-to-players guild player)
      (lavaplayer/play-youtube-url channel player (lavaplayer/peek-song guild)))))

(defn pause
  "Pause the currently playing track"
  [_event channel message _command _rest-args]
  (send-channel channel ":white_check_mark: Pausing the playback")
  (let [guild (.getGuild message)
        player (get @lavaplayer/guild-players guild)]
    (.setPaused player (not (.isPaused player)))))

(defn now-playing
  "Display the current song playing"
  [_event channel message _command _rest-args]
  (let [guild (.getGuild message)
        track (.getPlayingTrack (get @lavaplayer/guild-players guild))
        info  (.getInfo track)]
    (send-channel channel (-> (build-embed "Now playing" 0xa19ff3 "Currently playing track")
                              (.addField "Title" (.title info) false)
                              (.addField "Author" (.author info) false)
                              (.addField "Length" (format "%d" (.length info)) false)
                              (.addField "URL" (.uri info) false)
                              (finalize-embed)))))