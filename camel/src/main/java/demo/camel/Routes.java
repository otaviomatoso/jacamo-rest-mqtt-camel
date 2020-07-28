package demo.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;

@Component
public class Routes extends RouteBuilder {

	@Override
    public void configure() throws Exception {

        from("jetty://http://localhost:8090/demo/dummies")
          .to("direct:createDummyAgent")
          .to("direct:createDummyArt");

        from("direct:createDummyAgent")
          .log("Creating dummy agent...")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://192.168.1.112:8080/agents/mqtt?only_wp=true"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("{\"uri\":\"http://localhost:8090/mqtt\"}"))
          .to("http4://createDummyAgent");

        from("direct:createDummyArt")
          .log("Creating dummy artifact...")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://192.168.1.112:8081/workspaces/main/artifacts/mqtt"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("{\"template\":\"jacamo.rest.util.DummyArt\", \"values\":[]}"))
          .to("http4://createDummyArt")
          .log("Registering URL for the dummy artifact...")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://192.168.1.112:8081/workspaces/main/artifacts/mqtt/operations/register/execute"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("[\"http://localhost:8090/mqtt\"]"))
          .to("http4://registerUrl");

        from("jetty://http://localhost:8090/demo/alice")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://192.168.1.112:8080/agents/alice/inbox"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("{\"sender\":\"camel\", \"receiver\":\"alice\", \"performative\":\"achieve\", \"content\":\"send_msg\", \"msgId\":\"13\"}"))
          .to("http4://askAlice");

        from("jetty://http://localhost:8090/demo/bob")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://192.168.1.112:8081/agents/bob/inbox"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("{\"sender\":\"camel\", \"receiver\":\"bob\", \"performative\":\"achieve\", \"content\":\"send_msg\", \"msgId\":\"13\"}"))
          .to("http4://askBob");

        from("paho:mqtt/jacamo/alice?brokerUrl=tcp://broker.hivemq.com:1883")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://192.168.1.112:8080/agents/alice/inbox"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("{\"sender\":\"mqtt\", \"receiver\":\"alice\", \"performative\":\"signal\", \"content\":\"message(\\\"${body}\\\")\", \"msgId\":\"13\"}"))
          .to("http4://sendMsgAlice");

        from("paho:mqtt/jacamo/bob?brokerUrl=tcp://broker.hivemq.com:1883")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://192.168.1.112:8081/workspaces/main/artifacts/mqtt/operations/doDefineObsProperty/execute"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("[\"message\", \"${body}\"]"))
          .to("http4://obsPropDummy");

        from("jetty://http://localhost:8090/mqtt")
          .log("BODY = ${body}");
          // .setBody(simple("MARIA JOANA"))
          // .to("paho:otavio/test/two?brokerUrl=tcp://broker.hivemq.com:1883");
    }

}

// .process(new Processor() {
//   public void process(Exchange exchange) throws Exception {
// 		System.out.println("VAAAAAIIIIII CARAI");
//   }
// })
// .to("paho:otavio/test?brokerUrl=tcp://broker.hivemq.com:1883");
