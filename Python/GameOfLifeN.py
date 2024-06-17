import time
import pygame 
COLOR = (255, 255, 255) 
X, Y = 14, 10 # 53, 28
Scale = 20
W, H = X * Scale * 2, Y * Scale * 2
lamppost = pygame.image.load('C:\\Users\\pcpg\\Desktop\\Android9\\UWB_DB\\Python\\lamppost.png')
car = pygame.image.load('C:\\Users\\pcpg\\Desktop\\Android9\\UWB_DB\\Python\\car.png')
lamppost = pygame.transform.scale(lamppost, (60  * Scale / 10, 60  * Scale / 10))
car = pygame.transform.scale(car, (60  * Scale / 10, 60  * Scale / 10))
LamppostList = [(2, 5), (10, 5)]
v = (10, 2)
#Direction, Steps, StepSize || StepSize takes LCM with Steps since implementation is temporary
Directions = [('i', 2
               , None), ('l', 4, 2), ('i', 1, None), ('l', 4, 2)]
Moving = True
Dir, Steps, Index, StepSize = 0, 0, -1, None
TickTime = 1
pygame.init()
screen = pygame.display.set_mode((W, H))
# screen.fill(COLOR)

while True:
    screen.fill(COLOR)
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            quit()
    for l in LamppostList:
        sq = pygame.Surface((20  * Scale / 10, 20  * Scale / 10))
        sq.fill((0, 0, 255))
        screen.blit(sq, (l[0] * Scale * 2, (Y - l[1]) * Scale * 2))
        screen.blit(lamppost, ((l[0] - 0.5)* Scale * 2 , (Y - l[1] - 2.4) * Scale * 2))
    if Moving:
        if(Steps <= 0):
            Index += 1
            if(Index >= len(Directions)):
                Moving = False
                continue
            Dir = Directions[Index][0]
            Steps = Directions[Index][1]
            StepSize = Directions[Index][2]
        if StepSize == None:
            StepSize = 1
        for i in range(StepSize):
            Steps -= 1
            if Dir == 'r':
                v = (v[0] + 1, v[1])
            elif Dir == 'l':
                v = (v[0] - 1, v[1])
            elif Dir == 'u':
                v = (v[0], v[1] + 1)
            elif Dir == 'd':
                v = (v[0], v[1] - 1)
            elif Dir == 'i':
                pass
    sq = pygame.Surface((20  * Scale / 10, 20  * Scale / 10))
    sq.fill((255, 0, 0))
    screen.blit(sq, (v[0] * Scale * 2, (Y - v[1]) * Scale * 2))
    # if Dir == 'l': 
    tmpcar = pygame.transform.flip(car, True, False)
    # else: tmpcar = car
    screen.blit(tmpcar, ((v[0] - 1)* Scale * 2, (Y - v[1] - 1) * Scale * 2 ))
    # pygame.display.flip()
    pygame.display.update()
    # print(screen.get_size())
    pygame.time.wait(TickTime * 1000)