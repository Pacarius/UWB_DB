import tkinter as tk
import requests
import json
class PlotObject:
    def __init__(self, type):
        self.type = type
    def color():
        if type == "L":
            return "blue"
        if type == "V":
            return "red"
        if type == None:
            return "white"
Plot = [[PlotObject(None) for i in range(28)] for j in range(53)]
def Update():
    Lampposts = requests.get("http://localhost:9696/lampposts")
    Vehicles = requests.get("http://localhost:9696/vehicles")
    L = json.loads(Lampposts.text)
    for i in L:
        if i['x'] < 53 and i['y'] < 28:
            Plot[round(i['x'])][round(i['y'])]= PlotObject("L")
    V = json.loads(Vehicles.text)
    for i in V:
        if i['x'] < 53 and i['y'] < 28:
            Plot[round(i['x'])][round(i['y'])] = PlotObject("V")
    for i in range(53):
        for j in range(28):
            if Plot[i][j].type == "L":
                canvas.create_rectangle(i*10,j*10,i*10+10,j*10+10,fill="blue")
            if Plot[i][j].type == "V":
                canvas.create_rectangle(i*10,j*10,i*10+10,j*10+10,fill="red")
c = None
def create_grid(event=None):
    w = c.winfo_width() # Get current width of canvas
    h = c.winfo_height() # Get current height of canvas
    c.delete('grid_line') # Will only remove the grid_line

    # Creates all vertical lines at intevals of 100
    for i in range(0, w, round(w/53)):
        c.create_line([(i, 0), (i, h)], tag='grid_line')

    # Creates all horizontal lines at intevals of 100
    for i in range(0, h, round(h/28)):
        c.create_line([(0, i), (w, i)], tag='grid_line')

def Draw():
    root = tk.Tk()
    canvas = tk.Canvas(root, height=280, width=530, bg='white')
    global c
    c = canvas
    root.bind('<Configure>', create_grid)
    root.mainloop()
    return

Draw()