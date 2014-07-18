#########################################################################################################################################
#    "C:\WINDOWS\system32\WindowsPowerShell\v1.0\powershell.exe" -noexit -File "%1" 
#    "C:\WINDOWS\system32\WindowsPowerShell\v1.0\powershell.exe" -command "Set-ExecutionPolicy RemoteSigned"
#########################################################################################################################################

mode con cols=120 lines=60

###################### Save allPath ###################################

$Project_name = "haminium-connector"
$Setup_dir = [System.IO.Directory]::GetCurrentDirectory() + "\"
$Project_dir = (get-item $Setup_dir).parent.fullname + "\"
$VersionFile_path = $Sources_dir + "install.rdf"

set-alias cmd-7zip "C:\Program Files\7-Zip\7z.exe"
set-alias cmd-firefox "C:\Program Files (x86)\Mozilla Firefox\firefox.exe"

function getVersion($version){
    $new_version =""
    while ($new_version -eq ""){
        $input = read-host "   Digit to increment [x.y.z] or version [0.0.0] or skip [s] ? "
        if ($input -match "s|z|y|x") {
            $version_digit = $version -split "\."
            switch ($input){
                "s" { $new_version = $version }
                "z" { $new_version = $version_digit[0] + "." + $version_digit[1] + "." + [string]([int]$version_digit[2]+1) }
                "y" { $new_version = $version_digit[0] + "." + [string]([int]$version_digit[1]+1) + ".0" }
                "x" { $new_version = [string]([int]$version_digit[0]+1) + ".0.0" }
            }
        }else{
            if ($input -match "\d+\.\d+\.\d+") { $new_version = $input }
        }
    }
    return $new_version
}

function getSha1($filepath){
	write-host $filepath
    [String]$sha1 =""
    $sha1o = New-Object System.Security.Cryptography.SHA1CryptoServiceProvider
    $file = [System.IO.File]::Open($filepath, "open", "read")
    $sha1o.ComputeHash($file)| %{ $sha1 += $_.ToString("x2") }
    $file.Close()
    return $sha1
}

write-host ""
write-host " --------------------------------- $Project_name -------------------------------------------"

write-host ""
write-host "        > Manual       ** Automatic"
write-host ""

#Check folders and files
(gi Env:PATH).value.split(";")| ForEach {if(!(test-path $_)){write-host("  Error ENV:PATH : Folder " + $_ + """ not found" ) -ForegroundColor Red;}}
Get-Variable -name *_dir | ForEach {if(!(test-path $_.Value)){write-host("  Error : Folder " + $_.Name + "=""" + $_.Value + """ not found") -ForegroundColor Red;}}
Get-Variable -name *_path | ForEach {if(!(test-path $_.Value)){write-host("  Error : File " + $_.Name + "=""" + $_.Value + """ not found") -ForegroundColor Red;}}
Get-Alias -name cmd-* | ForEach {if(!(test-path $_.Definition)){write-host("  Error : Program " + $_.Name + "=""" + $_.Definition + """ not found") -ForegroundColor Red;}}

#Get the version from update.rdf and subversion URL
$VersionFile_txt = (([regex]::matches((get-content $VersionFile_path), "<em:version>(\d+\.\d+\.\d+)<"))[0]).Groups[1].Value;

#Get last compilation date
$LastCompil_date = if ((test-path($VersionFile_path)) -eq 1){(get-item( $VersionFile_path )).LastWriteTime}

#Write information on the screen
write-host " __________________________________________________________________________________________________"
write-host ""
write-host "  Project name    : "  $Project_name
write-host "  Directory       : "  $Project_dir
write-host "  Current Version : "  $VersionFile_txt
write-host "  Last creation   : "  $LastCompil_date
write-host " __________________________________________________________________________________________________"
write-host ""


write-host " 1-Create package :"
	$ZipInclude_list= @("chrome\*.*","chrome.manifest","install.rdf")
	$ZipExclude_list= @()
	$OutputXpi = $Project_name + "-" + $VersionFile_txt + ".xpi"
    write-host "   ** Create the package $OutputXpi ..."
    	if(test-path($OutputXpi)){ Remove-Item $OutputXpi; }
    	cmd-7zip a $OutputXpi  -tzip -r ($ZipInclude_list|ForEach{$_}) ($ZipExclude_list|ForEach{"-x!"+$_}) |  out-Null
        if($LASTEXITCODE -eq 1) { write-host("  Package creation failed ! ") -ForegroundColor red; break; }
        
    write-host ""
 	
write-host " 2-Install in firefox :"
	write-host "   ** Launching Firefox for installation..."
	cmd-firefox $OutputXpi
exit