package no.example;

import io.vavr.control.Validation;

import static java.lang.String.format;

class Validate {
    static Validation<String, Fnr> validateFnr(String fnr) {
        if (fnr != null && fnr.matches("\\d{11}")) {
            return Validation.valid(new Fnr(fnr));
        }
        return Validation.invalid(format("%s er ikke et gyldig fnr", fnr));
    }

    static Validation<String, VeilederId> validateAccess(String veilederId) {
        if ("FOO".equals(veilederId)) {
            return Validation.valid(new VeilederId(veilederId));
        }
        return Validation.invalid(format("Veileder %s har ikke tilgang", veilederId));
    }
}
