package fr.aquillet.kiwi.jna.struct;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.POINT;

public class MSLLHOOKSTRUCT extends Structure {

	public POINT pt;
	public DWORD mouseData;
	public DWORD flags;
	public DWORD time;
	public ULONG_PTR dwExtraInfo;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("pt", "mouseData", "flags", "time", "dwExtraInfo");
	}

}
