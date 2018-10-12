# meshAlert

**Objective**

We aim to build an android application that makes extensive use of mesh networks for communication before, during and after a disaster.

**Why mesh networks**

Most of the **communication channels and networks are disrupted** during and after a disaster. Even otherwise, alerting and communicating information to people who are not connected to the internet is equally important to minimize casualties during a disaster. Mesh networks allow delivering messages over longer distances by using other phones as intermediaries without requiring internet. This service can be used to create a broadcast network that spreads messages out to all nearby users. As a result, **our application will be able to work offline**. We will be using an android SDK for the same.

**An overview of our application**

A database with a couple of regions within a certain diameter in a state is maintained along with the details of relief camps in these regions. Predictions are simulated in these regions (since alert APIs are not free and the focus here is on the communication and network issues). The following features will be incorporated in our application based on the simulated predictions.

1. Providing disaster alerts through mesh networks
2. Providing nearest safe region and relief camps in that region \
	a. Based on the simulated predictions the regions are classified as ‘Safe’ or ‘Not Safe’.\
	b. The nearest region among the ‘Safe’ ones along with the details of the relief camps in that region is notified to the user using mesh networks.
3. Providing options for offline messaging or sending SOS through mesh networks

