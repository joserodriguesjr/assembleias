import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class VotingSimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .disableWarmUp();

    private FeederBuilder<String> associateFeeder = csv("gatling/data/associates.csv").queue();

    private static String sharedAgendaId;

    private ScenarioBuilder setupScenario = scenario("Setup Agenda and Session")
            .exec(
                    http("Create New Agenda")
                            .post("/v1/agendas")
                            .body(StringBody("{\"name\": \"Pauta de Teste de Carga\"}"))
                            .asJson()
                            .check(status().is(201))
                            .check(jsonPath("$.id").saveAs("agendaId"))
            )
            .exec(session -> {
                sharedAgendaId = session.getString("agendaId");

                LocalDateTime endTime = LocalDateTime.now().plusSeconds(220);
                return session.set("endTime", endTime.toString());
            })
            .exec(
                    http("Open Voting Session")
                            .post(session -> "/v1/agendas/" + sharedAgendaId + "/session")
                            .body(StringBody("{\"endTime\": \"#{endTime}\"}"))
                            .asJson()
                            .check(status().is(201))
            )
            .pause(1);

    private ScenarioBuilder votingScenario = scenario("Mass Voting")
            .feed(associateFeeder)
            .exec(
                    http("Submit Vote")
                            .post(session -> "/v1/agendas/" + sharedAgendaId + "/votes")
                            .body(StringBody(
                                    """
                                            {
                                              "associateId": "#{associateId}",
                                              "choice": "SIM"
                                            }
                                            """
                            ))
                            .asJson()
                            .check(status().is(202))
            );

    {
        setUp(
                setupScenario.injectOpen(atOnceUsers(1))
                        .andThen(
                                votingScenario.injectOpen(
                                        rampUsers(100000).during(Duration.ofSeconds(180))
//                                        rampUsersPerSec(1).to(555).during(Duration.ofSeconds(30)),
//                                        constantUsersPerSec(1000).during(Duration.ofSeconds(200))
                                )
                        )).protocols(httpProtocol);
    }
}
