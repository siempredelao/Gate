Gate
====

"Gate" is the Android framework used in "NotifiKRtera" final degree project. By using it, you can create several Android apps, which objective is to send data of "something" to "somebody", by configuring a XML file.

There are two use modes: Automatic and Non-Automatic. In "Automatic mode", app is started and data is sended automatically each amount of time. In "Non-Automatic mode", app is started and it shows a wizard to the user; in the final step, all data collected is sended.

"NotifiKRtera" use case uses Non-Automatic mode to function (please, go to NotifiKRtera repo and see the /res/raw/definicionnotifikrtera.xml file). There also is an Automatic mode use case, called gAmbulance (please, go to gAmbulance repo).

Finally, using REST webservices, the data is sended to a specified server.
