rem Create a junction in C:\dev
SET JUNCTION=ThirdParty\Tools\Windows\Junction.exe

%JUNCTION% -q -d C:\dev\trunk
%JUNCTION% C:\dev .
PAUSE
