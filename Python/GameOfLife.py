import time
import pygame 
COLOR = (255, 255, 255) 
X, Y = 53, 28
Scale = 20
W, H = X * Scale * 2, Y * Scale * 2
lamppost = pygame.image.load('C:\\Users\\pcpg\\Desktop\\Android9\\UWB_DB\\Python\\lamppost.png')
car = pygame.image.load('C:\\Users\\pcpg\\Desktop\\Android9\\UWB_DB\\Python\\car.png')
lamppost = pygame.transform.scale(lamppost, (60  * Scale / 10, 60  * Scale / 10))
car = pygame.transform.scale(car, (60  * Scale / 10, 60  * Scale / 10))
LamppostList = [(17, 11), (46, 17)]
v = (10, 10)
Directions = [()]
pygame.init()
screen = pygame.display.set_mode((W, H))
screen.fill(COLOR)

while True:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            quit()
    for l in LamppostList:
        sq = pygame.Surface((20, 20))
        sq.fill((0, 0, 255))
        screen.blit(sq, (l[0] * Scale * 2, (Y - l[1]) * Scale * 2))
        screen.blit(lamppost, (l[0] * Scale * 2 - 22, (Y - l[1]) * Scale * 2 - 85))
    for v in VehicleList:
        sq = pygame.Surface((20, 20))
        sq.fill((255, 0, 0))
        screen.blit(sq, (v[0] * Scale * 2, (Y - v[1]) * Scale * 2))
        screen.blit(car, (v[0] * Scale * 2 - 10, (Y - v[1]) * Scale * 2 - 10))
    pygame.display.flip()
    # pygame.display.update()
    print(screen.get_size())
    time.sleep(2)