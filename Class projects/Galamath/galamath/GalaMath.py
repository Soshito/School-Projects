import pygame
import os
import time
import random
pygame.font.init()
pygame.mixer.init()

WIDTH, HEIGHT = 800, 800
WIN = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("GalaMath")
MATH_PROBLEM_FONT = pygame.font.SysFont("comicsans", 50)
ANSWER_FONT = pygame.font.SysFont("comicsans", 20)
HEALTH_FONT = pygame.font.SysFont("comicsans", 25)
SCORE_FONT = pygame.font.SysFont("comicsans", 25)
ESCAPE_FONT = pygame.font.SysFont("comicsans", 25)
OPTION_FONT = pygame.font.SysFont("comicsans", 40)
HEADER_FONT = pygame.font.SysFont("comicsans", 70)
GAME_OVER_FONT = pygame.font.SysFont("comicsans", 100)
BULLET_HIT_SOUND = pygame.mixer.Sound(os.path.join("Assets", "Grenade+1.mp3"))
BULLET_FIRE_SOUND = pygame.mixer.Sound(os.path.join("Assets", "lazer_shot.mp3"))
GAME_START_SOUND = pygame.mixer.Sound(os.path.join("Assets", "game_start.mp3"))
SELECTION_SOUND = pygame.mixer.Sound(os.path.join("Assets", "selection_sound.mp3"))
FPS = 60
PLAYER_SPEED = 5
ENEMY_SPEED = 1
BULLET_SPEED = 8
MAX_AMMO = 5
ENEMY_FIRE_SPEED = 2
WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
RED = (255, 0, 0)
YELLOW = (255, 255, 0)
MIDLINE_BORDER = pygame.Rect(0, 395, WIDTH, 10)
SHIP_WIDTH = 40
SHIP_HEIGHT = 35
ASTEROID_WIDTH = 80
ASTEROID_HEIGHT = 80
ASTEROID_SPEED = 1
GAME_LOGO = pygame.image.load(os.path.join('Assets', 'GalaMathLogo.png'))
SELECTION_MARKER = pygame.image.load(os.path.join('Assets', 'selection_marker.png'))
PLAYER_SHIP_IMAGE = pygame.image.load(os.path.join('Assets', 'spaceship_yellow.png'))
PLAYER_SHIP = pygame.transform.rotate(pygame.transform.scale(PLAYER_SHIP_IMAGE, (SHIP_WIDTH, SHIP_HEIGHT)), 180)
ENEMY_SHIP_IMAGE = pygame.image.load(os.path.join('Assets', 'spaceship_red.png'))
ENEMY_SHIP = pygame.transform.scale(ENEMY_SHIP_IMAGE, (SHIP_WIDTH, SHIP_HEIGHT))
SPACE = pygame.transform.scale(pygame.image.load(os.path.join('Assets', 'space.png')), (WIDTH, HEIGHT))
ASTEROID_IMAGE = pygame.image.load(os.path.join('Assets', 'Asteroid.png'))
ASTEROID = pygame.transform.scale(ASTEROID_IMAGE, (ASTEROID_WIDTH, ASTEROID_HEIGHT))
PLAYER_HIT = pygame.USEREVENT + 1


MATH_PROBLEMS_ANSWERS = {"18 + 34": 52, "25 + 6": 31, "3 + 8": 11,
                      "24 - 9": 15,"15 - 2": 13, "36 - 11": 25,
                      "3 * 4": 12, "6 * 6": 36, "8 * 7": 56,
                      "15 / 3": 5, "48 / 4": 12, "64 / 8": 8}
MATH_PROBLEMS = {0: "18 + 34", 1: "25 + 6", 2: "3 + 8",
                      3: "24 - 9",4: "15 - 2", 5: "36 - 11",
                      6: "3 * 4", 7: "6 * 6", 8: "8 * 7",
                      9: "15 / 3", 10: "48 / 4", 11: "64 / 8"}


