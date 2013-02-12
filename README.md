CSPsolver
=========

Several algorithm to solve Constraint Satisfaction Problem

Problem description:
A social network game consists of networks of players linked together on a graph. The nodes of the graph are players and two players share a link if they are friends. In the game:
1. The server assigns each player a role of either Warrior, Sorceress, Archer, or Blacksmith.
2. The server needs to assign roles to players on the graph such that no friends have the same role.
3. The server uniformly randomly chooses N players to hold magical tokens. These players must be assigned the same role.
Algorithms used:
Backtracking, Backjumping, Forward checking, Confilict-directed backjumping(CDBJ)
Heuristic used:
Minimum remaining values(MRV), Least constraining variable(LCV)