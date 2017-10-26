# Project Lanterminal
This is a text-based GUI Java project using the [Lanterna](https://github.com/mabe02/lanterna) library in the context of a temperature monitoring system.
I really enjoy running old-school text-based applications like this under a [CRT (Cool Retro Term)](https://github.com/Swordfish90/cool-retro-term) terminal instance!

## Project structure analysis

I'm using Apache Maven to manage this Java-based project and bring it to harmony with Lanterna 3. 

Now let’s take a quick look at our project’s packages:

* tms: This is where the class containing our main method lives.
* gui: Holds the class responsible for casting those powerful Lanterna spells and involves the displaying and assessment of the temperature monitoring system’s data.
* physics: Contains the code for simulating and controlling ambient temperature changes to be picked up by the system’s heat sensors.
* Inside the other packages, we have the core functionality of our temperature monitoring application. The idea here is that we have a heat sensor monitoring system with all sensors placed at different locations.

### Contribute to this project
It's always great to collaborate with other developers! If you have any ideas making this project better, join and start collaborating!