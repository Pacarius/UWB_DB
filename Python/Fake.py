import math
from matplotlib.offsetbox import AnnotationBbox, OffsetImage
import matplotlib.pyplot as plt
import matplotlib.patches as patches
from PIL import Image
import numpy as np

Scale = 1
X, Y = 53, 28
im = Image.new('RGB', (X * 10, Y * 10), (255, 255, 255))
x = np.linspace(0,10, 10)
y = [math.sin(i) for i in x]
fig = plt.figure()
ax = fig.add_subplot(111)
ax.imshow(im)
lp = OffsetImage(plt.imread('113782.png'), zoom = 10)
a = [] 
for px, py in zip(x,y):
    box = AnnotationBbox(lp, (px, py), frameon=False)
    a.append(ax.add_artist(box))
ax.plot(x,y,'r--')
plt.show()