def create_player_bullet(player, player_ammo):
    bullet = pygame.Rect(player.x + player.width // 2, player.y + player.height, 5, 10)
    player_ammo.append(bullet)


def decide_enemy_shooters(enemies, enemy_bullets):
    if len(enemies) > 0:
        shooting_enemy1, shooting_enemy2, shooting_enemy3, shooting_enemy4 = random.randint(0, len(enemies) - 1), random.randint(0, len(enemies) - 1), random.randint(0, len(enemies) - 1), random.randint(0, len(enemies) - 1)
        create_enemy_bullet(enemies[shooting_enemy1], enemy_bullets)
        create_enemy_bullet(enemies[shooting_enemy2], enemy_bullets)
        create_enemy_bullet(enemies[shooting_enemy3], enemy_bullets)
        create_enemy_bullet(enemies[shooting_enemy4], enemy_bullets)


def create_enemy_bullet(enemy, enemy_bullets):
    bullet = pygame.Rect(enemy.x + enemy.width // 2, enemy.y + enemy.height, 5, 10)
    enemy_bullets.append(bullet)
    BULLET_FIRE_SOUND.play()


def handle_enemy_shots(player, enemies, enemy_bullets):
    for bullet in enemy_bullets:
        bullet.y += BULLET_SPEED
        if player.colliderect(bullet):
            enemy_bullets.remove(bullet)
            pygame.event.post(pygame.event.Event(PLAYER_HIT))
            BULLET_HIT_SOUND.play()
        if bullet.y >= HEIGHT:
            enemy_bullets.remove(bullet)


def handle_player_bullets(player_ammo, player, enemies):
    for bullet in player_ammo:
        bullet.y -= BULLET_SPEED
        for enemy in enemies:
            if enemy.colliderect(bullet):
                player_ammo.remove(bullet)
                destroy_enemy(enemy, enemies)
                BULLET_HIT_SOUND.play()
                return True
        if bullet.y < 0:
            player_ammo.remove(bullet)

def handle_player_bullets_asteroids(player_ammo, player, asteroids):
    for bullet in player_ammo:
        for asteroid in asteroids:
            if asteroid.colliderect(bullet):
                player_ammo.remove(bullet)
                asteroids.remove(asteroid)
                BULLET_HIT_SOUND.play()
                return True


def destroy_enemy(enemy, enemies):
    for e in enemies:
        if e == enemy:
            enemies.remove(e)


def draw_game_over(text, score):
    if set_high_score(score):
        draw_new_high_score = OPTION_FONT.render("NEW HIGH SCORE!", 1, WHITE)
        WIN.blit(draw_new_high_score, (WIDTH // 2 - draw_new_high_score.get_width() // 2,
                                       (HEIGHT // 2 - draw_new_high_score.get_height() // 2) + 170))
    draw_game_over_text = GAME_OVER_FONT.render(text, 1, RED)
    draw_game_score_text = HEADER_FONT.render("SCORE: " + str(score), 1, WHITE)
    WIN.blit(draw_game_over_text, (WIDTH // 2 - draw_game_over_text.get_width() // 2,
                                   HEIGHT // 2 - draw_game_over_text.get_height() // 2))
    WIN.blit(draw_game_score_text, (WIDTH // 2 - draw_game_score_text.get_width() // 2,
                                    (HEIGHT // 2 - draw_game_score_text.get_height() // 2) + 100))
    pygame.display.update()
    pygame.time.delay(5000)


def handle_player_movement(keys_pressed, player):
    if keys_pressed[pygame.K_LEFT] and (player.x - PLAYER_SPEED) > 0:  # left
        player.x -= PLAYER_SPEED
    if keys_pressed[pygame.K_RIGHT] and (player.x + PLAYER_SPEED) < WIDTH - SHIP_WIDTH:  # right
        player.x += PLAYER_SPEED
    if keys_pressed[pygame.K_UP] and (player.y - PLAYER_SPEED) > MIDLINE_BORDER.y + 10:# up
        player.y -= PLAYER_SPEED
    if keys_pressed[pygame.K_DOWN] and (player.y + PLAYER_SPEED + SHIP_HEIGHT) < HEIGHT:  # down
        player.y += PLAYER_SPEED


def move_enemies(movement_tracker, enemies):
    if movement_tracker == 0 or movement_tracker % 2 == 0:
        for enemy in enemies:
            enemy.x += ENEMY_SPEED
            #enemy.y += 1
    if movement_tracker != 0 and movement_tracker % 2 != 0:
        for enemy in enemies:
            enemy.x -= ENEMY_SPEED
            #enemy.y -= 1


def handle_enemy_movement(enemies, movement_tracker):
    move_enemies(movement_tracker, enemies)
    for enemy in enemies:
        if enemy.x <= 0:
            return 100
        elif enemy.x + SHIP_WIDTH >= WIDTH:
            return 100
    return 0


def handle_asteroid_movement(asteroids):
    for asteroid in asteroids:
        asteroid.x -= ASTEROID_SPEED
        asteroid.y += ASTEROID_SPEED
        if asteroid.x <= 0 or asteroid.y >= HEIGHT:
            asteroid.x = WIDTH - ASTEROID_WIDTH
            asteroid.y = 0


def game_window(enemies, player, player_ammo, player_health, player_score, enemy_bullets, asteroids, math_problem, math_problem_answer):
    WIN.blit(SPACE, (0, 0))
    pygame.draw.rect(WIN, BLACK, MIDLINE_BORDER)
    player_health_text = HEALTH_FONT.render("HP: " + str(player_health), 1, WHITE)
    player_score_text = SCORE_FONT.render(str(player_score), 1, WHITE)
    escape_text = ESCAPE_FONT.render("ESC FOR BACK", 1, WHITE)
    math_problem_text = MATH_PROBLEM_FONT.render(str(math_problem), 1, WHITE)
    math_problem_answer_text = ANSWER_FONT.render(str(math_problem_answer), 1, WHITE)
    WIN.blit(escape_text, (10, 760))
    WIN.blit(player_health_text, (10, 10))
    WIN.blit(player_score_text, (WIDTH - player_score_text.get_width() - 10, 10))
    WIN.blit(math_problem_text, (WIDTH // 2 - math_problem_text.get_width() // 2, HEIGHT // 2 - math_problem_text.get_height() // 2))
    WIN.blit(PLAYER_SHIP, (player.x, player.y))
    for enemy in enemies:
        WIN.blit(ENEMY_SHIP, (enemy.x, enemy.y))
    for bullet in player_ammo:
        pygame.draw.rect(WIN, YELLOW, bullet)
    for bullet in enemy_bullets:
        pygame.draw.rect(WIN, RED, bullet)
    for asteroid in asteroids:
        WIN.blit(ASTEROID, (asteroid.x, asteroid.y))
        WIN.blit(math_problem_answer_text, (asteroid.x + 7, asteroid.y + 40))
    pygame.display.update()


def game(difficulty):
    track_time = time.time()
    movement_tracker = 0
    asteroids = []
    asteroids.append(pygame.Rect(WIDTH - ASTEROID_WIDTH, 0, ASTEROID_WIDTH, ASTEROID_HEIGHT))
    enemies = []
    enemy_bullets = []
    tempx = 10
    tempy = 50
    for i in range(0, 15):
        enemies.append(pygame.Rect(tempx, tempy, SHIP_WIDTH, SHIP_HEIGHT))
        tempx += 50
        if i % 2 == 0:
            tempy += 80
        tempy -= 40
    player = pygame.Rect(400, 700, SHIP_WIDTH, SHIP_HEIGHT)
    player_ammo = []
    player_health = 3
    if difficulty == "EASY":
        player_health = 3
    if difficulty == "MEDIUM":
        player_health = 2
    if difficulty == "HARD":
        player_health = 1
    player_score = 0
    game_over_text = ''
    clock = pygame.time.Clock()
    math_problem_num = random.randint(0, len(MATH_PROBLEMS))
    run = True
    while run:
        clock.tick(FPS)
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                run = False
                pygame.quit()
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_SPACE and len(player_ammo) < MAX_AMMO:
                    create_player_bullet(player, player_ammo)
                    BULLET_FIRE_SOUND.play()
                if event.key == pygame.K_ESCAPE:
                    menu()
            if event.type == PLAYER_HIT:
                player_health -= 1
        if player_health <= 0:
            game_over_text = "GAME OVER"
        if game_over_text != '':
            draw_game_over(game_over_text, player_score)
            pygame.quit()
        math_problem = MATH_PROBLEMS[math_problem_num]
        math_problem_answer = MATH_PROBLEMS_ANSWERS[math_problem]
        keys_pressed = pygame.key.get_pressed()
        handle_asteroid_movement(asteroids)
        handle_player_movement(keys_pressed, player)
        update_tracker = handle_enemy_movement(enemies, movement_tracker)
        if update_tracker > 0:
            movement_tracker += 1
        if handle_player_bullets(player_ammo, player, enemies):
            player_score += 100
        if handle_player_bullets_asteroids(player_ammo, player, asteroids):
            player_score += 1000
            asteroids.append(pygame.Rect(WIDTH - ASTEROID_WIDTH, 0, ASTEROID_WIDTH, ASTEROID_HEIGHT))
            math_problem_num = random.randint(0, len(MATH_PROBLEMS))
            math_problem = MATH_PROBLEMS[math_problem_num]
            math_problem_answer = MATH_PROBLEMS_ANSWERS[math_problem]
        if time.time() - track_time > ENEMY_FIRE_SPEED:
            decide_enemy_shooters(enemies, enemy_bullets)
            track_time = time.time()
        handle_enemy_shots(player, enemies, enemy_bullets)

        game_window(enemies, player, player_ammo, player_health, player_score, enemy_bullets, asteroids, math_problem, math_problem_answer)
    game(difficulty)


def menu_play():
    run = True
    selection_pos_array = [(70, 410), (240, 410), (490, 410)]
    selection_index = 0
    while run:
        WIN.blit(SPACE, (0, 0))
        WIN.blit(GAME_LOGO, (200, 100))
        header_play_text = HEADER_FONT.render("PLAY", 1, WHITE)
        WIN.blit(header_play_text, (300, 300))
        option_easy_text = OPTION_FONT.render("EASY", 1, WHITE)
        option_medium_text = OPTION_FONT.render("MEDIUM", 1, WHITE)
        option_hard_text = OPTION_FONT.render("HARD", 1, WHITE)
        escape_text = ESCAPE_FONT.render("ESC FOR BACK", 1, WHITE)
        WIN.blit(escape_text, (10, 10))
        WIN.blit(option_easy_text, (120, 400))
        WIN.blit(option_medium_text, (290, 400))
        WIN.blit(option_hard_text, (540, 400))
        WIN.blit(SELECTION_MARKER, selection_pos_array[selection_index])
        for event in pygame.event.get():
            if event.type == pygame.KEYDOWN:
                SELECTION_SOUND.play()
                if event.key == pygame.K_RIGHT:
                    if selection_index < 2:
                        selection_index += 1
                if event.key == pygame.K_LEFT:
                    if selection_index > 0:
                        selection_index -= 1
                if event.key == pygame.K_ESCAPE:
                    menu()
                if event.key == pygame.K_SPACE:
                    GAME_START_SOUND.play()
                    if selection_index == 0:
                        game('EASY')
                        run = False
                    if selection_index == 1:
                        game('MEDIUM')
                        run = False
                    if selection_index == 2:
                        game('HARD')
                        run = False
        pygame.display.update()


def menu_settings():
    volume_value = 30
    run = True
    while run:
        if volume_value > 100:
            volume_value -= 10
        if volume_value < 0:
            volume_value += 10
        BULLET_FIRE_SOUND.set_volume(volume_value / 100)
        BULLET_HIT_SOUND.set_volume(volume_value / 100)
        GAME_START_SOUND.set_volume((volume_value / 100) + 0.2)
        SELECTION_SOUND.set_volume((volume_value / 100) + 0.2)
        WIN.blit(SPACE, (0, 0))
        WIN.blit(GAME_LOGO, (200, 100))
        header_settings_text = HEADER_FONT.render("SETTINGS", 1, WHITE)
        WIN.blit(header_settings_text, (200, 300))
        option_volume_text = OPTION_FONT.render("VOLUME", 1, WHITE)
        WIN.blit(option_volume_text, (200, 500))
        value_volume_text = OPTION_FONT.render(str(volume_value), 1, WHITE)
        WIN.blit(value_volume_text, (540, 500))
        escape_text = ESCAPE_FONT.render("ESC FOR BACK", 1, WHITE)
        WIN.blit(escape_text, (10,10))
        for event in pygame.event.get():
            if event.type == pygame.KEYDOWN:
                SELECTION_SOUND.play()
                if event.key == pygame.K_RIGHT:
                    volume_value += 10
                if event.key == pygame.K_LEFT:
                    volume_value -= 10
                if event.key == pygame.K_ESCAPE:
                    menu()
        pygame.display.update()


def menu_window(selection_position):
    WIN.blit(SPACE, (0, 0))
    WIN.blit(GAME_LOGO, (200, 100))
    option_play_text = OPTION_FONT.render("PLAY", 1, WHITE)
    option_settings_text = OPTION_FONT.render("SETTINGS", 1, WHITE)
    option_quit_text = OPTION_FONT.render("QUIT", 1, WHITE)
    high_score_text = SCORE_FONT.render("HIGH SCORE: " + str(get_high_score()), 1, WHITE)
    WIN.blit(option_play_text, (170, 400))
    WIN.blit(option_settings_text, (400, 400))
    WIN.blit(option_quit_text, (300, 600))
    WIN.blit(high_score_text, (10, 10))
    WIN.blit(SELECTION_MARKER, selection_position)


def get_high_score():
    file = open("high_score.txt", 'r')
    content = file.read(-1)
    file.close()
    high_score = int(content)
    return high_score


def set_high_score(new_hs):
    is_updated = False
    cur_hs = get_high_score()
    if new_hs > cur_hs:
        is_updated = True
        file = open("high_score.txt", 'w')
        file.write(str(new_hs))
        file.close()
    return is_updated


def menu():
    run = True
    selection_position = (120, 410)
    while run:
        menu_window(selection_position)
        for event in pygame.event.get():
            if event.type == pygame.KEYDOWN:
                SELECTION_SOUND.play()
                if event.key == pygame.K_DOWN:
                    selection_position = (250, 610)
                if event.key == pygame.K_RIGHT:
                    selection_position = (350, 410)
                if event.key == pygame.K_LEFT:
                    selection_position = (120, 410)
                if event.key == pygame.K_UP:
                    selection_position = (120, 410)
                if event.key == pygame.K_SPACE:
                    if selection_position == (120, 410):
                        menu_play()
                    if selection_position == (350, 410):
                        menu_settings()
                    if selection_position == (250, 610):
                        pygame.quit()
        pygame.display.update()


def main():
    menu()


if __name__ == "__main__":
    main()
