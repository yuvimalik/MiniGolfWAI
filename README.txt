=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: _______
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. JUnit Testing
  This is featured heavily in the MiniGolfTesting.java to test the encapsulated state that is used in order to train the AI model.
  Multiple tests ensure key functions such as clamp and simulate shot are working accurately.

  2. AI via QLearning
  The use of the encapsulated state to implement a Q-Learning hybridized state which runs in its own environment.
  Thanks to rewards and punishments, the models learns from past trainings what are the most optimal actions to take at various states
  eventually creating a sequence that is the optimal sequence of moves for the computer to take in order to reach the hole.

  3. 2D arrays
  2D arrays are what I used to design the MiniGolf individual holes and store various Water, Sand Hazards and the location of the hole.
  The 2D array is iterated through to make a grid like formation which is also used for the QLearning model to store various states.

  4. Inheritance and Subtyping
  Inheritance and Subtyping is used in the Player1 class, the GameObject Class and the GolfCourse class.
  This design encapsulates shared attributes and behaviors, such as position, velocity, angle, power, and stroke count, promoting code reuse and consistency.
  In the GameObject state, many subclasses such as SandTrap, Hole and Water implement the main functionalities but also implement their own
  such as the movement of the ball back to the reset position after the water is Hit for example.
  Lastly, the subclass HoleDesign within the GolfCourse class is used to stores locations of traps and more easily.

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named 
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

  The main class is GameBoard, where all the GUI is stored along with a lot of the logic for winning and losing the game.
  Game Object is the superclass that stores vital information for the different game objects, such as the Sand, Hole and Water
  to inherit from.
  GolfCourse is what stores the UI that needs to be generated. It has a subclass HoleDesign which
  stores the location of traps for different holes and allows for the use of generic collision handling etc.
  HeadlessEnvTendency is where the testable components are stored. This is the environment that trains the model.
  OfflineTendencyTraining is the model that actually trains the game and uses FILE I/O to store various parts.
  Player1, which is badly named, is the Class that is used to store the various states and functionalities of the Game Players.
  HomeScreen is the homescreen which provides instructions and lets you start a game.

  Various other classes, such as Shot and Action, are used to help combine various variables into a single state.


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

  Building a working AI with limit computing power was quite difficult and ended up requiring a hybrid approach
  between qLearning and other topics.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

  Technically speaking, private state is encapsulated well thanks to the model having to train itself
  in a virtual environment. Here, the movement of the computer is simulated without any interference with
  the GUI and another other states.





========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

https://www.datacamp.com/tutorial/introduction-q-learning-beginner-tutorial
