package no.example;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
class Bruker {
    String fnr;
    String veilederId;
    String kommentar;
    ZonedDateTime frist;

    static Bruker of(Fnr fnr, VeilederId veilederId, String kommentar, ZonedDateTime frist) {
        return new Bruker(
                fnr.getFnr(),
                veilederId.getVeilederId(),
                kommentar,
                frist
        );
    }
}
