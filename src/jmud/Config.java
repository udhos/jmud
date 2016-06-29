/*-GNU-GPL-BEGIN-*
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*-GNU-GPL-END-*/

package jmud;

import java.util.StringTokenizer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;

import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.Separators;
import jmud.util.StrUtil;

public class Config {

    static private String meta_startZone           = "ZonaInicial";
    static private String meta_startRoom           = "SalaInicial";
    static private String meta_worldDir            = "DiretorioDeMundo";
    static private String meta_worldFileName       = "ArquivoDeMundo";
    static private String meta_helpFileName        = "ArquivoDeAjuda";
    static private String meta_stateFileName       = "ArquivoDeEstado";
    static private String meta_userFilesDir        = "DiretorioDeUsuarios";
    static private String meta_itemFilesDir        = "DiretorioDePosses";
    static private String meta_boardFilesDir       = "DiretorioDeQuadros";
    static private String meta_maxSpam             = "MaximoDeAbuso";
    static private String meta_motdFileName        = "ArquivoDeMensagemDoDia";
    static private String meta_maxClients          = "MaximoDeClientes";
    static private String meta_subversionFileName  = "ArquivoDeSubversao";
    static private String meta_syntaxFileName      = "ArquivoDeSintaxe";
    static private String meta_abilityDepFileName  = "ArquivoDeDependenciaDeHabilidades";
    static private String meta_abilityJobsFileName = "ArquivoDeProfissoesDasHabilidades";

    private String startZone           = null;
    private String startRoom           = null;
    private String worldDir            = null;
    private String worldFileName       = null;
    private String helpFileName        = null;
    private String stateFileName       = null;
    private String userFilesDir        = null;
    private String itemFilesDir        = null;
    private String boardFilesDir       = null;
    private String maxSpam             = null;
    private String motdFileName        = null;
    private String maxClients          = null;
    private String subversionFileName  = null;
    private String syntaxFileName      = null;
    private String abilityDepFileName  = null;
    private String abilityJobsFileName = null;

    private String fileName;

  private String checkNewMeta(String oldInfo, String newInfo, int line)
    throws InvalidFileFormatException {
    if (oldInfo != null)
      throw new InvalidFileFormatException(fileName, newInfo, "colisão", "?", line);
    return newInfo;
  }

  private void checkNullInfo(String info, String meta)
    throws InvalidFileFormatException {
      if (info == null)
	throw new InvalidFileFormatException(fileName, meta, "indefinição", "?", -1);
  }

  private int ensureNumericValue(String info)
    throws InvalidFileFormatException {
    try {
      return Integer.parseInt(info);
    }
    catch(NumberFormatException e) {
      throw new InvalidFileFormatException();
    }
  }

