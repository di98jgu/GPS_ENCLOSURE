GPS-BASED VIRTUAL GEOGRAPHIC ENCLOSURE FOR MOBILE UNIT
======================================================


PARTICIPANTS
Jim Gunnarsson


INTRO
If you are a stranger, welcome to this project. Feel free to let
me know what you think about my project.

I who run this project here at GitHub are a student at BTH. The main 
language is swedish but all comments in the code is in English. 

The goal with this assignment is a Bachelor degree. 


BACKGROUND
Smartphones, tablets and more recently cameras is all mobile units. 
A modern mobile unit is part of a computer network. It is designed to 
be connected all the time. A mobile unit have the capability to handle 
large amount of data. Data that might be both sensitive and possess a 
economic value. Management of mobile units is vital to protect the data 
and insure security for the entire network.

A mobil unit is a resource in the network. Each resource is defined 
by a set of parameters. One parameter that make a mobil unit special is 
the lack of physical boundaries to world. Typically, the first step in 
security is access control to resources. So the lack of physical access 
control have to be replaced by other means such as tracking the location 
of the mobile unit.

In this work I have studied the possibility to create a virtual fence
around the mobile unit. The mobile unit is free to move within the area 
enclosed by the virtual fence. The mobile unit alert all concerned 
parties if it cross the fence and thus leave the allowed area. The 
needed actions can then be taken.  

The aim of this work is to find a algorithm for establishing and
maintaining a virtual enclosure around a mobile unit. The area may
have any form and should be scalable up to and including national
borders. The enclosure is managed on the mobile unit. 


STRUCTURE
-  doc: Documentation of this project
-  uml: Uml diagrams, bouml or douml is needed for this files
-  src: Android source code
-  img: Images and graphics used for this project


CODE STYLE
-  Encoding UTF-8, make sure Eclipse use the proper encoding !
-  Documentation, javadoc always. 
-  Indentation: 3 spaces, no tabs.
-  Line length: 80 columns
-  Consistency: Look at what's around you!
-  Braces: Opening braces don't go on their own line.
-  Acronyms are words: Treat acronyms as words in names, yielding 
   XmlHttpRequest, getUrl(), etc.
-  Constants, all CAPS. 
