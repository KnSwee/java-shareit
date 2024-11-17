package ru.practicum.shareit.booking.enums;

public enum State {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    FUTURE("FUTURE"),
    WAITING("WAITING"),
    REJECTED("REJECTED");

    private final String label;

    State(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }


}