  Config(String configFileName)
    throws FileNotFoundException, IOException, InvalidFileFormatException {

      fileName = configFileName;

      LineReader configFile = new LineReader(new FileReader(configFileName));

      for (; ; ) {

	if (configFile.eos())
	  throw new InvalidFileFormatException();

	String buf = configFile.readLine().trim();
	int line = configFile.getLineNumber();
	if (buf.length() == 0)
	  continue;
	StringTokenizer toker = new StringTokenizer(buf);
	if (!toker.hasMoreTokens())
	  continue;
	String meta = toker.nextToken();
	if (meta.startsWith(Separators.REM))
	  continue;
	if (meta.startsWith(Separators.EOF))
	  break;
	if (!toker.hasMoreTokens())
	  throw new InvalidFileFormatException();
	String info = toker.nextToken();

	if (meta.equals(meta_startZone))
	    startZone = checkNewMeta(startZone, info, line);
	else if (meta.equals(meta_startRoom))
	  startRoom = checkNewMeta(startRoom, info, line);
	else if (meta.equals(meta_worldDir))
	  worldDir = checkNewMeta(worldDir, info, line);
	else if (meta.equals(meta_worldFileName))
	  worldFileName = checkNewMeta(worldFileName, info, line);
	else if (meta.equals(meta_helpFileName))
	  helpFileName = checkNewMeta(helpFileName, info, line);
	else if (meta.equals(meta_stateFileName))
	  stateFileName = checkNewMeta(stateFileName, info, line);
	else if (meta.equals(meta_userFilesDir))
	  userFilesDir = checkNewMeta(userFilesDir, info, line);
	else if (meta.equals(meta_itemFilesDir))
	    itemFilesDir = checkNewMeta(itemFilesDir, info, line);
	else if (meta.equals(meta_boardFilesDir))
	    boardFilesDir = checkNewMeta(boardFilesDir, info, line);
	else if (meta.equals(meta_maxSpam))
	  maxSpam = checkNewMeta(maxSpam, info, line);
	else if (meta.equals(meta_motdFileName))
	  motdFileName = checkNewMeta(motdFileName, info, line);
	else if (meta.equals(meta_maxClients))
	  maxClients = checkNewMeta(maxClients, info, line);
	else if (meta.equals(meta_subversionFileName))
	  subversionFileName = checkNewMeta(subversionFileName, info, line);
	else if (meta.equals(meta_syntaxFileName))
	  syntaxFileName = checkNewMeta(syntaxFileName, info, line);
	else if (meta.equals(meta_abilityDepFileName))
	  abilityDepFileName = checkNewMeta(abilityDepFileName, info, line);
	else if (meta.equals(meta_abilityJobsFileName))
	  abilityJobsFileName = checkNewMeta(abilityJobsFileName, info, line);
	else
	  throw new InvalidFileFormatException();
      }

      configFile.close();

      checkNullInfo(startZone, meta_startZone);
      checkNullInfo(startRoom, meta_startRoom);
      checkNullInfo(worldDir, meta_worldDir);
      checkNullInfo(worldFileName, meta_worldFileName);
      checkNullInfo(helpFileName, meta_helpFileName);
      checkNullInfo(stateFileName, meta_stateFileName);
      checkNullInfo(userFilesDir, meta_userFilesDir);
      checkNullInfo(itemFilesDir, meta_itemFilesDir);
      checkNullInfo(boardFilesDir, meta_boardFilesDir);
      checkNullInfo(maxSpam, meta_maxSpam);
      checkNullInfo(motdFileName, meta_motdFileName);
      checkNullInfo(maxClients, meta_maxClients);
      checkNullInfo(syntaxFileName, meta_syntaxFileName);
      checkNullInfo(abilityDepFileName, meta_abilityDepFileName);
      checkNullInfo(abilityJobsFileName, meta_abilityJobsFileName);

      ensureNumericValue(startZone);
      ensureNumericValue(startRoom);
      ensureNumericValue(maxSpam);
      ensureNumericValue(maxClients);

      worldDir      = StrUtil.ensureTrailingSlash(worldDir);
      userFilesDir  = StrUtil.ensureTrailingSlash(userFilesDir);
      itemFilesDir  = StrUtil.ensureTrailingSlash(itemFilesDir);
      boardFilesDir = StrUtil.ensureTrailingSlash(boardFilesDir);
  }

    int getStartZone()              { return Integer.parseInt(startZone); }
    int getStartRoom()              { return Integer.parseInt(startRoom); }
    String getWorldDir()            { return worldDir; }
    String getWorldFileName()       { return worldFileName; }
    String getHelpFileName()        { return helpFileName; }
    String getStateFileName()       { return stateFileName; }
    String getUserFilesDir()        { return userFilesDir; }
    String getItemFilesDir()        { return itemFilesDir; }
    String getBoardFilesDir()       { return boardFilesDir; }
    int getMaxSpam()                { return Integer.parseInt(maxSpam); }
    String getMotdFileName()        { return motdFileName; }
    int getMaxClients()             { return Integer.parseInt(maxClients); }
    String getSubversionFileName()  { return subversionFileName; }
    public String getSyntaxFileName()      { return syntaxFileName; }
    public String getAbilityDepFileName()  { return abilityDepFileName; }
    public String getAbilityJobsFileName() { return abilityJobsFileName; }
}
