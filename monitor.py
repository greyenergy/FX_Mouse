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
move_ready = True;
last_move = [cPosX,cPosY];

move_id = 0;
move_id_max = 20;

end_proc = False;

def shutdown_monitor():
	global end_proc;
	win32api.PostThreadMessage(main_thread_id, win32con.WM_QUIT, 0, 0);
	end_proc = True;
	exit();

def proc_listener():
	global cPosX;
	global cPosY;
	global posX;
	global posY;
	global command;
	global main_thread_id;
	global fxm;
	global move_ready;
	global move_id;
	global move_id_max;
	global end_proc;
	line = "";
	token = "";
	token_id = "";
	while(True):
		if(end_proc):
			return;
		# We can't do both stdin and stdout communication back and forth, so we'll just improvise with file I/O for now.
		#print "Open for reading...\n";
		
		line = "";
		token = "";
		token_id = "";
		f = open("./io/pipe","r+");
		lines = f.readlines(); # we just need the first line.
		f.close();
		if(len(lines) > 0):
			try:
				line = lines[0];
				ss = line.split(" ");
				token = ss[0];
				token_id = int(ss[1]);
			except:
				pass;
		#print "reading complete: "+str(line)+"\n";
		if((token.lower() == "moved") and (token_id == move_id)):
			#print "move match: "+str(token_id)+"\n";
			move_id += 1;
			if(move_id >= move_id_max):
				move_id = 0;
			move_ready = True;
			# Clear the file.
			
		time.sleep(0.05); # sleep is in seconds.

def hook_monitor():
	global cPosX;
	global cPosY;
	global posX;
	global posY;
	global command;
	global main_thread_id;
	global fxm;
	global move_ready;
	global last_move;
	global move_id;
	global move_id_max;
	global end_proc;
	just_moved = False;
	while(True):
		just_moved = False;
		time.sleep(0.05);
		# Update position
		if((cPosX != posX) or (cPosY != posY)):
			cPosX = posX;
			cPosY = posY;
			last_move[0] = cPosX;
			last_move[1] = cPosY;
			just_moved = True;
		# If java side is ready for the latest mouse position, send it and wait for java to notify it's ready for the next position.
		if(((move_ready) and ((cPosX != last_move[0]) or (cPosY != last_move[1]))) or (just_moved and move_ready)):
			try:
				fxm.stdin.write("move " + str(last_move[0])+" "+str(last_move[1])+" "+str(move_id)+"\n");
				#print "WRITE: move " + str(last_move[0])+" "+str(last_move[1])+" "+str(move_id)+"\n";
				#move_ready = False;
			except:
				shutdown_monitor();
		if(command != 0):
			try:
				if(command == 1):
					fxm.stdin.write("focus "+cmd_map[command]+"\n");
				elif(command == 2):
					fxm.stdin.write("hide "+cmd_map[command]+"\n");
			except:
				shutdown_monitor();
			command = 0;
		if(fxm.stdin.closed):
			shutdown_monitor();
	shutdown_monitor();

Thread(target=hook_monitor).start();
Thread(target=proc_listener).start();

pythoncom.PumpMessages()

