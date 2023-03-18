package SnakeGame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;

public class AudioPlayer {

    HashMap<String, Media> audio = new HashMap<>();

    public AudioPlayer(String[] mediaURLs, Stage root) {

        // Load each media file into memory, so it is ready for use
        for(int i = 0; i < mediaURLs.length; i++) {

            // Quick and dirty string splitting
            String[] split1 = mediaURLs[i].split("/");
            String mediaName = split1[split1.length-1].split("\\.")[0];

            // Load media and add to hashmap
            Media m = new Media(new File(mediaURLs[i]).toURI().toString());
            audio.put(mediaName, m);
        }

        // Subscribe eat game event
        root.addEventHandler(GameEvent.snake_eat, audioEvent -> {
            MediaPlayer mp = new MediaPlayer(audio.get("eat"));
            mp.play();
        });

        // Subscribe death game event
        root.addEventHandler(GameEvent.snake_die, audioEvent -> {
            MediaPlayer mp = new MediaPlayer(audio.get("die"));
            mp.play();
        });
    }
}
