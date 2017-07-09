package no.example;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

import static io.vavr.control.Try.failure;
import static io.vavr.control.Try.success;
import static java.lang.String.format;

@Slf4j
public class ArbeidslisteService {
    public Try<Bruker> trySaveBruker(Bruker bruker) {
        log.info("Saving user {}", bruker.getFnr());
        if ("23078547553".equals(bruker.getFnr())) {
            return success(bruker);
        }
        return failure(new RuntimeException(format("Kunne ikke lagre bruker %s", bruker.getFnr())));
    }

    public Try<Bruker> tryFetchBruker(Fnr fnr) {
        log.info("Fetching user {}", fnr);
        if ("23078547553".equals(fnr.getFnr())) {
            return success(Bruker.of(fnr, new VeilederId("XOXO"), "Dette er en kommentar...", ZonedDateTime.now()));
        }
        return failure(new RuntimeException(format("Could not fetch user %s", fnr)));
    }
}


