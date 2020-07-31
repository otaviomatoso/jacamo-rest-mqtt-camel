package demo.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;
import org.json.JSONObject;
import org.json.JSONArray;

@Component
public class Routes extends RouteBuilder {

	@Override
    public void configure() throws Exception {

				/**
				 * URI FOR CREATING DUMMY ENTITIES
				 */
        from("jetty://http://0.0.0.0:8090/demo/dummies")
          .to("direct:createDummyAgent")
          .to("direct:createDummyArtifact");

				/**
				* CREATE DUMMY AGENT
				*/
        from("direct:createDummyAgent")
          .log("Creating dummy agent...")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://ana:8080/agents/mqtt?only_wp=true"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("{\"uri\":\"http://camel:8090/agent/mqtt\"}"))
          .to("http4://createDummyAgent");

				/**
				* CREATE DUMMY ARTIFACT AND REGISTER ITS URL
				*/
        from("direct:createDummyArtifact")
          .log("Creating dummy artifact...")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://bob:8081/workspaces/main/artifacts/mqtt"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("{\"template\":\"jacamo.rest.util.DummyArt\", \"values\":[]}"))
          .to("http4://createDummyArtifact")
          .log("Registering URL for the dummy artifact...")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://bob:8081/workspaces/main/artifacts/mqtt/operations/register/execute"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("[\"http://camel:8090/artifact/mqtt\"]"))
          .to("http4://registerUrl")
					.transform().constant("dummy agent and dummy artifact were created!");

				/**
				* ASK ANA TO SEND A GREETING MESSAGE TO THE MQTT BROKER VIA DUMMY AGENT
				*/
        from("jetty://http://0.0.0.0:8090/demo/ana")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://ana:8080/agents/ana/inbox"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("{\"sender\":\"camel\", \"receiver\":\"ana\", \"performative\":\"achieve\", \"content\":\"send_msg\", \"msgId\":\"13\"}"))
          .to("http4://askAna")
					.transform().constant("OK!");

				/**
				* ASK BOB TO SEND A GREETING MESSAGE TO THE MQTT BROKER VIA DUMMY ARTIFACT
				*/
        from("jetty://http://0.0.0.0:8090/demo/bob")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://bob:8081/agents/bob/inbox"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("{\"sender\":\"camel\", \"receiver\":\"bob\", \"performative\":\"achieve\", \"content\":\"send_msg\", \"msgId\":\"13\"}"))
          .to("http4://askBob")
					.transform().constant("OK!");

				/**
				* SEND AN ACL MESSAGE TO ANA WITH THE CONTENT RECEIVED BY THE BROKER
				* IN THE TOPIC 'mqtt/jacamo/ana'
				*/
        from("paho:mqtt/jacamo/ana?brokerUrl=tcp://broker.hivemq.com:1883")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://ana:8080/agents/ana/inbox"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("{\"sender\":\"mqtt\", \"receiver\":\"ana\", \"performative\":\"signal\", \"content\":\"message(\\\"${body}\\\")\", \"msgId\":\"13\"}"))
          .to("http4://sendMsgAna");

				/**
				* DEFINE AN OBS. PROP. IN THE DUMMY ARTIFACT WITH THE CONTENT
				* RECEIVED BY THE BROKER IN THE TOPIC 'mqtt/jacamo/bob'
				*/
        from("paho:mqtt/jacamo/bob?brokerUrl=tcp://broker.hivemq.com:1883")
          .setHeader("CamelHttpMethod", constant("POST"))
          .setHeader("CamelHttpUri", constant("http4://bob:8081/workspaces/main/artifacts/mqtt/operations/doDefineObsProperty/execute"))
          .setHeader("Content-Type", constant("application/json"))
          .setBody(simple("[\"message\", \"${body}\"]"))
          .to("http4://obsPropDummy");

				/**
				* URI FOR RECEIVING MESSAGES FROM ANA AND SEND THEM TO THE MQTT BROKER
				*/
        from("jetty://http://0.0.0.0:8090/agent/mqtt?httpMethodRestrict=POST")
					.setHeader("CamelPahoOverrideTopic", jsonpath("['predicate']['terms'][0]"))
					.process(new Processor() {
						  public void process(Exchange exchange) throws Exception {
									String body = exchange.getIn().getBody(String.class);
									JSONObject json = new JSONObject(body);
									String msg = json.getJSONObject("predicate").getJSONArray("terms").getString(1);
									exchange.getIn().setBody(msg);
						  }
					})
					.to("paho:topic?brokerUrl=tcp://broker.hivemq.com:1883");

				/**
				* URI FOR RECEIVING MESSAGES FROM BOB AND SEND THEM TO THE MQTT BROKER
				*/
        from("jetty://http://0.0.0.0:8090/artifact/mqtt?httpMethodRestrict=POST")
					.setHeader("CamelPahoOverrideTopic", jsonpath("['terms'][0]"))
					.process(new Processor() {
							public void process(Exchange exchange) throws Exception {
									String body = exchange.getIn().getBody(String.class);
									JSONObject json = new JSONObject(body);
									String msg = json.getJSONArray("terms").getString(1);
									exchange.getIn().setBody(msg);
							}
					})
					.to("paho:topic?brokerUrl=tcp://broker.hivemq.com:1883");
    }
}
