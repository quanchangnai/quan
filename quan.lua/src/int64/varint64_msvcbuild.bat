
@rem First change the paths to your Lua installation below.
@rem Then open a "Visual Studio Command Prompt", cd to this directory
@rem and run this script. Afterwards copy the resulting varint64.dll to
@rem the directory where lua.exe is installed.

@if not defined INCLUDE goto :FAIL

@setlocal
@rem Path to the Lua includes and the library file for the Lua DLL:
@set LUA_INC=C:\Users\admin\Downloads\LuaJIT-2.0.5\src
@set LUA_LIB=%LUA_INC%\lua51.lib

@set MYCOMPILE=cl /nologo /MD /O2 /W3 /c -I %LUA_INC%
@set MYLINK=link /nologo
@set MYMT=mt /nologo

%MYCOMPILE% varint64.c longutils.c
%MYLINK% /DLL /export:luaopen_varint64 /out:varint64.dll *.obj  %LUA_LIB%
if exist varint64.dll.manifest^
  %MYMT% -manifest varint64.dll.manifest -outputresource:varint64.dll;2

del *.obj *.exp *.manifest

@goto :END
:FAIL
@echo You must open a "Visual Studio Command Prompt" to run this script
:END
