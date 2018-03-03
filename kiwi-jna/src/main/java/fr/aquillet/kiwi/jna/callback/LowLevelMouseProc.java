package fr.aquillet.kiwi.jna.callback;

import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;

import fr.aquillet.kiwi.jna.struct.MSLLHOOKSTRUCT;

public interface LowLevelMouseProc extends HOOKPROC { 
	
	LRESULT callback(int nCode, WPARAM wParam, MSLLHOOKSTRUCT info);

}
