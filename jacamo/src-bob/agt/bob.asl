// JaCaMo-REST: Integration Demo (Node-RED + MQTT)

/* Initial belief */
topic("mqtt/jacamo/ana"). // ana topic

/* Initial goal */
!start.

/* Plans */
+!start
  <- .print("bob is running");
     focusWhenAvailable("mqtt").

+!send_msg : topic(Topic) // send msg to mqtt broker
  <- .concat("Hi from bob", Msg);
     Publish =.. [publish,[Topic,Msg],[]];
     .print("Sending a message to the MQTT broker (ana topic) via dummy artifact");
     act(Publish, Res);
  .

+message(M)[source(S)] // receive msg from mqtt broker
  <- .print("New message: ", M);
     .print("Source: ", S);
  .

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
// { include("$jacamoJar/templates/org-obedient.asl") }
