var WshShell = new ActiveXObject("WScript.Shell")
var strCCKhome=WshShell.ExpandEnvironmentStrings("%CCK_HOME%");
var strPath;
var RegKey="HKCU\\Environment\\PATH";
try{
strPath=WshShell.RegRead(RegKey);
WshShell.RegWrite(RegKey, strPath + ";%CCK_HOME%\\bin","REG_EXPAND_SZ");
} catch (err) {
WshShell.RegWrite(RegKey,"%CCK_HOME%\\bin");
}
WshShell.RegWrite("HKCU\\Environment\\CCK_HOME",strCCKhome);
WshShell.RegWrite("HKCU\\Environment\\ANT_HOME",strCCKhome);
var strDesktop=WshShell.SpecialFolders("Desktop")
var oShellLink = WshShell.CreateShortcut(strDesktop+"\\JFlex.lnk");
oShellLink.TargetPath=strCCKhome+"\\lib\\JFlex.jar";
oShellLink.IconLocation="%SystemRoot%\\system32\\Shell32.dll,20";
oShellLink.save();
