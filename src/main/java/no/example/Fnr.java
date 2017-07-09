package no.example;

import lombok.Value;

@Value
class Fnr {
    private final String fnr;

    @Override
    public String toString() {
        return fnr;
    }
}
