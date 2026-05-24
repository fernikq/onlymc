# OnlyMC - Minecraft Server Plugin
A massive, feature-rich "Core" plugin built for Spigot servers, designed to deliver an engaging gameplay experience while maintaining high server performance under heavy load.


> 💡 **Project Background & Disclaimer**  
> This is a legacy project developed early in my software development journey. It was built for personal use to power my own production server and as a core part of my Java learning process. It features practical solutions to real-world performance problems and was successfully tested under heavy player load.

### Key Features

* **Guild & Faction System:** Management of player guilds, featuring dynamic cuboid territory claims, health/life mechanics, and alliance systems.
<img width="515" height="160" alt="2026-05-24_10 28 10" src="https://github.com/user-attachments/assets/6d2aa8ea-16a6-453c-8731-07c7ff41e307" />

* **Quest & Task System:** Interactive progression pathways for players, featuring customizable in-game challenges and reward milestones.
<img width="621" height="164" alt="2026-05-24_10 25 15" src="https://github.com/user-attachments/assets/7cf4192d-4b19-426e-b2dd-d003d4db9cdc" />

* **Leaderboards & Player Rankings:** Dynamic ranking systems that track player performance, PvP statistics, and guild points.
<img width="465" height="178" alt="2026-05-24_10 30 23" src="https://github.com/user-attachments/assets/863173e0-bd87-4c4e-a58f-f3c2516b315c" />

* **Lag Reduction Systems (The Abyss):** Custom-built performance protection layers, including a trash-clearing "Abyss" system that safely removes dropped items from the world to eliminate server lag, while allowing players to recover lost gear through a specialized menu.

<img width="416" height="276" alt="2026-05-24_10 25 47" src="https://github.com/user-attachments/assets/f795d920-d621-47a6-ad82-0df3eae428f3" />
 
* **Essential Commands & Utilities:** A robust collection of optimized base commands for player management, teleportation, and essential sandbox interactions.
  
* **Asynchronous Data Layer:** Fully decoupled database operations using MySQL, ensuring that player loading, saving, and ranking updates never block the main game thread.

### Battle-Tested in Production
This project was **deployed and actively tested on a live production server with a real, demanding player base**. It was continuously refined to handle peak player spikes, rapid PvP combat events, and massive entity management without compromising server ticks (TPS).

### Reflection & Learning Journey
* **Educational Value:** codebase taught me how to manage concurrency, handle real-time event-driven architectures, and optimize software for low-latency environments.

### Tech Stack
* Java 8+
* Spigot / Bukkit API
* MySQL (Data Storage, HikariCp - JDBC connection pool)
* Maven (Dependency Management)
