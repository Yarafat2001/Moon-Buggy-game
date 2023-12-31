

import java.util.*;

class MoonBuggyGame {
    Scanner scanner = new Scanner(System.in);

    boolean game_over;
    int distance;
    int energy;
    int grid_size = 10;
    Vehicle[] players = new Vehicle[2];
    List<Enemy> enemies = new ArrayList<>();
    Moon_surface moonSurface;

    int min_distance_to_win = 5;       // Minimum distance to win
    int min_energy_to_continue = 10;   // Minimum energy to continue

    void playGame() {
        System.out.println("Welcome to Moon Buggy ");
        System.out.println("Choose a game mode:");
        System.out.println("1. Single Player");
        System.out.println("2. Player vs Player");
        int game_mode = scanner.nextInt();

        if (game_mode == 1) {
            init_players(1);
        } else if (game_mode == 2) {
            init_players(2);
        } else {
            System.out.println("Invalid game mode selection. Exiting the game.");
            return;
        }

        energy = 50;

        init_enemies();

        moonSurface = new Moon_surface(grid_size);

        while (!game_over) {
            for (Vehicle player : players) {
                if (player != null) {
                    playTurn(player, game_mode);
                    if (game_over) {
                        break;
                    }
                }
            }
        }

        System.out.println("Game Over. Thanks for playing!");

        if (players[0] != null && players[1] != null) {
            // ... (existing code)
            if (players[0].isEliminated() && players[1].isEliminated()) {
                System.out.println("Both players are out of energy. It's a draw!");
            } else if (players[0].isEliminated()) {
                System.out.println(players[1].getName() + " wins!");
            } else if (players[1].isEliminated()) {
                System.out.println(players[0].getName() + " wins!");
            } else {
                System.out.println("Both players have reached the goal!");
            }

        } else if (players[0] != null) {
            System.out.println(players[0].getName() + " wins!");
        } else if (players[1] != null) {
            System.out.println(players[1].getName() + " wins!");
        } else {
            System.out.println("No valid players to determine a winner.");
        }
    }

    void init_players(int num_players) {
        for (int i = 0; i < num_players; i++) {
            System.out.print("Enter name for Player " + (i + 1) + ": ");
            String player_name = scanner.next();
            players[i] = new Vehicle(player_name);
        }
    }

    void playTurn(Vehicle player, int game_mode) {
        clearConsole(); // Clear the console before displaying game state
        display_game_state(player);
        moonSurface.display(player.get_posX(), player.get_posY(), get_enemy_pos());

        if (game_mode == 1) {  // Single Player
            if (Math.random() < 0.5) {  // 50% chance of enemy attack
                enemyAttack(player);
            }
        }

        System.out.print(player.getName() + ", enter 'F' to move forward, 'B' to move backward, 'U' to move up, 'D' to move down, 'E' to make enemies escape, 'Q' to quit: ");
        String input = scanner.next();
        switch (input.toUpperCase()) {
            case "F":
                movePlayer(player, 1, 0);
                break;
            case "B":
                movePlayer(player, -1, 0);
                break;
            case "U":
                movePlayer(player, 0, -1);
                break;
            case "D":
                movePlayer(player, 0, 1);
                break;
            case "E":
                escape_enemies();
                break;
            case "Q":
                game_over = true;
                break;
            default:
                System.out.println("Invalid input.");
        }
        energy--;

        updateGame(player);
        check_collis(player);
    }

    // ... (existing methods)
    void init_enemies() {
        Random random = new Random();
        int num_enemies = random.nextInt(3) + 2;

        for (int i = 0; i < num_enemies; i++) {
            int enemy_posX = random.nextInt(grid_size);
            int enemy_posY = random.nextInt(grid_size);
            enemies.add(new Enemy(enemy_posX, enemy_posY));
        }
    }

    int[] get_enemy_pos() {
        int[] enemy_pos = new int[enemies.size() * 2];
        for (int i = 0; i < enemies.size(); i++) {
            enemy_pos[i * 2] = enemies.get(i).get_posX();
            enemy_pos[i * 2 + 1] = enemies.get(i).get_posY();
        }
        return enemy_pos;
    }

    void movePlayer(Vehicle player, int directionX, int directionY) {
        int new_posX = player.get_posX() + directionX;
        int new_posY = player.get_posY() + directionY;
        if (new_posX >= 0 && new_posX < grid_size && new_posY >= 0 && new_posY < grid_size) {
            player.set_pos(new_posX, new_posY);
            distance++;
        } else {
            System.out.println("Can't move there.");
        }
    }

    void updateGame(Vehicle player) {
        if (distance >= grid_size) {
            if (energy > 0) {
                System.out.println("Congratulations! " + player.getName() + " wins!");
            } else {
                System.out.println(player.getName() + " has reached the goal, but they are out of energy.");
            }
            game_over = true;
        }
        if (energy <= 0) {
            System.out.println(player.getName() + " is out of energy.");
            game_over = true;
        }
    }

    void escape_enemies() {
        for (Enemy enemy : enemies) {
            enemy.escape();
        }
        System.out.println("Enemies have escaped!");
    }

    void enemyAttack(Vehicle player) {
        for (Enemy enemy : enemies) {
            enemy.attack(player);
            try {
                Thread.sleep(1000); // Delay for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clearConsole(); // Clear the console before displaying updated game state
            display_game_state(player);
            moonSurface.display(player.get_posX(), player.get_posY(), get_enemy_pos());

            if (player.isEliminated()) {
                System.out.println(player.getName() + " has been eliminated!");
            }
        }
    }


    void check_collis(Vehicle player) {
        for (Enemy enemy : enemies) {
            if (enemy.get_posX() == player.get_posX() && enemy.get_posY() == player.get_posY()) {
                System.out.println("Collision! " + enemy.getName() + " attacked " + player.getName() + "!");
                energy -= 10;
                moonSurface.mark_collis(player.get_posX(), player.get_posY());
            }
        }
    }

    void display_game_state(Vehicle player) {
        System.out.println("Distance: " + distance);
        System.out.println("Energy: " + energy);
        System.out.println(player.getName() + " position: (" + player.get_posX() + ", " + player.get_posY() + ")");
    }

    void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

}




