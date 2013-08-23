import os;
import pywinauto;
from pywinauto import application;
from pywinauto import findwindows;

import subprocess;
import threading;
from threading import Thread;
import pyHook;
import gc;
import time;

import pythoncom
import win32con
import win32api

main_thread_id = win32api.GetCurrentThreadId()

hook_manager = pyHook.HookManager();

cPosX = 0;
cPosY = 0;
posX = 0;
posY = 0;
command = 0;

def MouseEventFire(event):
	global record
	global loopOn #if recording, copy down mouse action's name, time, position, and wheel movement (if any)
	global posX;
	global posY;

	posX = int(event.Position[0]);
	posY = int(event.Position[1]);

	#fileRecord = open(title)
	#mThread = MouseThread(event.MessageName,event.Time,event.Position,event.Wheel)
	#mThread.start()

	#stringInput = str(event.MessageName) + ' Time ' + str(event.Time) +' Position ' + str(event.Position) + ' Wheel '+ str(event.Wheel) + "\n"
	#fileRecord.write(stringInput)
	#fileRecord.close
	# returning True will pass the event to other handlers.
	return True

hook_manager.MouseAll = MouseEventFire;
hook_manager.HookMouse();

# Run the FX_Mouse program
fxm = subprocess.Popen(['java','-cp',"\".;E:\Program Files\Oracle\JavaFX 2.2 Runtime\lib\jfxrt.jar\"",'FX_Mouse'],shell=True,stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=subprocess.STDOUT);

cmd_map = ["","focus","hide"];

def monitor():
	global cPosX;
	global cPosY;
	global posX;
	global posY;
	global command;
	global main_thread_id;

	while(True):
		if((cPosX != posX) or (cPosY != posY)):
			cPosX = posX;
			cPosY = posY;
			try:
				fxm.stdin.write("move " + str(cPosX)+" "+str(cPosY)+"\n");
			except:
				win32api.PostThreadMessage(main_thread_id, win32con.WM_QUIT, 0, 0);
				exit();
		if(command != 0):
			try:
				if(command == 1):
					fxm.stdin.write("focus "+cmd_map[command]+"\n");
				elif(command == 2):
					fxm.stdin.write("hide "+cmd_map[command]+"\n");
			except:
				win32api.PostThreadMessage(main_thread_id, win32con.WM_QUIT, 0, 0);
				exit();
			command = 0;
		if(fxm.stdin.closed):
			win32api.PostThreadMessage(main_thread_id, win32con.WM_QUIT, 0, 0);
			exit();
	win32api.PostThreadMessage(main_thread_id, win32con.WM_QUIT, 0, 0);
	exit();

Thread(target=monitor).start();

pythoncom.PumpMessages()

