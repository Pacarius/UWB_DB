import matplotlib.pyplot as plt
import matplotlib.patches as patches
from PIL import Image
import requests
import json
print(plt.get_backend())
class PlotObject:
    def __init__(self, x, y):
        self.x = x
        self.y = y
LamppostList = []
VehicleList = []
Scale = 1
X, Y = 53, 28
def Update():
    global LamppostList, VehicleList
    VehicleList.clear()
    Lampposts = requests.get("http://localhost:9696/lampposts")
    Vehicles = requests.get("http://localhost:9696/vehicles")
    L = json.loads(Lampposts.text)
    for i in L:
        if i['x'] < X * Scale and i['y'] < Y * Scale:
            LamppostList.append(PlotObject(round(i['x'] * Scale), round(i['y'] * Scale)))
    V = json.loads(Vehicles.text)
    for i in V:
        if i['x'] < X * Scale and i['y'] < Y * Scale:
            VehicleList.append(PlotObject(round(i['x'] * Scale), round(i['y'] * Scale)))
# Update()
# im = Image.new('RGB', (530, 280), (255, 255, 255))
# fig, ax = plt.subplots()
# ax.imshow(im)
# for v in VehicleList:
#     ax.add_patch(patches.Rectangle((v.x*10, v.y*10), 10, 10, color='red'))
# for l in LamppostList:
#     ax.add_patch(patches.Rectangle((l.x*10, l.y*10), 10, 10, color='blue'))
# plt.show()
fig = plt.figure()
ax = fig.add_subplot(111)
fig.show()
im = Image.new('RGB', (X * 10, Y * 10), (255, 255, 255))
while True:
    Update()
    ax.clear()
    ax.imshow(im)
    for v in VehicleList:
        ax.add_patch(patches.Rectangle((v.x*10/Scale, v.y*10/Scale), 10, 10, color='red'))
    for l in LamppostList:
        ax.add_patch(patches.Rectangle((l.x*10/Scale, l.y*10/Scale), 10, 10, color='blue'))
    fig.canvas.draw()
    # fig.canvas.flush_events()
    try:
        plt.pause(1)
        continue
    finally:
        continue
    
