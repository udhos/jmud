@echo off
rem
rem wmake.bat
rem
rem Por: Fabricio Matheus Goncalves
rem
rem $Id: wmake.bat,v 1.1.1.1 2001/11/14 09:01:45 fmatheus Exp $

REM === Inicio Do Setup ===
rem voce pode ter que editar as proximas variaveis para corresponder a sua
rem intalacao do pacote java (JDK) que pode ser obtido em http://java.sun.com

set JDK=C:\JDK1.2.2
set JC=javac.exe
set VM=java.exe
set JAR=jar.exe

rem caminho completo para o compilador
set JC=%JDK%\bin\%JC%

rem caminho completo para a maquina virtual
set VM=%JDK%\bin\%VM%

rem caminho completo para o jar soh eh necessario se vc quiser o jmud.jar
set JAR=%JDK%\bin\%JAR%
REM === Fim do Setup ===



rem guarda o valor antigo do classpath para ser restaurado
set CLASSPATHTMP=%CLASSPATH%

rem ajusta a variavel de ambiente classpath
set CLASSPATH=.;java_cup_runtime.jar;%CLASSPATH%

rem verifica se o jdk esta instalado
if not exist %JDK%\NUL goto SETUP1


rem parse das opcoes
if "%1"=="/?" GOTO USAGE
if "%1"=="" GOTO BUILD
if "%1"=="build" GOTO BUILD
if "%1"=="run" GOTO RUN
if "%1"=="jar" GOTO JAR
if "%1"=="runjar" GOTO RUNJAR
if "%1"=="clean" GOTO CLEAN
if "%1"=="bug" GOTO BUGREPORT
GOTO USAGE



:BUILD
if not exist %JC% goto SETUP2
rem cria os diretorios necessarios se nao existem
if not exist classes\nul md classes

echo Compilando o JMud. Por favor, aguarde.
echo Usando o CLASSPATH = %CLASSPATH%

%JC% -d classes src\JMud.java
pause

if errorlevel 3 echo Erro 3
if errorlevel 2 goto BERROR2
if errorlevel 1 goto BERROR1
if errorlevel 0 goto AFTERBUILD
GOTO EXIT

:RUN
if not exist %VM% goto SETUP3

rem cria os diretorios necessarios se nao existem
if not exist rec\usu\nul md rec\usu
if not exist rec\usu\per\nul md rec\usu\per
if not exist rec\usu\ite\nul md rec\usu\ite

echo Ao receber a mensagem "Interpretador indo dormir"
echo Em outra janela digite: telnet localhost 1234
pause
%VM% -cp CLASSES;java_cup_runtime.jar src.JMud %2 %3 %4 %5 %6 %7


if errorlevel 3 goto RERROR3
if errorlevel 2 echo Erro 2
if errorlevel 1 goto RERROR1
if errorlevel 0 goto EXIT

goto EXIT

:JAR
if not exist %JAR% goto SETUP4
if exist jmud.jar GOTO JARDONE
if not exist classes\src\JMud.class GOTO RERROR1
cd classes

if exist java_cup\runtime\Scanner.class goto CUPOK
%JAR% xvf ..\java_cup_runtime.jar java_cup
:CUPOK

%JAR% cvfm ..\jmud.jar ..\meta-inf\manifest.mf .
cd ..
:JARDONE
echo jmud.jar pronto. Agora: "wmake runjar" ou "java -jar jmud.jar"
GOTO END

:RUNJAR
if not exist %VM% goto SETUP3

rem cria os diretorios necessarios se nao existem
if not exist rec\usu\nul md rec\usu
if not exist rec\usu\per\nul md rec\usu\per
if not exist rec\usu\ite\nul md rec\usu\ite

if not exist jmud.jar GOTO JERROR
echo Ao receber a mensagem "Interpretador indo dormir"
echo Em outra janela digite: telnet localhost 1234
pause
%VM% -jar jmud.jar %2 %3 %4 %5 %6 %7

if errorlevel 3 goto RERROR3
if errorlevel 2 echo Erro 2
if errorlevel 1 goto RERROR1
if errorlevel 0 goto EXIT
goto EXIT

:CLEAN
if exist classes\NUL deltree /Y classes
if exist jmud.jar del jmud.jar
GOTO END

:JERROR
echo jmud.jar nao foi encontrado. Provavelmente falta fazer: wmake jar
GOTO END

:RERROR3
echo o JMud foi Abortado.
GOTO END

:RERROR1
echo JMud ou uma de suas classes nao foram encontradas.
echo Provavelmente falta fazer: wmake build
GOTO END

:AFTERBUILD
echo Compilado com Sucesso. Agora: "wmake run" ou "wmake jar"
GOTO END

:BERROR1
echo Ocorreu um erro durante a compilacao. Por favor, verifique o CLASSPATH.
if not "%CLASSPATHTMP%"=="" GOTO END
echo Obs:  se a versao do seu jdk e inferior a 1.2 a variavel de ambiente
echo       CLASSPATH deveria ser igual ao caminho das bibliotecas de java
echo Ex:   set CLASSPATH=C:\JDK1.1.8\LIB\CLASSES.ZIP
echo Veja: documentacao que acompanha o jdk ou instale uma versao mais recente
GOTO END

:BERROR2
echo .
echo Ocorreu um erro inesperado na compilacao. Por favor, certifique-se que
echo descompactou todos os arquivos da distribuicao do JMUD.
GOTO BUGREPORT

:USAGE
echo wmake [opcao]
echo Opcoes:
echo   build  - compila o jmud   (default)
echo   run    - inicia o teste do jmud (apos: wmake build)
echo   jar    - cria o jmud.jar  (apos: wmake build)
echo   runjar - testa o jmud.jar (apos: wmake jar)
echo   clean  - apaga os .class e o jmud.jar
echo   bug    - como informar um possivel bug
echo Obs:
echo   Voce pode ter que alterar as primeiras linhas do wmake.bat
echo   dependendo da sua versao do jdk.
goto END

:BUGREPORT
echo Correcao de Problemas:
echo Se voce acredita ter encontrado um erro nessa distribuicao, por favor,
echo envie um email para fmatheus@users.sourceforge.net, descrevendo o problema
echo bem como o seu sistema operacional (Win95/98/NT) a versao do seu JDK, e se
echo possivel as messagens geradas pelo programa antes do erro acontecer.
GOTO EXIT

:SETUP1
echo Voce deve editar o arquivo wmake.bat para que a variavel JDK corresponda
echo ao diretorio onde seu JDK esta instalado. Ex: set JDK=C:\JDK1.2
GOTO END
:SETUP2
echo %JC%  - Que compila os arquivos .java nao foi encontrado.
echo Voce deve editar o inicio do wmake.bat de acordo com sua intalacao do JDK
GOTO END
:SETUP3
echo %VM%  - Que executa o JMud nao foi encontrado.
echo Voce deve editar o inicio do wmake.bat de acordo com sua intalacao do JDK
GOTO END
:SETUP4
echo %JAR%  - Que cria o jmud.jar a partir dos .class nao foi encontrado.
echo Voce deve editar o inicio do wmake.bat de acordo com sua intalacao do JDK
GOTO END


:EXIT
echo Obrigado por testar o JMud.

:END
set CLASSPATH=%CLASSPATHTMP%
set CLASSPATHTMP=
set JDK=
set JC=
set VM=
set JAR=
