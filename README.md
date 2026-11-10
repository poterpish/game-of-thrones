# War of the Five Kings

A Java Console-Based Turn-Based Strategy Game

## 1. Project Overview

War of the Five Kings** is a turn-based strategy game inspired by the world of Game of Thrones. 
The player takes control of one of five noble houses and competes for power and territory in Westeros.
The game combines military strategy, resource management, diplomacy, intelligence, and political intrigue. 
Players must make decisions carefully because their actions can influence future events, relationships between houses, and the outcome of the campaign.
The main goal of this project is to demonstrate the practical application of 5 software design patterns in a complete Java application.

## 2. Playable Houses

Players can choose one of five houses:

- **House Stark** — The North
- **House Lannister** — The Westerlands
- **House Baratheon** — The Stormlands
- **House Greyjoy** — The Iron Islands
- **House Targaryen** — Dragonstone

Each house has its own characteristics, strategic advantages, and story events.

## 3. Main Features

- **Five playable houses**, each with its own abilities and story events.
- **Turn-based campaign** with 12 territories, land and sea routes, and four AI opponents.
- **Military gameplay:** recruitment, movement, equipment, sieges, and tactical battles.
- **Economy:** gold, food, influence, upkeep, and loans.
- **Politics:** alliances, peace treaties, endorsements, intelligence, feasts, and conspiracies.
- **Multiple endings** based on territorial control, political support, or the final Great Council.
  
## 4. Software Design Patterns

The project was developed around the following 5 design patterns:

| Design Pattern | Application in the Game |
|---|---|
| **Builder** | Constructing and configuring campaign scenarios |
| **Abstract Factory** | Creating related objects associated with different houses |
| **Facade** | Providing a unified interface for game actions |
| **Strategy** | Implementing interchangeable battle tactics |
| **Observer** | Updating game components when world events occur |

These patterns help separate responsibilities, reduce dependencies between components, and make the project easier to extend and maintain.

## 5. Project Structure

The Java source code is organized into packages based on functionality:

```text 
src/
├── main/
│   └── java/
│       └── edu/wotfk/
│           ├── ai/              # Computer-controlled opponents
│           ├── campaign/        # Campaign and turn management
│           ├── combat/          # Battles and armies
│           ├── domain/          # Core game models
│           ├── economy/         # Resources and economy
│           ├── game/            # Game facade
│           ├── houses/          # House-specific abilities
│           ├── intelligence/    # Intelligence system
│           ├── intrigue/        # Diplomacy and conspiracies
│           ├── story/           # Story events and consequences
│           └── ui/              # Console interface
└── test/
    └── java/
        └── edu/wotfk/           # Automated checks
```
## 6. How to Run the Game

### Requirements

- Java Development Kit (JDK)
- IntelliJ IDEA

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/poterpish/game-of-thrones.git
   ```

2. Open the `game-of-thrones` folder in IntelliJ IDEA.
3. Configure the project JDK.
4. Find the `ConsoleApplication.java` class in:

   ```text
   src/main/java/edu/wotfk/ui/
   ```

5. Run the application using IntelliJ IDEA.
6. Follow the instructions displayed in the console to start playing.

## 7. Testing

The project includes automated checks for important game systems, including:

- Campaign behavior
- Combat and territory capture
- Economic calculations
- Artificial intelligence
- House-specific story events
- Political intrigue and conspiracies

To run the project verification script on Windows, open PowerShell in the project directory and execute:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\check.ps1
```

The test suite helps detect errors and verify that new features do not break existing functionality.

## 8. Team Members

**Alua & Gaukhar**

This project was developed collaboratively as part of the **Software Design Patterns** course.

GitHub Repository: https://github.com/poterpish/game-of-thrones

## 10. Conclusion

*War of the Five Kings* demonstrates how software design patterns can be applied to a turn-based strategy game.

The project combines several interconnected systems, including combat, economy, diplomacy, artificial intelligence, and story events, while maintaining a modular object-oriented architecture.
