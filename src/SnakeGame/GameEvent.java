package SnakeGame;

import javafx.event.Event;
import javafx.event.EventType;

public class GameEvent extends Event {

    public GameEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public static final EventType<GameEvent> snake_eat = new EventType<>(Event.ANY, "snake_eat");
    public static final EventType<GameEvent> snake_die = new EventType<>(Event.ANY, "snake_die");
}
