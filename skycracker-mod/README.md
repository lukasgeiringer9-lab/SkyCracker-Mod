# SkyCracker Mod (Fabric, Minecraft 1.21.1)

Fügt den **SkyCracker** hinzu: ein harmloser Mob, der dir überallhin folgt
und dich alle 10–30 Sekunden anschreit (Chat-Nachricht + Brüll-Sound + Feuerwerk-Partikel).

## Benutzen
1. Mod bauen (siehe unten) und die Datei `build/libs/skycracker-1.0.0.jar` in den `mods`-Ordner legen.
2. Zusätzlich **Fabric Loader (>= 0.16.0)** und **Fabric API** für 1.21.1 installieren. Java 21 wird benötigt.
3. Im Kreativmodus: Tab "Spawn-Eier" -> **SkyCracker-Spawn-Ei**.
   Oder per Befehl: `/summon skycracker:skycracker`

## Bauen
Voraussetzung: JDK 21.

Variante A (am einfachsten): Projekt in IntelliJ IDEA öffnen -> als Gradle-Projekt importieren -> Gradle-Task `build` ausführen.

Variante B (Kommandozeile): Im Projektordner einmal `gradle wrapper --gradle-version 8.8` ausführen
(oder den Wrapper aus der Fabric-Vorlage von https://fabricmc.net/develop/template kopieren), dann:

    ./gradlew build

## Anpassen
- Sprüche: `YELLS` in `SkyCrackerEntity.java`
- Abstand zwischen den Schreien: `yellCooldown = 200 + random.nextInt(400)` (Ticks, 20 Ticks = 1 Sekunde)
- Lauftempo: `GENERIC_MOVEMENT_SPEED` und der Wert in `FollowPlayerGoal`
- Aussehen: `src/main/resources/assets/skycracker/textures/entity/skycracker.png` (64x64, Zombie-Layout)
