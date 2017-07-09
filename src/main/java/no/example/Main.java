package no.example;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import lombok.extern.slf4j.Slf4j;
import spark.Response;

import java.util.function.Function;

import static java.lang.String.format;
import static no.example.JsonTransformer.mapper;
import static no.example.Validate.validateAccess;
import static no.example.Validate.validateFnr;
import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

@Slf4j
public class Main {

    public static void main(String[] args) {
        port(8080);
        enableDebugScreen();

        ArbeidslisteService service = new ArbeidslisteService();

        get("/arbeidsliste/:fnr", "application/json", (request, response) -> {
            response.type("application/json");
            String fnr = request.params("fnr");
            String veilederId = request.queryParams("veilederId");

            if (validateAccess(veilederId).isInvalid()) {
                response.status(401);
                return new RestResponse<>(List.of(format("Veileder %s har ikke tilgang", veilederId)), List.empty());
            }

            return validateFnr(fnr)
                    .map(service::tryFetchBruker)
                    .fold(
                            validationErr -> {
                                response.status(400);
                                return createResponse(validationErr);
                            },
                            data(response)
                    );

        }, new JsonTransformer());

        put("/arbeidsliste", "application/json", ((request, response) -> {
            response.type("application/json");
            Arbeidsliste body = mapper().readValue(request.body(), Arbeidsliste.class);

            return
                    body.arbeidsliste
                            .map(bruker ->
                                    validate(bruker)
                                            .map(service::trySaveBruker)
                                            .fold(validationError(response), data(response))
                            )
                            .reduce(RestResponse::merge);

        }), new JsonTransformer());

        get("/ping", (request, response) -> "pong");


    }

    private static Function<Try<Bruker>, RestResponse<Bruker>> data(Response response) {
        return result -> {
            if (result.isFailure()) {
                response.status(404);
                return createResponse(result.getCause().getMessage());
            }
            return RestResponse.of(result.get());
        };
    }

    private static Function<Seq<String>, RestResponse<Bruker>> validationError(Response response) {
        return validationErr -> {
            response.status(400);
            return createResponse(validationErr);
        };
    }

    private static RestResponse<Bruker> createResponse(String error) {
        return new RestResponse<>(List.of(error), List.empty());
    }

    private static RestResponse<Bruker> createResponse(Seq<String> error) {
        return new RestResponse<>(error.toList(), List.empty());
    }

    private static Validation<Seq<String>, Bruker> validate(Bruker bruker) {
        return Validation
                .combine(
                        validateFnr(bruker.getFnr()),
                        validateAccess(bruker.getVeilederId()),
                        Validation.valid(bruker.getKommentar()),
                        Validation.valid(bruker.getFrist())
                )
                .ap(Bruker::of);
    }
}
