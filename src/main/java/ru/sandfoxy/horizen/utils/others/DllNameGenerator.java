package ru.sandfoxy.horizen.utils.others;

import java.util.List;

public class DllNameGenerator {
    private static final List<String> dllNames = List.of(
            "cryptonetobj.dll",
            "cryptonet.dll",
            "DXEngine.dll",
            "dbghelper.dll", 
            "enginedebug.dll",
            "glhandler.dll",
            "opennet.dll",
            "WinType.dll",
            "curlnet.dll",
            "opensslcrypt.dll",
            "comctl64.dll",
            "jniimage.dll",
            "jitcompiler.dll",
            "opengldbg.dll",
            "d3d11.dll",
            "d3dcompiler_47.dll",
            "xinput1_4.dll",
            "ucrtbase.dll",
            "msvcp140.dll",
            "vcruntime140.dll",
            "kernel32.dll",
            "user32.dll",
            "shell32.dll",
            "advapi32.dll",
            "gdi32.dll",
            "ole32.dll",
            "winmm.dll",
            "dxgi.dll",
            "vulkan-1.dll",
            "nvapi64.dll",
            "amdocl64.dll",
            "atiadlxx.dll",
            "opengl32.dll",
            "msvcr120.dll",
            "mfc140u.dll"
    );

    public static String get(){
        return dllNames.get((int)(Math.random() * dllNames.size()));
    }
}
