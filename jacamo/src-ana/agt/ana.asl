// JaCaMo-REST: Integration Demo (Camel + MQTT)

/* Initial belief */
topic("mqtt/jacamo/bob"). // bob's topic

/* Initial goal */
!start.

/* Plans */
+!start <- .print("ana is running").

+!send_msg : topic(Topic)// send msg to mqtt broker
  <- .concat("Hi from ana", Msg);
     .print("Sending a message to the MQTT broker (bob topic) via dummy agent");
     .send(mqtt, achieve, publish_mqtt(Topic,Msg));
  .

+message(M)[source(S)] // receive msg from mqtt broker
  <- .print("New message: ", M);
     .print("Source: ", S);
  .

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
// { include("$jacamoJar/templates/org-obedient.asl") }
